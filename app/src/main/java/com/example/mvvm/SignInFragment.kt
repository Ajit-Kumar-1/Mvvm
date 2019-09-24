package com.example.mvvm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mvvm.view.APICallActivity
import java.util.regex.Pattern

class SignInFragment : Fragment() {

    companion object {
        const val EMAIL_REGEX: String = "[A-Z0-9a-z]+@[A-Za-z0-9]+\\.[A-Za-z]{2,64}"
        const val REMEMBER_KEY: String = "autoLogin"
    }

    private var sharedPreference: SharedPreferences? = null
    private var email: String? = null
    private var emailAutoFill: String? = null
    private var password: String? = null
    private val emailValidationPattern = Pattern.compile(EMAIL_REGEX)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sign_in_fragment, container, false)
        val context = this.activity
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        context!!.title = getString(R.string.sign_in)
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
            InputMethodManager.HIDE_NOT_ALWAYS,
            0
        )
        val emailField: EditText = view.findViewById(R.id.sign_in_email)
        //val passwordField:EditText = view.findViewById(R.id.sign_in_password)
        //val rememberCheck:CheckBox = view.findViewById(R.id.sign_in_remember)
        sharedPreference = context.getSharedPreferences(REMEMBER_KEY, Context.MODE_PRIVATE)
        emailField.requestFocus()
//        when {
//            sharedPreference!!.getBoolean(REMEMBER_TOGGLE , false) &&
//                    sharedPreference!!.getString(REMEMBER_ACCOUNT ,
//                    NOT_FOUND ) != NOT_FOUND
//            -> startActivity(Intent(context, AccountActivity::class.java).
//            putExtra(ACCOUNT,
//                sharedPreference!!.getString(REMEMBER_ACCOUNT ,
//                NOT_FOUND )))
//        }
        //Auto-fill email
        emailField.setText(emailAutoFill)

        view.findViewById<Button>(R.id.sign_in)?.setOnClickListener {
            // Sign-in
            /*email = emailField.text.toString().toLowerCase().trim()
            password = passwordField.text.toString().trim()
            sharedPreference = context.getSharedPreferences(email, Context.MODE_PRIVATE)
            when {
                email!="" -> when {
                    validateEmail(email) -> when  {
                        sharedPreference?.getString(PREFERENCE_PASSWORD ,
                        NOT_FOUND )==password-> {
                            emailAutoFill = email
                            startActivity(Intent(context, AccountActivity::class.java).
                            putExtra(ACCOUNT ,email))
                            context.getSharedPreferences(REMEMBER_KEY ,
                             Context.MODE_PRIVATE).edit().apply{
                                putString(REMEMBER_ACCOUNT ,email)
                                putBoolean(REMEMBER_TOGGLE ,rememberCheck.isChecked)
                                apply()
                            } // Remember me set
                        } //Sign in complete
                        sharedPreference?.getString(PREFERENCE_PASSWORD ,
                        NOT_FOUND )==NOT_FOUND
                        ->Toast.makeText(context,getString(R.string.account_nonexistent),
                         Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(context,getString(R.string.wrong_password),
                         Toast.LENGTH_SHORT).show()
                    }
                    else -> Toast.makeText(context,getString(R.string.enter_valid_email),
                     Toast.LENGTH_SHORT).show()
                }
                else -> Toast.makeText(context,getString(R.string.enter_email),
                 Toast.LENGTH_SHORT).show()
            }*/
            startActivity(Intent(context, APICallActivity::class.java))
            context.overridePendingTransition(R.anim.enter, R.anim.exit)
        } // End of sign-in
//        view.findViewById<TextView>(R.id.forgot_password)?.setOnClickListener {
//            fragmentManager!!.beginTransaction().apply{
//                setCustomAnimations(R.anim.enter,R.anim.exit)
//                replace(R.id.fragment_container, ForgotPasswordFragment())
//                addToBackStack(null)
//                commit()
//            }
//        }
        view.findViewById<Button>(R.id.create_account)?.setOnClickListener {
            //            fragmentManager!!.beginTransaction().apply{
//                setCustomAnimations(R.anim.enter,R.anim.exit)
//                replace(R.id.fragment_container, RegisterFragment())
//                addToBackStack(null)
//                commit()
//            }
            startActivity(Intent(context, TemporaryActivity::class.java))
        }
        return view
    }
//    private fun validateEmail(email: String?): Boolean {
//        return when {
//            email != "" && emailValidationPattern.matcher(email!!).matches() -> true
//            else -> false
//        }
//    }

}