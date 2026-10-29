package com.ioffeivan.core.datastore

data class StoredCredentials(
    val email: String,
    val password: String,
    val isSignedIn: Boolean,
)
