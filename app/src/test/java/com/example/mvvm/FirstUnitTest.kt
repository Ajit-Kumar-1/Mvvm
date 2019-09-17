package com.example.mvvm

import android.os.Build
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
import org.robolectric.annotation.Config
import java.io.InputStreamReader

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class FirstUnitTest {

    private lateinit var activity:CallAPIActivity
    private lateinit var viewModel:ProfileViewModel

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
            loadInitialPage()
            progress2.value = true
            onPutError(VolleyError())
            assert(!progress2.value!!)
        }
    }

    @Test
    fun viewModelTest_4(){ // Check state of value bound to layout
        viewModel.apply {
            loadInitialPage()
            assignment(position = 2)
            netCall()
            assert(refreshRecyclerView.value!!)
        }
    }

    @Test
    fun viewModelTest_5(){ // Check entry of data
        viewModel.apply {
            loadInitialPage()
            assert(getData().size>0)
        }
    }

    @Test
    fun viewModelTest_6(){ // Check whether API call successful
        viewModel.apply {
            val expected: Int = getPageIndex() + 1
            loadInitialPage()
            getPageResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
                .readText()))
            assertEquals(expected,getPageIndex())
        }
    }

    @Test
    fun viewModelTest_7(){ // Check state of layout-bound variable
        viewModel.apply {
            loadInitialPage()
            enabled.value = true
            assignment(position = 0)
            assert(!enabled.value!!)
        }
    }

    @Test
    fun viewModelTest_8(){ // Check state of layout-bound variable, subject to change on successful
        //network call
        viewModel.apply {
            loadInitialPage()
            assert(!viewContainer.value!!)
        }
    }

    @Test
    fun viewModelTest_9(){ // Check if correct data assigned
        viewModel.apply {
            loadInitialPage()
            assertEquals(0,getPosition())
        }
    }

    @Test
    fun viewModelTest_10(){ // Check layout-bound variable, which indicates state of network flow
        viewModel.apply {
            loadInitialPage()
            assert(!progress1.value!!)
        }
    }

    @Test
    fun viewModelTest_11(){ // Check layout-bound variable, which indicates state of network flow
        viewModel.apply {
            loadInitialPage()
            progress2.value = false
            assignment(position = 0)
            account.value?.firstName="Jann-Fiete"
            putData()
            assert(progress2.value!!)
        }
    }

    @Test
    fun viewModelTest_12(){ //Check toggle of Livedata variable
        viewModel.apply {
            loadInitialPage()
            onPutResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_single_account_response),
                "UTF-8").readText()))
            assert(refreshRecyclerView.value!!)
        }
    }

    @Test
    fun viewModelTest_13(){ // Check layout-bound variable
        viewModel.apply {
            loadInitialPage()
            progress2.value = true
            onPutResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_single_account_response),
                "UTF-8").readText()))
            assert(!progress2.value!!)
        }
    }

    @Test
    fun viewModelTest_14(){ // Check layout-bound variable
        viewModel.apply {
            enabled.value = true
            loadInitialPage()
            onPutResponse(JSONObject(InputStreamReader(
                activity.resources.openRawResource(R.raw.sample_single_account_response),
                "UTF-8").readText()))
            assert(!enabled.value!!)
        }
    }
    
    @Test
    fun viewModelTest_15(){ // Check if variable assignment matches data value
        viewModel.apply {
            loadInitialPage()
            assertEquals(account.value?.status=="active",statusCheck.value)
        }
    }

    @Test
    fun viewModelTest_16(){ // Check if variable assignment matches data value
        viewModel.apply {
            loadInitialPage()
            assertEquals(account.value?.gender=="male",maleCheck.value)
        }
    }

    @Test
    fun viewModelTest_17(){ // Check if variable assignment matches data value
        viewModel.apply {
            loadInitialPage()
            assertEquals(account.value?.status=="female",femaleCheck.value)
        }
    }
    
    @Test
    fun viewModelTest_18(){ // Check that reassignment is to the correct position
        viewModel.apply {
            loadInitialPage()
            val position = getPosition()
            assignment(position)
            reassign()
            assertEquals(position,getPosition())
        }
    }
    
    @Test
    fun viewModelTest_19(){ // Ensure increment of url page index
        viewModel.apply {
            loadInitialPage()
            assertEquals(2,getPageIndex())
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

    private fun loadInitialPage(){
        viewModel.getPageResponse(JSONObject(InputStreamReader(
            activity.resources.openRawResource(R.raw.sample_page_response),"UTF-8")
            .readText()))
    }
}