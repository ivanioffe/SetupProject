package com.ioffeivan.setupproject

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.the

internal val Project.libs
    get(): VersionCatalog = the<VersionCatalogsExtension>().named("libs")

internal fun Project.catalogVersion(alias: String): String {
    return libs.findVersion(alias)
        .orElseThrow {
            GradleException("$path : version '$alias' is missing from the libs catalog.")
        }
        .requiredVersion
}
