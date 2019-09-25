package com.example.mvvm.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mvvm.R
import com.example.mvvm.model.AccountDatabase
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    companion object {
        const val FRAGMENT_TAG = "home"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runBlocking {
            suspend { AccountDatabase.getInstance(this@MainActivity)?.clearAllTables() }
        }

        if (supportFragmentManager.fragments.size == 0) supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, SignInFragment(), FRAGMENT_TAG).commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)!!.isVisible) finish()
        else supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.left_to_right, R.anim.right_to_left)
            .replace(R.id.fragment_container, SignInFragment(), FRAGMENT_TAG).commit()
    }
}