package com.example.mvvm.view

import android.annotation.TargetApi
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import android.widget.PopupWindow
import android.widget.Toast
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
    private var isCancelButtonShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_api)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setViewModelAndConnectivityReceiver()
        setLayoutTitleAndVisibility()
        setButtonListeners()
        setUpRecyclerView()
        setObservers()
        setPhoneIntent()

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setButtonListeners(): Unit? = binding.account?.run {
        binding.cancelButton.setOnClickListener {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                title = getString(R.string.accounts)
                viewDetailsContainerOnPortrait.value = false
            }
            resetAccount()
        }
        binding.cancelButton.setImageDrawable(getDrawable(R.drawable.cancel))
        binding.editButton.setOnClickListener {
            if (enableAccountDetailEdit.value == false) enableAccountDetailEdit.value = true
            else putAccountDetailChanges()
            title = getString(R.string.account_details)
            viewDetailsContainerOnPortrait.value = true
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
        model.enableAccountDetailEdit.observe(this, Observer<Boolean> {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            if (it) {
                binding.editButton.setImageDrawable(getDrawable(R.drawable.check))
                showCancelButton()
            } else {
                binding.editButton.setImageDrawable(getDrawable(R.drawable.edit))
                if (isCancelButtonShown) hideCancelButton()
            }
        })
    }

    private fun showCancelButton(): Unit = binding.run {
        val animation = TranslateAnimation(
            editButton.x - editButton.width * 0.4375f, cancelButton.x,
            editButton.y, cancelButton.y
        )
        animation.duration = 300
        animation.fillAfter = false
        cancelButton.startAnimation(animation)
        isCancelButtonShown = true
    }

    private fun hideCancelButton(): Unit = binding.run {
        val animation = TranslateAnimation(
            cancelButton.x, editButton.x - editButton.width * 0.4375f,
            cancelButton.y, editButton.y
        )
        animation.duration = 300
        animation.fillAfter = false
        cancelButton.startAnimation(animation)
        isCancelButtonShown = false
    }

    private fun setPhoneIntent(): Unit = binding.phoneEdit.setOnLongClickListener {
        val callNumberPopupView: View =
            (applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.call_number, binding.fragmentContainer)
        val callNumberPopupWindow = PopupWindow(
            callNumberPopupView,
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            callNumberPopupWindow.elevation = 10.0f
        callNumberPopupWindow.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isFocusable = true
            isOutsideTouchable = true
            showAsDropDown(binding.phoneEdit)
        }
        callNumberPopupView.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL, Uri.parse("tel:" + binding.phoneEdit.text.toString())
            )
            if (packageManager.queryIntentActivities(intent, MATCH_DEFAULT_ONLY)
                    .isNotEmpty()
            ) startActivity(intent)
            else {
                Toast.makeText(this, getString(R.string.no_dialer_app_found), Toast.LENGTH_SHORT)
                    .show()
                callNumberPopupWindow.dismiss()
            }
        }
        true
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
        isCancelButtonShown = false
        binding.run {
            account?.viewDetailsContainerOnPortrait?.value = false
            cancelButton.refreshDrawableState()
            account?.resetAccount()
            fragmentContainer.visibility = View.GONE
        }
    } else {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

}
