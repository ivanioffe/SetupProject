import com.ioffeivan.setupproject.catalogVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

class KtlintConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")

            extensions.configure<KtlintExtension> {
                version.set(catalogVersion("ktlintEngine"))
                relative.set(true)

                reporters {
                    reporter(ReporterType.HTML)
                }
            }
        }
    }
}
