package com.example.mvvm

import android.os.Build
import androidx.lifecycle.ViewModelProviders
import com.android.volley.VolleyError
import com.example.mvvm.view.CallAPIActivity
import com.example.mvvm.viewModel.ProfileViewModel
import org.json.JSONObject
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

//@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class FirstUnitTest {
    private lateinit var viewModel: ProfileViewModel

    @Before
    @Throws(Exception::class)
    fun setUp(){
//        val activity= Robolectric.buildActivity(CallAPIActivity::class.java)
//            .create().resume().get()
//        viewModel= ViewModelProviders.of(activity).get(ProfileViewModel::class.java)
        viewModel= ProfileViewModel(RuntimeEnvironment.application)
    }

    @Test
    fun accountChanges_1() {//Logical algorithm test
        accountChanges(0)
    }

    @Test
    fun accountChanges_2(){//Logical algorithm test
        accountChanges(1)
    }

    @Test
    fun accountChanges_3(){
        accountChanges(2)
    }

    @Test
    fun viewModelTest_1(){// Variable state verification
        viewModel.netCall()
        println(viewModel.getData().toString())
        assert(viewModel.progress1.value!!)
    }

    @Test
    fun viewModelTest_2(){// Variable state verification
        viewModel.getPageError(VolleyError())
        assert(!viewModel.progress1.value!!)
    }

    @Test
    fun viewModelTest_3(){// Variable state verification
        viewModel.refreshRecyclerView.value=true

        viewModel.onPutResponse(JSONObject("{\"_meta\":{\"success\":true}}"))
        assert(!viewModel.refreshRecyclerView.value!!)
    }

    @Test
    fun conversion_1(){//Logical algorithm test
         convert(0)
    }

    @Test
    fun conversion_2(){//Logical algorithm test
        convert(1)
    }

    private fun accountChanges(iteration:Int) {//Repeated test method
        TestSamples.accountChangesSamples[iteration].apply{
            assertEquals(second, TestMethods.putChanges(first))
        }
    }
    private fun convert(iteration:Int){//Repeated test method
        TestSamples.conversionSamples[iteration].apply {
            assertEquals(second,TestMethods.retrieveDetails(JSONObject(first)))
        }
    }

}