package com.example.mvvm

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.example.mvvm.model.AccountDAO
import com.example.mvvm.model.AccountDatabase
import com.example.mvvm.model.AccountEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RoomTest {

    private var dataDAO: AccountDAO? = null
    private var db: AccountDatabase? = null
    private val sample: AccountEntity = TestSamples.accountChangesSamples[0].first.first

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = AccountDatabase.getInstance(context)
        dataDAO = db?.accountDAO()
    }

    @Test
    @Throws(Exception::class)
    fun lastNameQueryTest() = runBlocking {
        dataDAO?.insert(sample)
        assertEquals(sample, dataDAO?.findByLastName(sample.lastName!!))
    }

    @Test
    @Throws(Exception::class)
    fun idQueryTest() = runBlocking {
        dataDAO?.insert(sample)
        assertEquals(sample, dataDAO?.findById(sample.id))
    }

    @Test
    @Throws(Exception::class)
    fun updateItemTest() = runBlocking {
        dataDAO?.let {
            it.insert(TestSamples.accountChangesSamples[0].first.first)
            it.updateData(sample)
        }
        assertEquals(sample, dataDAO?.getAll()?.value?.get(0))
    }

    @After
    fun closeDb() {

    }


}
