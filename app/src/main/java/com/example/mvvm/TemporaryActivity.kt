package com.example.mvvm

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mvvm.model.AccountDatabase
import com.example.mvvm.model.AccountEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class TemporaryActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temporary)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val accountDatabase: AccountDatabase? = AccountDatabase.getInstance(application)
        var account: AccountEntity? = null

        findViewById<EditText>(R.id.findByLastName).requestFocus()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            0
        )

        findViewById<Button>(R.id.getEntry).setOnClickListener {
            val deferred = GlobalScope.async {
                accountDatabase?.accountDAO()?.findByLastName(
                    findViewById<EditText>(R.id.findByLastName).text.trim().toString()
                )
            }
            runBlocking {
                account = deferred.await()
            }
            findViewById<TextView>(R.id.textView4).text =
                "ID: ${account?.id}\nFirst Name: ${account?.firstName}\nLast Name: " +
                        "${account?.lastName}\nGender: " + "${account?.gender}\nEmail:" +
                        " ${account?.email}\nPhone:" + " ${account?.phone}\nDate of Birth: " +
                        "${account?.dob}\nWebsite: ${account?.website}\nAddress: " +
                        "${account?.address}\nStatus: ${account?.status}"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
            InputMethodManager.HIDE_NOT_ALWAYS,
            0
        )
        return super.onSupportNavigateUp()
    }

}