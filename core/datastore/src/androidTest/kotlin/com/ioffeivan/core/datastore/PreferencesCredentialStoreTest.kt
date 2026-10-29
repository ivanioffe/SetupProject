package com.ioffeivan.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID
// CI-DEMO: неиспользуемый импорт для анализаторов androidTest.
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class PreferencesCredentialStoreTest {
    @Test
    fun credentialsSurviveReopeningAndLogout() =
        runTest {
            // CI-DEMO: пропущен пробел перед равенством для ktlint.
            val context= InstrumentationRegistry.getInstrumentation().targetContext
            val file = File(context.cacheDir, "credentials-${UUID.randomUUID()}.preferences_pb")
            val firstJob = SupervisorJob()
            val secondJob = SupervisorJob()
            val original = StoredCredentials("ivan@example.com", "password123", isSignedIn = true)

            try {
                val firstStore =
                    PreferencesCredentialStore(
                        PreferenceDataStoreFactory.create(
                            scope = CoroutineScope(StandardTestDispatcher(testScheduler) + firstJob),
                        ) { file },
                    )
                firstStore.saveCredentials(original)
                firstStore.setSignedIn(isSignedIn = false)
                firstJob.cancelAndJoin()

                val reopenedStore =
                    PreferencesCredentialStore(
                        PreferenceDataStoreFactory.create(
                            scope = CoroutineScope(StandardTestDispatcher(testScheduler) + secondJob),
                        ) { file },
                    )
                assertEquals(original.copy(isSignedIn = false), reopenedStore.readCredentials())
                secondJob.cancelAndJoin()
            } finally {
                firstJob.cancel()
                secondJob.cancel()
                file.delete()
            }
        }
}
