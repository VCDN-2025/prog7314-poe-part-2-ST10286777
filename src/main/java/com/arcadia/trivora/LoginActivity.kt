package com.arcadia.trivora

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arcadia.trivora.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPrefs
        SharedPrefs.init(this)

        // Enables an instance of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Navigates to RegisterActivity
        binding.textView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Allows users to login using Google
        binding.googleSignInBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Allows user to login using Email and Password
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                loginWithEmailPassword(email, pass)
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginWithEmailPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchTokenAndCallBackend()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()

        // Auto-login if user is already logged in
        if (firebaseAuth.currentUser != null) {
            fetchTokenAndCallBackend()
        }
    }

    // Handles the result of Google Login
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Sign in to Firebase with Google token
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    fetchTokenAndCallBackend()
                } else {
                    Toast.makeText(this, "Google sign-in failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Fetches Firebase Token
    private fun fetchTokenAndCallBackend() {
        val user = firebaseAuth.currentUser
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                val idToken = result.token

                if (idToken != null) {
                    callBackendWithToken(idToken)
                } else {
                    Toast.makeText(this, "Failed to get authentication token", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Token fetch failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callBackendWithToken(idToken: String) {
        // Save token first
        SharedPrefs.saveAuthToken(idToken)

        // Use coroutines to call the suspend function
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.getProfile()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        // Get user data from the profile response
                        val userProfile = apiResponse.data
                        userProfile?.user?.let { user ->
                            val userId = user.id ?: firebaseAuth.currentUser?.uid ?: ""
                            SharedPrefs.saveUserInfo(user.email, userId)
                        }

                        val message = apiResponse.message ?: "Welcome back!"
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()

                        // Navigate to MainActivity
                        navigateToMainActivity()

                    } else {
                        Toast.makeText(this@LoginActivity, "API error: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                        // Save email from Firebase as fallback
                        saveEmailFromFirebase()
                        navigateToMainActivity()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "HTTP error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    // Save email from Firebase as fallback
                    saveEmailFromFirebase()
                    navigateToMainActivity()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                // Save email from Firebase as fallback
                saveEmailFromFirebase()
                navigateToMainActivity()
            }
        }
    }

    // Save email from Firebase
    private fun saveEmailFromFirebase() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.email?.let { email ->
            val userId = currentUser.uid ?: ""
            SharedPrefs.saveUserInfo(email, userId)
        }
    }


    private fun navigateToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)

        val username = when {
            SharedPrefs.getUserEmail() != null -> SharedPrefs.getUserEmail()!!
            firebaseAuth.currentUser?.email != null -> firebaseAuth.currentUser?.email!!
            else -> "Player"
        }

        intent.putExtra("username", username)
        startActivity(intent)
        finish()
    }
}