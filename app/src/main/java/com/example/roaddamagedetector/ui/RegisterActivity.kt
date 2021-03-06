package com.example.roaddamagedetector.ui

import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.roaddamagedetector.R
import com.example.roaddamagedetector.data.local.UserEntity
import com.example.roaddamagedetector.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity() {
    private var emailValid = false
    private var nameValid = false
    private var passwordValid = false
    private var passwordConfirmationValid = false

    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var firestore: FirebaseFirestore

    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        validateButton()

        binding.edEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
            }
        })

        binding.edName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateName()
            }
        })

        binding.edPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword()
            }
        })

        binding.edConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasswordConfirmation()
            }
        })
    }

    fun validateEmail() {
        val input = binding.edEmail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            emailValid = false
            showEmailExistAlert(true)
        } else {
            emailValid = true
            showEmailExistAlert(false)
        }
        validateButton()
    }

    fun validateName() {
        val input = binding.edName.text.toString()
        val reges = Regex("[a-zA-Z ]+")
        if (!input.matches(reges)) {
            nameValid = false
            showNameExistAlert(true)
        } else {
            nameValid = true
            showNameExistAlert(false)
        }
        validateButton()
    }

    fun validatePassword() {
        val input = binding.edPassword.text.toString()
        if (input.length < 6) {
            passwordValid = false
            showPasswordMinimalAlert(true)
        } else {
            passwordValid = true
            showPasswordMinimalAlert(false)
        }
        validateButton()
    }

    fun validatePasswordConfirmation() {
        val input = binding.edConfirmPassword.text.toString()
        if (input != binding.edPassword.text.toString()) {
            passwordConfirmationValid = false
            showPasswordConfirmationAlert(true)
        } else {
            passwordConfirmationValid = true
            showPasswordConfirmationAlert(false)
        }
        validateButton()
    }

    private fun validateButton() {
        if (emailValid && passwordValid && passwordConfirmationValid) {
            binding.btnRegister.isEnabled = true
            binding.btnRegister.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.btnRegister.setOnClickListener {
                firebaseAuth!!.createUserWithEmailAndPassword(binding.edEmail.text.toString(), binding.edPassword.text.toString())
                    .addOnCompleteListener(
                        this
                    ) { task -> //checking if success
                        if (task.isSuccessful) {
                            val user =
                                UserEntity(
                                    firebaseAuth!!.uid.toString(),
                                    binding.edEmail.text.toString(),
                                    binding.edName.text.toString(),
                                    binding.edPassword.text.toString(),
                                    binding.edConfirmPassword.text.toString(),
                                    ""
                                )
                            firestore.collection("users").document(user.id).set(user)

                            //display some message here
                            Toast.makeText(
                                this,
                                "Successfully registered",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this,LoginActivity::class.java))
                        } else {
                            //display some message here
                            Toast.makeText(
                                this,
                                "Registration Error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        finish()
                    }
            }
        } else {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        }
    }

    private fun showEmailExistAlert(isNotValid: Boolean) {
        binding.edEmail.error = if (isNotValid) getString(R.string.invalid_email) else null
    }

    private fun showNameExistAlert(isNotValid: Boolean) {
        binding.edName.error = if (isNotValid) getString(R.string.invalid_name) else null
    }

    private fun showPasswordMinimalAlert(isNotValid: Boolean) {
        binding.edPassword.error = if (isNotValid) getString(R.string.invalid_password) else null
    }

    private fun showPasswordConfirmationAlert(isNotValid: Boolean) {
        binding.edConfirmPassword.error = if (isNotValid) getString(R.string.invalid_confirm_password) else null
    }
}