plugins {
    alias(libs.plugins.ioffeivan.android.application)
    alias(libs.plugins.ioffeivan.detekt)
    alias(libs.plugins.ioffeivan.hilt)
    alias(libs.plugins.ioffeivan.ktlint)
    alias(libs.plugins.ioffeivan.compose)
}

android {
    namespace = "com.ioffeivan.setupproject"
    defaultConfig {
        applicationId = "com.ioffeivan.setupproject"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:home"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
}
