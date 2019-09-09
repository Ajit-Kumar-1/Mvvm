package com.example.mvvm.view

import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm.R
import com.example.mvvm.databinding.ActivityCallApiBinding
import com.example.mvvm.util.ConnectivityReceiver
import com.example.mvvm.viewModel.DetailAdapter
import com.example.mvvm.viewModel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar

@Suppress("DEPRECATION")
class CallAPIActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener{
    private var snackBar:Snackbar?=null
    private var model: ProfileViewModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding:ActivityCallApiBinding=DataBindingUtil.setContentView(this, R.layout.activity_call_api)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        snackBar=Snackbar.make(binding.recyclerView, R.string.not_connected,Snackbar.LENGTH_INDEFINITE)
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        binding.lifecycleOwner=this
        model?.apply {
            binding.let{
                it.account=this
                container=it.fragmentContainer
                it.cancelButton.setOnClickListener {
                    if(resources.configuration.orientation==Configuration.ORIENTATION_LANDSCAPE) {
                        title = getString(R.string.accounts)
                        active= false
                    }
                    cancel()
                }
                it.editButton.setOnClickListener {
                    if(resources.configuration.orientation==Configuration.ORIENTATION_LANDSCAPE)
                        title=getString(R.string.account_details)
                    active=true
                    if(!binding.editButton.isChecked)
                        putData()
                }
                statusCheck.observe (this@CallAPIActivity, Observer<Boolean> {value ->
                    binding.statusEdit.text=when(value){
                        true-> getString(R.string.active)
                        else-> getString(R.string.inactive)
                    }
                })
                if ((resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && active) ||
                    (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && it.editButton.isChecked)) {
                    title = getString(R.string.account_details)
                    container?.visibility = View.VISIBLE
                }
                else
                    title = getString(R.string.accounts)
                recyclerView=it.recyclerView
            }
            recyclerView?.let{
                it.layoutManager=LinearLayoutManager(it.context)
                it.adapter=DetailAdapter(repository.data)
                it.scrollToPosition(position)
                it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(it: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(it, newState)
                        if (!it.canScrollVertically(1)) {
                            binding.progressSpinner.y=it.height*0.75.toFloat()
                            netCall()
                        }
                    }})
            }
        }
    }
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        model?.apply {
            if (isConnected) {
                snackBar?.dismiss()
                if(retryRequest){
                    retry()
                    retryRequest=false
                }
            }
            else {
                snackBar?.show()
                retryRequest=true
            }
        }
    }
    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }
    override fun onBackPressed() {
        model?.let {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&
                it.container?.visibility == View.VISIBLE) {
                title = getString(R.string.accounts)
                it.active = false
                it.container?.visibility = View.GONE
            }
            else
                super.onBackPressed()
        }
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}