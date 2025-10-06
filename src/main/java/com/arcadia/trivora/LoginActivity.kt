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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001 // request code for Google sign-in

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Enables an instance of FireAuth
        firebaseAuth = FirebaseAuth.getInstance()

        //Navigates to RegisterActivity
        binding.textView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // from google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleSignInBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

            //Allows user to login using Email and Password
            binding.button.setOnClickListener {
                val email = binding.emailEt.text.toString()
                val pass = binding.passET.text.toString()

                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                fetchTokenAndCallBackend()
                            } else {
                                Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }
        override fun onStart() {
            super.onStart()
            if (firebaseAuth.currentUser != null) {
                fetchTokenAndCallBackend()
            }

        }

    //Handles the result of Google Login
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

    //Sign in to Firebase with Google token
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("username", firebaseAuth.currentUser?.email ?: "Player")
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Google sign-in failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //Fetches Firebase Token
    private fun fetchTokenAndCallBackend() {
        val user = firebaseAuth.currentUser
        user?.getIdToken(true)?.addOnSuccessListener { result ->
            val idToken = "Bearer ${result.token}" // Format for backend

            // Call Node.js backend using Retrofit
            RetrofitClient.instance.getProfile(idToken)
                .enqueue(object : Callback<ProfileResponse> {
                    override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                        if (response.isSuccessful) {
                            val message = response.body()?.message ?: "No message"
                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()

                            // Navigate to MainActivity after successful backend response
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("username", user.email ?: "Player")
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Unauthorized", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}

