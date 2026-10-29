package com.ioffeivan.setupproject

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk {
            version = release(catalogVersion("compileSdk").toInt())
        }

        defaultConfig.minSdk = catalogVersion("minSdk").toInt()

        defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        compileOptions.sourceCompatibility = JavaVersion.toVersion(catalogVersion("jvmTarget"))
        compileOptions.targetCompatibility = JavaVersion.toVersion(catalogVersion("jvmTarget"))
    }

    extensions.configure<KotlinAndroidProjectExtension> {
        configureKotlin(compilerOptions)
    }
}

internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.toVersion(catalogVersion("jvmTarget"))
        targetCompatibility = JavaVersion.toVersion(catalogVersion("jvmTarget"))
    }

    extensions.configure<KotlinJvmProjectExtension> {
        configureKotlin(compilerOptions)
    }
}

private fun Project.configureKotlin(compilerOptions: KotlinJvmCompilerOptions) {
    compilerOptions.apply {
        jvmTarget.set(JvmTarget.fromTarget(catalogVersion("jvmTarget")))

        allWarningsAsErrors.set(
            providers.gradleProperty("warningsAsErrors")
                .map(String::toBoolean).orElse(false),
        )
    }
}
