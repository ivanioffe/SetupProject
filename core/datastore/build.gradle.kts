plugins {
    alias(libs.plugins.ioffeivan.datastore)
    alias(libs.plugins.ioffeivan.detekt)
    alias(libs.plugins.ioffeivan.ktlint)
}

android {
    namespace = "com.ioffeivan.core.datastore"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
