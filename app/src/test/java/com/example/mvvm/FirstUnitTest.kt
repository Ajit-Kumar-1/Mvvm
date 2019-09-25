package com.example.mvvm

import android.os.Build
import android.os.Looper.getMainLooper
import com.example.mvvm.view.APICallActivity
import com.example.mvvm.viewModel.AccountViewModel
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.InputStreamReader

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class FirstUnitTest {

    private lateinit var activity: APICallActivity
    private lateinit var viewModel: AccountViewModel

    @Before
    @Throws(Exception::class)
    fun setUp() {
        activity = Robolectric.buildActivity(APICallActivity::class.java).create().resume().get()
        viewModel = AccountViewModel(activity.application)
    }

    @Test
    fun accountChanges_1() { // Check differences in entity properties
        accountDetailChanges(iteration = 0)
    }

    @Test
    fun accountChanges_2() { // Check differences in entity properties
        accountDetailChanges(iteration = 1)
    }

    @Test
    fun accountChanges_3() { // Check differences in entity properties
        accountDetailChanges(iteration = 2)
    }

    @Test
    fun conversion_1() { // convertJSONObjectToAccountEntity json data into entity
        convertJSONObjectToAccountEntity(iteration = 0)
    }

    @Test
    fun conversion_2() { // convertJSONObjectToAccountEntity json data into entity
        convertJSONObjectToAccountEntity(iteration = 1)
    }

    @Test
    fun paginationProgressSpinnerStateOnNetworkCallInitiation() {
        // Check state of value bound to layout
        viewModel.apply {
            paginationProgressSpinnerVisibility.value = false
            getAccountsPage()
            assert(paginationProgressSpinnerVisibility.value!!)
        }
    }

    @Test
    fun paginationProgressSpinnerStateTestOnNetworkErrorResponse() {
        // Check state of value bound to layout
        viewModel.apply {
            paginationProgressSpinnerVisibility.value = true
            getAccountsPageError(Throwable())
            assert(!paginationProgressSpinnerVisibility.value!!)
        }
    }

    @Test
    fun putRequestProgressSpinnerStateTestOnNetworkErrorResponse() {
        // Check state of value bound to layout
        viewModel.apply {
            putRequestProgressSpinnerVisibility.value = true
            putAccountChangesError(Throwable())
            assert(!putRequestProgressSpinnerVisibility.value!!)
        }
    }

    @Test
    fun dataNotNullAfterLoadingInitialData() {
        // Check entry of data
        viewModel.apply {
            loadInitialPage()
            assert(getData()?.value?.size!! > 0)
        }
    }

    @Test
    fun detailsContainerVisibilityStateAfterLoadingInitialData() {
        // Check state of layout-bound variable, subject to change on successful
        //network call
        viewModel.apply {
            viewDetailsContainerOnPortrait.value = true
            loadInitialPage()
            assert(!viewDetailsContainerOnPortrait.value!!)
        }
    }

    @Test
    fun positionAssignmentAfterLoadingInitialData() {
        // Check if correct data assigned
        viewModel.apply {
            loadInitialPage()
            assertEquals(0, getSelectedItemPosition())
        }
    }

    @Test
    fun paginationProgressSpinnerVisibilityStateAfterLoadingInitialData() {
        // Check layout-bound variable, which indicates state of network flow
        viewModel.apply {
            paginationProgressSpinnerVisibility.value = true
            loadInitialPage()
            assert(!paginationProgressSpinnerVisibility.value!!)
        }
    }

    @Test
    fun statusSwitchValueStateAfterLoadingInitialData() {
        // Check if variable assignAccountDetails matches data value
        viewModel.apply {
            loadInitialPage()
            assertEquals(
                accountCurrentDetails.value?.status == "showInPortrait", statusSwitchValue.value
            )
        }
    }

    @Test
    fun maleRadioButtonStateTest() {
        // Check if variable assignAccountDetails matches data value
        viewModel.apply {
            loadInitialPage()
            assertEquals(accountCurrentDetails.value?.gender == "male", maleRadioButtonValue.value)
        }
    }

    @Test
    fun femaleRadioButtonStateAfterLoadingInitialData() {
        // Check if variable assignAccountDetails matches data value
        viewModel.apply {
            loadInitialPage()
            assertEquals(
                accountCurrentDetails.value?.status == "female",
                femaleRadioButtonValue.value
            )
        }
    }

    @Test
    fun enableAccountDetailEditStateTest2() { // Check layout-bound variable
        viewModel.apply {
            enableAccountDetailEdit.value = true
            loadInitialPage()
            shadowOf(getMainLooper()).idle()
            loadPutResponse()
            assert(!enableAccountDetailEdit.value!!)
        }
    }

    @Test
    fun putResponseDataTest() { // Ensure data updated after PUT request
        viewModel.apply {
            loadInitialPage()
            shadowOf(getMainLooper()).idle()
            loadPutResponse()
            val expected = JSONObject(
                InputStreamReader(
                    activity.resources.openRawResource
                        (R.raw.sample_single_account_response), "UTF-8"
                ).readText()
            )
                .getJSONObject("result").toString()
            assertEquals(expected, getData()?.value?.get(0))
        }
    }

    private fun accountDetailChanges(iteration: Int) {
        // Check differences in entity properties
        TestSamples.accountChangesSamples[iteration].apply {
            assertEquals(second, TestMethods.findChanges(first.first,first.second))
        }
    }

    private fun convertJSONObjectToAccountEntity(iteration: Int) {
        // convertJSONObjectToAccountEntity json data into entity
        TestSamples.conversionSamples[iteration].apply {
            assertEquals(second, TestMethods.getEntityFromJSON(JSONObject(first)))
        }
    }

    private fun loadInitialPage() {
        viewModel.getAccountsPageResponse(
            JSONObject(
                InputStreamReader(
                    activity.resources.openRawResource(R.raw.sample_page_response), "UTF-8"
                )
                    .readText()
            )
        )
    }

    private fun loadPutResponse() {
        viewModel.putAccountChangesResponse(
            JSONObject(
                InputStreamReader(
                    activity.resources.openRawResource(R.raw.sample_single_account_response),
                    "UTF-8"
                ).readText()
            )
        )
    }

    @After
    fun clear() {

    }
}