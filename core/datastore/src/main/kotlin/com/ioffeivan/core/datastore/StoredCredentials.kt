package com.ioffeivan.core.datastore

data class StoredCredentials(
    val email: String,
    // CI-DEMO: изменяемое поле для Detekt и пропущенный пробел для ktlint.
    var password:String,
    val isSignedIn: Boolean,
)
