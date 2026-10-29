import com.ioffeivan.setupproject.libs
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("dev.detekt")

            pluginManager.withPlugin("org.jetbrains.kotlin.plugin.compose") {
                dependencies {
                    "detektPlugins"(libs.findLibrary("detekt-compose-rules").get())
                }
            }

            extensions.configure<DetektExtension> {
                parallel.set(true)
                buildUponDefaultConfig.set(true)
                config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
                basePath.set(rootDir)
                ignoredBuildTypes.set(listOf("release"))
            }

            tasks.withType<Detekt>().configureEach {
                val buildDirectory = layout.buildDirectory
                exclude {
                    it.file.startsWith(buildDirectory.get().asFile)
                }

                reports {
                    checkstyle.required.set(false)
                    html.required.set(true)
                    sarif.required.set(false)
                    markdown.required.set(false)
                }
            }

            tasks.named { it == "check" }.configureEach {
                setDependsOn(
                    dependsOn.filterNot {
                        it is TaskProvider<*> && it.name == "detekt"
                    },
                )

                dependsOn("detektMain", "detektTest")
            }
        }
    }
}
