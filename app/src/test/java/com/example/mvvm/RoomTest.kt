package com.example.mvvm

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.example.mvvm.model.APIEntity
import com.example.mvvm.model.DataDAO
import com.example.mvvm.model.MyDatabase
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RoomTest {
    private var dataDAO: DataDAO? = null
    private var db: MyDatabase? = null

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = MyDatabase.getInstance(context)
        dataDAO = db?.dataDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db?.close()
    }

    @Test
    @Throws(Exception::class)
    fun test_1(){
        val sample = TestSamples.accountChangesSamples[0].first.first
        var actual:APIEntity? = null
        val deferred: Deferred<APIEntity?> = GlobalScope.async {
            dataDAO?.insert(sample)
            dataDAO?.findByLastName(sample.lastName!!)
        }
        runBlocking {
            actual = deferred.await()
        }
        assertEquals(sample, actual)
    }

}
