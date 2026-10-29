plugins {
    alias(libs.plugins.ioffeivan.jvm.library)
    alias(libs.plugins.ioffeivan.hilt)
    alias(libs.plugins.ioffeivan.detekt)
    alias(libs.plugins.ioffeivan.ktlint)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
