package com.example.roaddamagedetector.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.roaddamagedetector.R
import com.example.roaddamagedetector.databinding.ActivityLoginBinding
import com.example.roaddamagedetector.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        firebaseAuth = FirebaseAuth.getInstance();

        validateButton()

        binding.edEmailLogin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateButton()
            }
        })

        binding.edPasswordLogin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateButton()
            }
        })

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateButton() {
        binding.btnLogin.isEnabled = true
        binding.btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        binding.btnLogin.setOnClickListener {
            firebaseAuth!!.signInWithEmailAndPassword(binding.edEmailLogin.text.toString(), binding.edPasswordLogin.text.toString())
                .addOnCompleteListener(
                    this
                ) { task -> //checking if success
                    if (task.isSuccessful) {
                        //display some message here
                            Toast.makeText(
                                this,
                                "Successfully Login",
                                Toast.LENGTH_LONG
                            ).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    } else {
                            //display some message here
                            Toast.makeText(
                                this,
                                "Wrong Email or Password",
                                Toast.LENGTH_LONG
                            ).show()
                    }
                }
        }
    }
}
