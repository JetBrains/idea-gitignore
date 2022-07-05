import org.jetbrains.changelog.date
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.0"
    id("org.jetbrains.intellij") version "1.6.3"
    id("org.jetbrains.changelog") version "1.3.1"
    id("org.jetbrains.grammarkit") version "2021.2.2"
    id("org.jetbrains.qodana") version "0.1.13"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
}

// Set the JVM language level used to compile sources and generate files - Java 11 is required since 2020.3
kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
    }
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

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    headerParserRegex.set("\\[?v(\\d(?:\\.\\d+)+)\\]?.*".toRegex())
    header.set(provider {
        "[v${version.get()}] (https://github.com/JetBrains/idea-gitignore/tree/v${version.get()}) (${date()})"
    })
    version.set(properties("pluginVersion"))
    groups.set(emptyList())
}

// Configure Gradle Qodana Plugin - read more: https://github.com/JetBrains/gradle-qodana-plugin
qodana {
    cachePath.set(projectDir.resolve(".qodana").canonicalPath)
    reportPath.set(projectDir.resolve("build/reports/inspections").canonicalPath)
    saveReport.set(true)
    showReport.set(System.getenv("QODANA_SHOW_REPORT")?.toBoolean() ?: false)
}

tasks {
    // Set the JVM compatibility versions
    withType<KotlinCompile> {
        dependsOn("generateLexer", "generateParser", generateTemplatesList)
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    sourceSets {
        main {
            java.srcDirs("src/main/gen")
        }
    }

    clean {
        delete("src/main/gen")
    }

    generateLexer {
        source.set("src/main/grammars/Ignore.flex")
        targetDir.set("src/main/gen/mobi/hsz/idea/gitignore/lexer/")
        targetClass.set("IgnoreLexer")
        purgeOldFiles.set(true)
    }

    generateParser {
        source.set("src/main/grammars/Ignore.bnf")
        targetRoot.set("src/main/gen")
        pathToParser.set("/mobi/hsz/idea/gitignore/IgnoreParser.java")
        pathToPsiRoot.set("/mobi/hsz/idea/gitignore/psi")
        purgeOldFiles.set(true)
    }

    buildPlugin {
        archiveBaseName.set("ignore")
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
            projectDir.resolve("README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        )

        // Get the latest available change notes from the changelog file
        changeNotes.set(provider {
            changelog.run {
                getOrNull(properties("pluginVersion")) ?: getLatest()
            }.toHTML()
        })
    }

    runIdeForUiTests {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
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
