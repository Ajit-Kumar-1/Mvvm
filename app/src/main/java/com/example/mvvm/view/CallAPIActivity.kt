package com.example.mvvm.view

import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm.R
import com.example.mvvm.databinding.ActivityCallApiBinding
import com.example.mvvm.util.ConnectivityReceiver
import com.example.mvvm.viewModel.ActivityCallBack
import com.example.mvvm.viewModel.DetailAdapter
import com.example.mvvm.viewModel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar

@Suppress("DEPRECATION")
class CallAPIActivity : AppCompatActivity(), ActivityCallBack,
    ConnectivityReceiver.ConnectivityReceiverListener {

    private var model: ProfileViewModel? = null
    private var details: FrameLayout? = null
    private val connectivityReceiver = ConnectivityReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding:ActivityCallApiBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_call_api)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        registerReceiver(connectivityReceiver,IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        binding.lifecycleOwner = this
        val recyclerView: RecyclerView = binding.recyclerView
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val snackBar: Snackbar = Snackbar.make(binding.recyclerView, R.string.not_connected,
            Snackbar.LENGTH_INDEFINITE)

        model?.apply {
            binding.let{
                details = it.fragmentContainer
                it.account = this
                if ((resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                    && active) ||
                    (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                    && enabled.value!!)) {
                    title = getString(R.string.account_details)
                    details?.visibility = View.VISIBLE
                }
                else
                    title = getString(R.string.accounts)
                if(pageIndex == 1)
                    details?.visibility = View.GONE
                it.cancelButton.setOnClickListener {
                    if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        title = getString(R.string.accounts)
                        active = false
                    }
                    assignment(position)
                }
                it.editButton.setOnClickListener {
                    title = getString(R.string.account_details)
                    active = true
                    if (!binding.editButton.isChecked)
                        putData()
                }
            }
            recyclerView.let{
                it.layoutManager=LinearLayoutManager(it.context)
                it.adapter=DetailAdapter(getData())
                it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(it: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(it, newState)
                        if (!it.canScrollVertically(1)) {
                            netCall()
                            if (pageIndex > 1)
                                binding.progressSpinner.y = it.height*0.75.toFloat()
                        }
                    }})
                it.scrollToPosition(position)
            }
            refreshRecyclerView.observe(this@CallAPIActivity, Observer<Boolean> {value ->
                if (value) recyclerView.adapter?.notifyDataSetChanged()
                else recyclerView.adapter?.notifyItemChanged(position)
            })
            viewContainer.observe(this@CallAPIActivity, Observer<Boolean> { value ->
                if (value) details?.visibility=View.VISIBLE
                else if (!value &&
                    resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                        details?.visibility=View.VISIBLE
            })
            statusCheck.observe (this@CallAPIActivity, Observer<Boolean> {value ->
                binding.statusEdit.text = if (value) getString(R.string.active)
                else getString(R.string.inactive)
            })
            retryRequest.observe(this@CallAPIActivity,Observer<Boolean>{ value ->
                if (value) snackBar.show()
                else snackBar.dismiss()
            })
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        model?.apply {
            if (isConnected) {
                if (retryRequest.value!!)
                retry()
                retryRequest.value = false
            }
            else {
                retryRequest.value = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun showDetails() {
        details?.visibility=View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&
            details?.visibility == View.VISIBLE) {
            title = getString(R.string.accounts)
            model?.active = false
            model?.assignment(model?.position!!)
            details?.visibility = View.GONE
        }
        else
            super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

}
