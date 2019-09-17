package com.example.mvvm

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.room.Room
import com.example.mvvm.model.APIEntity
import com.example.mvvm.model.MyDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class TemporaryActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temporary)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val db =MyDatabase.getInstance(application)
        var data: APIEntity?=null
        findViewById<EditText>(R.id.findByLastName).requestFocus()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).
            toggleSoftInput(InputMethodManager.SHOW_FORCED,0)
        findViewById<Button>(R.id.getEntry).setOnClickListener {
            val deferred=GlobalScope.async {
                db?.dataDao()?.findByLastName(findViewById<EditText>(R.id.findByLastName).
                    text.trim().toString())
            }
            runBlocking { data=deferred.await() }
         findViewById<TextView>(R.id.textView4).text=
             "ID: ${data?.id}\nFirst Name: ${data?.firstName}\nLast Name: " +
                     "${data?.lastName}\nGender: " + "${data?.gender}\nEmail:" +
                     " ${data?.email}\nPhone:" + " ${data?.phone}\nDate of Birth: " +
                     "${data?.dob}\nWebsite: ${data?.website}\nAddress: " +
                     "${data?.address}\nStatus: ${data?.status}"
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).
        toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS,0)
        return super.onSupportNavigateUp()
    }
}