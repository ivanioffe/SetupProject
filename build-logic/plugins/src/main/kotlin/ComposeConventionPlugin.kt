import com.android.build.api.dsl.CommonExtension
import com.ioffeivan.setupproject.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            pluginManager.withPlugin("com.android.base") {
                extensions.configure<CommonExtension> {
                    configureCompose(this)
                }
            }
        }
    }
}

private fun Project.configureCompose(commonExtension: CommonExtension) {
    commonExtension.apply {
        buildFeatures.compose = true

        dependencies {
            "implementation"(platform(libs.findLibrary("androidx-compose-bom").get()))
            "implementation"(libs.findLibrary("androidx-compose-ui").get())
            "implementation"(libs.findLibrary("androidx-compose-ui-graphics").get())
            "implementation"(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            "implementation"(libs.findLibrary("androidx-compose-material3").get())

            "debugImplementation"(libs.findLibrary("androidx-compose-ui-test-manifest").get())
            "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())

            "androidTestImplementation"(platform(libs.findLibrary("androidx-compose-bom").get()))
            "androidTestImplementation"(libs.findLibrary("androidx-compose-ui-test-junit4").get())
        }
    }
}
