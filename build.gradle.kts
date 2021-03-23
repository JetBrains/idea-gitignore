import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.closure
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.4.32"
    id("org.jetbrains.intellij") version "0.7.2"
    id("org.jetbrains.changelog") version "1.1.2"
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("org.jetbrains.grammarkit") version "2020.3.2"
}

// Import variables from gradle.properties file
val pluginGroup: String by project
// `pluginName_` variable ends with `_` because of the collision with Kotlin magic getter in the `intellij` closure.
// Read more about the issue: https://github.com/JetBrains/intellij-platform-plugin-template/issues/29
val pluginName_: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project
val pluginVerifierIdeVersions: String by project

val platformType: String by project
val platformVersion: String by project
val platformPlugins: String by project
val platformDownloadSources: String by project

group = pluginGroup
version = pluginVersion

// Configure project's dependencies
repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.15.0")
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
    pluginName = pluginName_
    version = platformVersion
    type = platformType
    downloadSources = platformDownloadSources.toBoolean()
    updateSinceUntilBuild = true

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    setPlugins(*platformPlugins.split(',').map(String::trim).filter(String::isNotEmpty).toTypedArray())
}

changelog {
    headerParserRegex = "\\[?v\\d(\\.\\d+)+\\]?.*".toRegex()
    version = pluginVersion
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
        version(pluginVersion)
        sinceBuild(pluginSinceBuild)
        untilBuild(pluginUntilBuild)

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription(
            closure {
                File("./README.md").readText().lines().run {
                    val start = "<!-- Plugin description -->"
                    val end = "<!-- Plugin description end -->"

                    if (!containsAll(listOf(start, end))) {
                        throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                }.joinToString("\n").run { markdownToHTML(this) }
            }
        )

        // Get the latest available change notes from the changelog file
        changeNotes(
            closure {
                changelog.getLatest().toHTML()
            }
        )
    }

    runPluginVerifier {
        ideVersions(pluginVerifierIdeVersions)
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://jetbrains.org/intellij/sdk/docs/tutorials/build_system/deployment.html#specifying-a-release-channel
        channels(pluginVersion.split('-').getOrElse(1) { "default" }.split('.').first())
    }

    // TODO: Temporary fix for https://youtrack.jetbrains.com/issue/KT-42837
    buildSearchableOptions {
        enabled = false
    }

    runIde {
        jvmArgs = listOf("-Xmx1024m", "-XX:+UnlockDiagnosticVMOptions")
        systemProperty("ide.plugins.snapshot.on.unload.fail", "true")
    }
}
