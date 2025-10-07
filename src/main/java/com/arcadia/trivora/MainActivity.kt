package com.arcadia.trivora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.arcadia.trivora.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import androidx.lifecycle.Lifecycle

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setupWindowInsets()
        setupRecyclerView()
        setupObservers()
        setupUsername()

        // Load categories when activity starts
        viewModel.loadCategories()

        // Handle navigation item clicks
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_random_quiz -> {
                    val intent = Intent(this@MainActivity, RandomQuizActivity::class.java)
                    startActivity(intent)
                    true
                    true
                }
                R.id.nav_settings -> {
                    // Load SettingsFragment
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    startActivity(intent)

                    true
                }
                else -> false
            }
        }

    }

    private fun setupUsername() {
        // Get user data from SharedPrefs
        val userEmail = SharedPrefs.getUserEmail() ?: "Not available"

        binding.tvUsername.text = userEmail

        binding.tvUsername.setOnClickListener {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("SELECTED_CATEGORY", category)
            }
            startActivity(intent)
        }

        binding.categoriesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = categoryAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        // Observe categories with StateFlow
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect { categories ->
                    categoryAdapter.submitList(categories)
                    if (categories.isNotEmpty()) {
                        binding.emptyState.visibility = View.GONE
                    } else {
                        binding.emptyState.visibility = View.VISIBLE
                    }
                }
            }
        }

        // Observe loading state with StateFlow
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    if (isLoading) {
                        binding.emptyState.visibility = View.GONE
                    }
                }
            }
        }

        // Observe errors with StateFlow
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { error ->
                    error?.let {
                        Toast.makeText(this@MainActivity, "Error: $it", Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                        binding.emptyState.visibility = View.VISIBLE
                        binding.emptyState.text = "Failed to load categories\nTap to retry"
                    }
                }
            }
        }

        // Setup retry on empty state click
        binding.emptyState.setOnClickListener {
            viewModel.loadCategories()
        }
    }
}