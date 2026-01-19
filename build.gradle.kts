import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.10.5"
    id("org.jetbrains.changelog") version "2.5.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellijPlatform {
    val version = providers.gradleProperty("platformVersion")
    val type = providers.gradleProperty("platformType")

    pluginConfiguration {
        name = properties("pluginName")
    }

    buildSearchableOptions = false

    pluginVerification {
        ides {
            create(type, version) {
                useInstaller = false
                useCache = true
            }
        }
    }
}

dependencies {
    testImplementation("org.mockito:mockito-core:4.5.1")
}

dependencies {
    intellijPlatform {
        val version = providers.gradleProperty("platformVersion")
        val type = providers.gradleProperty("platformType")
        create(type, version) {
            useInstaller = false
            useCache = true
        }

        bundledPlugins("com.intellij.java", "org.jetbrains.plugins.yaml")

        compatiblePlugins(
            "com.jetbrains.php",
            "com.jetbrains.twig",
        )

        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Plugin.Java)
    }

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Configure gradle-changelog-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.set(emptyList())
    repositoryUrl.set(properties("pluginRepositoryUrl"))
}

tasks {
    test {
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
        }
    }

    // Set the JVM compatibility versions
    properties("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    patchPluginXml {
        version = properties("pluginVersion")
        sinceBuild.set(properties("pluginSinceBuild"))

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
        changeNotes.set(provider {
            changelog.renderItem(changelog.run {
                getLatest()
            }, Changelog.OutputType.HTML)
        })
    }
}
