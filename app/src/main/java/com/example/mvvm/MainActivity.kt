package com.example.mvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mvvm.model.StringValues

class MainActivity : AppCompatActivity() {
    private val finalValues= StringValues()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(supportFragmentManager.fragments.size==0)//Ensure start-up state (no fragments yet)
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, SignInFragment(),
                finalValues.HOME ).commit()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    override fun onBackPressed() {
        if(supportFragmentManager.findFragmentByTag(finalValues.HOME)!!.isVisible)
            finish()
        else
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.left_to_right,R.anim.right_to_left)
            replace(R.id.fragment_container, SignInFragment(),finalValues.HOME).commit()
        }
    }
}