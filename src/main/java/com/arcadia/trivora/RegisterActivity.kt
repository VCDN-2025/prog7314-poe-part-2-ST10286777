package com.arcadia.trivora

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arcadia.trivora.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.registerButton.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    registerUser(email, pass)
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchTokenAndRegisterWithBackend()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchTokenAndRegisterWithBackend() {
        val user = firebaseAuth.currentUser
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result.token
                if (idToken != null) {
                    registerWithBackend(idToken)
                } else {
                    Toast.makeText(this, "Failed to get authentication token", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Token fetch failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerWithBackend(idToken: String) {
        // Save token to SharedPrefs first
        SharedPrefs.saveAuthToken(idToken)
        SharedPrefs.saveUserInfo(
            firebaseAuth.currentUser?.email ?: "",
            firebaseAuth.currentUser?.uid ?: ""
        )

        // Use coroutines to call the suspend function
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.getProfile()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        val message = apiResponse.message ?: "Registration successful!"
                        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()

                        // Navigate to MainActivity
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.putExtra("username", firebaseAuth.currentUser?.email ?: "Player")
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Backend registration failed: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                        // Still navigate to MainActivity since Firebase registration worked
                        navigateToMainActivity()
                    }
                } else {
                    when (response.code()) {
                        401 -> Toast.makeText(this@RegisterActivity, "Backend registration failed - Unauthorized", Toast.LENGTH_SHORT).show()
                        500 -> Toast.makeText(this@RegisterActivity, "Server error during registration", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@RegisterActivity, "Backend error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                    // Still navigate to MainActivity since Firebase registration worked
                    navigateToMainActivity()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                // Still navigate to MainActivity since Firebase registration worked
                navigateToMainActivity()
            }
        }
    }

    // Helper function to navigate to MainActivity
    private fun navigateToMainActivity() {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}