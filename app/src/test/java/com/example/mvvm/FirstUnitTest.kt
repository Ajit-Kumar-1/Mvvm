package com.example.mvvm

import android.os.Build
import com.android.volley.VolleyError
import com.example.mvvm.model.MyDatabase
import com.example.mvvm.view.CallAPIActivity
import com.example.mvvm.viewModel.ProfileViewModel
import org.json.JSONObject
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.io.InputStreamReader

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class FirstUnitTest {

    private lateinit var activity:CallAPIActivity
    private lateinit var viewModel:ProfileViewModel
    private var db:MyDatabase? = null

    @Before
    @Throws(Exception::class)
    fun setUp(){
        activity = Robolectric.buildActivity(CallAPIActivity::class.java).create().resume().get()
        viewModel= ProfileViewModel(activity.application)
    }

    @Test
    fun accountChanges_1() { // Check differences in entity properties
        accountChanges(iteration = 0)
    }

    @Test
    fun accountChanges_2(){ // Check differences in entity properties
        accountChanges(iteration = 1)
    }

    @Test
    fun accountChanges_3(){ // Check differences in entity properties
        accountChanges(iteration = 2)
    }

    @Test
    fun conversion_1(){ // convert json data into entity
        convert(iteration = 0)
    }

    @Test
    fun conversion_2(){ // convert json data into entity
        convert(iteration = 1)
    }

    @Test
    fun viewModelTest_1(){ // Check state of value bound to layout
        viewModel.apply {
            progress1.value = false
            netCall()
            assert(progress1.value!!)
        }
    }

    @Test
    fun viewModelTest_2(){ // Check state of value bound to layout
        viewModel.apply {
            progress1.value = true
            getPageError(VolleyError())
            assert(!progress1.value!!)
        }
    }

    @Test
    fun viewModelTest_3(){ // Check state of value bound to layout
        viewModel.apply {
            progress2.value = true
            onPutError(VolleyError())
            assert(!progress2.value!!)
        }
    }

    @Test
    fun viewModelTest_4(){ // Check state of value bound to layout
        viewModel.apply {
            refreshRecyclerView.value = false
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            assignment(position = 2)
            assert(refreshRecyclerView.value!!)
        }
    }

    @Test
    fun viewModelTest_5(){ // Check entry of data
        viewModel.apply {
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            assert(getData().size>0)
        }
    }

    @Test
    fun viewModelTest_6(){ // Check whether API call successful
        viewModel.apply {
            val expected: Int = pageIndex + 1
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            assertEquals(expected,pageIndex)
        }
    }

    @Test
    fun viewModelTest_7(){ // Check state of layout-bound variable
        viewModel.apply {
            enabled.value = true
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            assignment(position = 0)
            assert(!enabled.value!!)
        }
    }

    @Test
    fun viewModelTest_8(){ // Check state of layout-bound variable, subject to change on successful
        //network call
        viewModel.apply {
            viewContainer.value = true
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            assert(!viewContainer.value!!)
        }
    }

    @Test
    fun viewModelTest_9(){ // Check if correct data assigned
        viewModel.apply {
            position = 10
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            assertEquals(0,position)
        }
    }

    @Test
    fun viewModelTest_10(){ // Check layout-bound variable, which indicates state of network flow
        viewModel.apply {
            progress1.value = true
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            assert(!progress1.value!!)
        }
    }

    @Test
    fun viewModelTest_11(){ // Check layout-bound variable, which indicates state of network flow
        viewModel.apply {
            progress2.value = false
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            assignment(position = 0)
            account.value?.firstName="Jann-Fiete"
            putData()
            assert(progress2.value!!)
        }
    }

    @Test
    fun viewModelTest_12(){
        viewModel.apply {
            refreshRecyclerView.value = true
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            onPutResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_single_account_response),
                "UTF-8").readText()))
            assert(!refreshRecyclerView.value!!)
        }
    }

    @Test
    fun viewModelTest_13(){
        viewModel.apply {
            progress2.value = true
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            onPutResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_single_account_response),
                "UTF-8").readText()))
            assert(!progress2.value!!)
        }
    }

    @Test
    fun viewModelTest_14(){
        viewModel.apply {
            enabled.value = true
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            onPutResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_single_account_response),
                "UTF-8").readText()))
            assert(!enabled.value!!)
        }
    }

    private fun accountChanges(iteration:Int) { // Check differences in entity properties
        TestSamples.accountChangesSamples[iteration].apply{
            assertEquals(second, TestMethods.putChanges(first))
        }
    }

    private fun convert(iteration:Int){ // convert json data into entity
        TestSamples.conversionSamples[iteration].apply {
            assertEquals(second,TestMethods.getEntityFromJSON(JSONObject(first)))
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db?.close()
    }

}