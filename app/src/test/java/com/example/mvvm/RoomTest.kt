package com.example.mvvm

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.example.mvvm.model.APIEntity
import com.example.mvvm.model.DataDAO
import com.example.mvvm.model.MyDatabase
import junit.framework.JUnit4TestAdapter
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RoomTest {
    private var dataDAO: DataDAO? = null
    private var db: MyDatabase? = null

    init {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = MyDatabase.getInstance(context)
        dataDAO = db?.dataDao()
    }

    @Before
    fun createDb() {
    }

    @Test
    @Throws(Exception::class)
    fun test_1(){
        val sample = TestSamples.accountChangesSamples[0].first.first
        runBlocking {
            dataDAO?.insert(sample)
            assertEquals(sample, dataDAO?.findByLastName(sample.lastName!!))
        }
    }

    @Test
    @Throws(Exception::class)
    fun test_2() {
        val sample = TestSamples.accountChangesSamples[0].first.first
        var actual:APIEntity? = null
        runBlocking {
                dataDAO?.insert(sample)
                actual = dataDAO?.findById(sample.id)
                assertEquals(sample, actual)
            }
    }

    @Test
    @Throws(Exception::class)
    fun test_3() {
        val sample = TestSamples.accountChangesSamples[0].first.first
        val newSample = TestSamples.accountChangesSamples[1].first.first
        var actual:APIEntity? = null
        runBlocking {
                actual = dataDAO?.run {
                    insert(sample)
                    updateData(newSample)
                    getAll()[10]
                }
                assertEquals(newSample, actual)
            }
    }

//    @After
//    fun closeDb(){
//        db?.close()
//    }


}
