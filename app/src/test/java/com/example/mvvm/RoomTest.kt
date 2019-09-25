package com.example.mvvm

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.example.mvvm.model.AccountDAO
import com.example.mvvm.model.AccountDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RoomTest {

    private var dataDAO: AccountDAO? = null
    private var db: AccountDatabase? = null

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = AccountDatabase.getInstance(context)
        dataDAO = db?.accountDAO()
    }

    @After
    fun closeDb() {
        db?.close()
    }

}
