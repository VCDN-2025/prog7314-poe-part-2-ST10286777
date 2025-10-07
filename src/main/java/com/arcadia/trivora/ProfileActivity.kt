package com.arcadia.trivora

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.arcadia.trivora.databinding.ActivityProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setupUserInfo()
        setupLogoutButton()

        // Handle navigation item clicks
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_random_quiz -> {
                    val intent = Intent(this@ProfileActivity, RandomQuizActivity::class.java)
                    startActivity(intent)

                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this@ProfileActivity, SettingsActivity::class.java)
                    startActivity(intent)

                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this@ProfileActivity, ProfileActivity::class.java)
                    startActivity(intent)

                    true
                }
                else -> false
            }
        }
    }

    private fun setupUserInfo() {
        // Get user data from SharedPrefs
        val userEmail = SharedPrefs.getUserEmail() ?: "Not available"

        binding.tvUserEmail.text = userEmail

        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            user.displayName?.let { name ->
            }
        }
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes, Logout") { dialog, which ->
                performLogout()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogout() {
        try {
            //Cleared SharedPrefs data
            SharedPrefs.clearUserData()

            //Sign out from Firebase
            firebaseAuth.signOut()

            //Sign out from Google
            GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()

            //Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}