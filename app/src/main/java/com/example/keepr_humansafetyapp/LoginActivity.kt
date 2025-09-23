package com.example.keepr_humansafetyapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is already logged in → Go directly to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // No user logged in → Show login screen
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.login_container, LoginFragment.newInstance())
                    .commit()
            }
        }
    }




    fun openRegisterFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_container, RegisterFragment.newInstance())
            .addToBackStack(null) // This allows the back button to go back to LoginFragment
            .commit()
    }


}
