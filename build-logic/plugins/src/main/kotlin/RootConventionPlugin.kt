import org.gradle.api.Plugin
import org.gradle.api.Project

private val buildLogicTaskPathsByRootTask =
    mapOf(
        "clean" to listOf(":plugins:clean"),
        "check" to listOf(":plugins:check"),
        "ktlintCheck" to listOf(":plugins:ktlintCheck"),
        "ktlintFormat" to listOf(":plugins:ktlintFormat"),
    )

class RootConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("base")
            pluginManager.apply("ioffeivan.ktlint")

            // CI-DEMO: пробелы вокруг равенства проверяются ktlint в build-logic.
            val buildLogic=gradle.includedBuild("build-logic")

            buildLogicTaskPathsByRootTask.forEach { (rootTaskName, buildLogicTaskPaths) ->
                val buildLogicTasks = buildLogicTaskPaths.map(buildLogic::task)

                tasks.named(rootTaskName).configure {
                    dependsOn(buildLogicTasks)
                }
            }
        }
    }
}
