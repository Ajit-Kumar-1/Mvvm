package com.example.mvvm.view

import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm.R
import com.example.mvvm.databinding.ActivityCallApiBinding
import com.example.mvvm.model.AccountEntity
import com.example.mvvm.util.ConnectivityReceiver
import com.example.mvvm.viewModel.AccountDetailsAdapter
import com.example.mvvm.viewModel.AccountViewModel
import com.google.android.material.snackbar.Snackbar

@Suppress("DEPRECATION")
class APICallActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var model: AccountViewModel
    private lateinit var fullDetailsContainer: FrameLayout
    private lateinit var binding: ActivityCallApiBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var snackBar: Snackbar
    private val connectivityReceiver = ConnectivityReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_api)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        registerReceiver(
            connectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        model = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        binding.lifecycleOwner = this
        binding.account = model

        setViewReferences()
        setLayoutTitleAndVisibility(model)
        setButtonListeners(model)
        setUpRecyclerView(recyclerView, model)
        setObservers(this, model)
    }

    private fun setLayoutTitleAndVisibility(model: AccountViewModel) {
        if ((resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                    && model.viewDetailsContainerOnPortrait.value!!) ||
            (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                    && model.enableAccountDetailEdit.value!!)
        ) {
            title = getString(R.string.account_details)
            fullDetailsContainer.visibility = View.VISIBLE
        } else {
            title = getString(R.string.accounts)
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                return
        }

        if (!model.dataExists)
            fullDetailsContainer.visibility = View.GONE
    }

    private fun setViewReferences() {
        recyclerView = binding.recyclerView
        fullDetailsContainer = binding.fragmentContainer
        snackBar = Snackbar.make(recyclerView, R.string.not_connected, Snackbar.LENGTH_INDEFINITE)
    }

    private fun setButtonListeners(model: AccountViewModel) {
        binding.cancelButton.setOnClickListener {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                title = getString(R.string.accounts)
                model.viewDetailsContainerOnPortrait.value = false
            }
            model.reassignAccountDetails()
        }
        binding.editButton.setOnClickListener {
            title = getString(R.string.account_details)
            model.viewDetailsContainerOnPortrait.value = true
            if (!binding.editButton.isChecked)
                model.putAccountDetailChanges()
        }
    }

    private fun setUpRecyclerView(recyclerView: RecyclerView, model: AccountViewModel) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AccountDetailsAdapter(model.getData()?.value)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(it: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(it, newState)
                    if (!it.canScrollVertically(1)) {
                        model.getPage()
                        model.getData()?.value?.size?.minus(1)
                            ?.let { it1 -> it.scrollToPosition(it1) }
                    }
                }
            })
            scrollToPosition(model.getRecyclerViewPosition())
        }
    }

    private fun setObservers(activity: AppCompatActivity, model: AccountViewModel) {
        model.apply {
            viewDetailsContainerOnPortrait.observe(activity, Observer<Boolean> { value ->
                if (dataExists)
                    if (value) fullDetailsContainer.visibility = View.VISIBLE
                    else {
                        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                            fullDetailsContainer.visibility = View.VISIBLE
                        else
                            fullDetailsContainer.visibility = View.GONE
                    }
            })
            statusSwitchValue.observe(activity, Observer<Boolean> { value ->
                binding.statusEdit.text = if (value) getString(R.string.active)
                else getString(R.string.inactive)
            })
            retryNetworkRequest.observe(activity, Observer<Boolean> { value ->
                if (value) snackBar.show()
                else snackBar.dismiss()
            })
            getData()?.observe(activity, Observer<MutableList<AccountEntity>> { value ->
                (recyclerView.adapter as AccountDetailsAdapter).setData(value)
                if (value.size == 1) assignAccountDetails(value[0], 0)
            })
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        model.apply {
            if (isConnected) {
                if (retryNetworkRequest.value!!)
                    retryNetworkRequest()
                retryNetworkRequest.value = false
            } else {
                retryNetworkRequest.value = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&
            fullDetailsContainer.visibility == View.VISIBLE
        ) {
            title = getString(R.string.accounts)
            model.viewDetailsContainerOnPortrait.value = false
            model.reassignAccountDetails()
            fullDetailsContainer.visibility = View.GONE
        } else
            super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

}
