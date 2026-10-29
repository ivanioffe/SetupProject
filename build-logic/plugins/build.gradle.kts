import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    `kotlin-dsl`
    alias(libs.plugins.ktlint)
}

val jvmTargetVersion = libs.versions.jvmTarget.get()
java {
    sourceCompatibility = JavaVersion.toVersion(jvmTargetVersion)
    targetCompatibility = JavaVersion.toVersion(jvmTargetVersion)
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(jvmTargetVersion)
    }
}

ktlint {
    version.set(libs.versions.ktlintEngine)
    relative.set(true)

    reporters {
        reporter(ReporterType.HTML)
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.plugins.detekt.asDependency())
    implementation(libs.plugins.hilt.asDependency())
    implementation(libs.plugins.kotlin.compose.asDependency())
    implementation(libs.plugins.kotlin.jvm.asDependency())
    implementation(libs.plugins.kotlin.serialization.asDependency())
    implementation(libs.plugins.ksp.asDependency())
    implementation(libs.plugins.ktlint.asDependency())
    implementation(libs.plugins.room.asDependency())
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.ioffeivan.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.ioffeivan.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("compose") {
            id = libs.plugins.ioffeivan.compose.get().pluginId
            implementationClass = "ComposeConventionPlugin"
        }
        register("datastore") {
            id = libs.plugins.ioffeivan.datastore.get().pluginId
            implementationClass = "DataStoreConventionPlugin"
        }
        register("detekt") {
            id = libs.plugins.ioffeivan.detekt.get().pluginId
            implementationClass = "DetektConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.ioffeivan.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.ioffeivan.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("ktlint") {
            id = libs.plugins.ioffeivan.ktlint.get().pluginId
            implementationClass = "KtlintConventionPlugin"
        }
        register("room") {
            id = libs.plugins.ioffeivan.room.get().pluginId
            implementationClass = "RoomConventionPlugin"
        }
        register("root") {
            id = libs.plugins.ioffeivan.root.get().pluginId
            implementationClass = "RootConventionPlugin"
        }
    }
}

fun Provider<PluginDependency>.asDependency() =
    map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
