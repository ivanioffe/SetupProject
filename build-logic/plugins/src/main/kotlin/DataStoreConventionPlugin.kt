import com.ioffeivan.setupproject.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class DataStoreConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("ioffeivan.android.library")
            pluginManager.apply("ioffeivan.hilt")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                "implementation"(libs.findLibrary("androidx.datastore.preferences").get())
                "implementation"(libs.findLibrary("kotlinx.serialization.json").get())

                "androidTestImplementation"(libs.findLibrary("androidx.junit").get())
                "androidTestImplementation"(libs.findLibrary("androidx.test.runner").get())
                "androidTestImplementation"(libs.findLibrary("kotlinx.coroutines.test").get())
                "androidTestImplementation"(libs.findLibrary("truth").get())
            }
        }
    }
}
