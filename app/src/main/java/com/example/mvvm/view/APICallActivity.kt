package com.example.mvvm.view

import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
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

class APICallActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var binding: ActivityCallApiBinding
    private lateinit var snackBar: Snackbar
    private val connectivityReceiver = ConnectivityReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_api)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setViewModelAndConnectivityReceiver()
        setLayoutTitleAndVisibility()
        setButtonListeners()
        setUpRecyclerView()
        setObservers()
    }

    private fun setViewModelAndConnectivityReceiver(): Unit = binding.let {
        @Suppress("DEPRECATION")
        registerReceiver(connectivityReceiver, IntentFilter(CONNECTIVITY_ACTION))
        it.account = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        it.lifecycleOwner = this
        snackBar = Snackbar.make(it.recyclerView, R.string.disconnected, Snackbar.LENGTH_INDEFINITE)
    }

    private fun setLayoutTitleAndVisibility(): Unit = if (binding.account?.dataExists == true) {
        if ((resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                    && binding.account?.viewDetailsContainerOnPortrait?.value!!) ||
            (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                    && binding.account?.enableAccountDetailEdit?.value!!)
        ) {
            title = getString(R.string.account_details)
            binding.fragmentContainer.visibility = View.VISIBLE
        } else title = getString(R.string.accounts)
    } else binding.fragmentContainer.visibility = View.GONE

    private fun setButtonListeners(): Unit? = binding.account?.run {
        binding.cancelButton.setOnClickListener {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                title = getString(R.string.accounts)
                viewDetailsContainerOnPortrait.value = false
            }
            reassignAccountDetails()
        }
        binding.editButton.setOnClickListener {
            title = getString(R.string.account_details)
            viewDetailsContainerOnPortrait.value = true
            if (!(it as ToggleButton).isChecked) putAccountDetailChanges()
        }
    }

    private fun setUpRecyclerView(): Unit = binding.recyclerView.run {
        layoutManager = LinearLayoutManager(context)
        adapter = binding.account?.adapter
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(it: RecyclerView, newState: Int) {
                super.onScrollStateChanged(it, newState)
                if (!it.canScrollVertically(1)) binding.account?.getAccountsPage()
            }
        })
    }

    private fun setObservers(): Unit? = binding.account?.let { model ->
        model.getData()?.observe(this, Observer<MutableList<AccountEntity>> {
            (binding.recyclerView.adapter as AccountDetailsAdapter).setData(it)
        })
        model.viewDetailsContainerOnPortrait.observe(this, Observer<Boolean> {
            binding.fragmentContainer.visibility = if (model.dataExists &&
                (it || resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            ) View.VISIBLE else View.GONE
        })
        model.retryNetworkRequest.observe(this, Observer<Boolean> {
            snackBar.apply { if (it) show() else dismiss() }
        })
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean): Unit = binding.account?.let {
        if (!isConnected) it.retryNetworkRequest.value = true
        else if (it.retryNetworkRequest.value == true) it.retryNetworkRequest()
    } ?: Unit

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed(): Unit = if (binding.fragmentContainer.visibility == View.VISIBLE &&
        resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    ) {
        title = getString(R.string.accounts)
        binding.account?.viewDetailsContainerOnPortrait?.value = false
        binding.account?.reassignAccountDetails()
        binding.fragmentContainer.visibility = View.GONE
    } else {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

}
