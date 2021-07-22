import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.closure
import org.jetbrains.changelog.date
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.5.20"
    id("org.jetbrains.intellij") version "1.0"
    id("org.jetbrains.changelog") version "1.2.1"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("org.jetbrains.grammarkit") version "2021.1.3"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
}
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.17.1")
}

val generateLexer = task<GenerateLexer>("generateLexer") {
    source = "src/main/grammars/Ignore.flex"
    targetDir = "src/main/gen/mobi/hsz/idea/gitignore/lexer/"
    targetClass = "IgnoreLexer"
    purgeOldFiles = true
}

val generateParser = task<GenerateParser>("generateParser") {
    source = "src/main/grammars/Ignore.bnf"
    targetRoot = "src/main/gen"
    pathToParser = "/mobi/hsz/idea/gitignore/IgnoreParser.java"
    pathToPsiRoot = "/mobi/hsz/idea/gitignore/psi"
    purgeOldFiles = true
}

val generateTemplatesList = task("generateTemplatesList") {
    val path = "src/main/resources"
    val content = files("$path/gitignore")
        .asFileTree.matching { include("**/*.gitignore") }
        .files.joinToString("\n") { relativePath(it.path).substring(path.length + 1) }
    file("$path/templates.list").writeText(content)
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    downloadSources.set(properties("platformDownloadSources").toBoolean())
    updateSinceUntilBuild.set(true)

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

changelog {
    headerParserRegex = "\\[?v\\d(\\.\\d+)+\\]?.*".toRegex()
    header = closure {
        "[v$version] (https://github.com/JetBrains/idea-gitignore/tree/v$version) (${date()})"
    }
    version = properties("pluginVersion")
    groups = emptyList()
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"

        dependsOn(generateLexer, generateParser, generateTemplatesList)
    }

    withType<Detekt> {
        jvmTarget = "11"
    }

    sourceSets {
        main {
            java.srcDirs("src/main/gen")
        }
    }

    clean {
        delete("src/main/gen")
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
            File(projectDir, "README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        )

        // Get the latest available change notes from the changelog file
        changeNotes.set(provider { changelog.getLatest().toHTML() })
    }

    runPluginVerifier {
        ideVersions.set(properties("pluginVerifierIdeVersions").split(',').map(String::trim).filter(String::isNotEmpty))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }

    runIde {
        jvmArgs = listOf("-Xmx1024m", "-XX:+UnlockDiagnosticVMOptions")
        systemProperty("ide.plugins.snapshot.on.unload.fail", "true")
    }
}
