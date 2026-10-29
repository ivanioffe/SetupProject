package com.ioffeivan.core.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
// CI-DEMO: неиспользуемый импорт для анализаторов androidTest.
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class RoomUserStoreTest {
    @Test
    fun usernameIsStoredAndRemoved() =
        runTest {
            // CI-DEMO: пропущен пробел перед равенством для ktlint.
            val context= InstrumentationRegistry.getInstrumentation().targetContext
            val database = Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java).build()

            try {
                val store = RoomUserStore(database.userDao())
                assertNull(store.readUsername())

                store.saveUsername("Ivan")
                assertEquals("Ivan", store.username.first())

                store.saveUsername("Updated")
                assertEquals("Updated", store.readUsername())

                store.deleteUsername()
                assertNull(store.username.first())
            } finally {
                database.close()
            }
        }
}
