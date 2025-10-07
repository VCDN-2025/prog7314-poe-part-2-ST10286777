package com.arcadia.trivora

import android.Manifest
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.arcadia.trivora.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var toneGenerator: ToneGenerator
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize tone generator for system beep
        toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, 100)

        // Initialize vibrator with API check
        vibrator = getSystemService(Vibrator::class.java)

        loadCurrentSettings()
        setupClickListeners()

        binding.backButton.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(intent)
            true
        }

    }

    private fun loadCurrentSettings() {
        val settings = SharedPrefs.getSettings()

        binding.soundSwitch.isChecked = settings.soundEnabled
        binding.vibrationSwitch.isChecked = settings.vibrationEnabled


        binding.easyCheckbox.isChecked = settings.selectedDifficulties.contains("Easy")
        binding.mediumCheckbox.isChecked = settings.selectedDifficulties.contains("Medium")
        binding.hardCheckbox.isChecked = settings.selectedDifficulties.contains("Hard")
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            saveSettings()
            playFeedback()
        }

        // Test sound when sound switch is toggled
        binding.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                playSound()
            }
        }

        // Test vibration when vibration switch is toggled
        binding.vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                vibrate()
            }
        }
    }

    private fun saveSettings() {
        val selectedDifficulties = mutableSetOf<String>()
        if (binding.easyCheckbox.isChecked) selectedDifficulties.add("Easy")
        if (binding.mediumCheckbox.isChecked) selectedDifficulties.add("Medium")
        if (binding.hardCheckbox.isChecked) selectedDifficulties.add("Hard")

        if (selectedDifficulties.isEmpty()) {
            Toast.makeText(this, "Please select at least one difficulty level", Toast.LENGTH_SHORT).show()
            return
        }

        val settings = Settings(
            soundEnabled = binding.soundSwitch.isChecked,
            selectedDifficulties = selectedDifficulties,
            vibrationEnabled = binding.vibrationSwitch.isChecked,

        )

        SharedPrefs.saveSettings(settings)
        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
    }

    private fun playFeedback() {
        playSound()
        vibrate()
    }

    private fun playSound() {
        if (binding.soundSwitch.isChecked) {
            toneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 200)
        }
    }

    private fun vibrate() {
        if (binding.vibrationSwitch.isChecked && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26+ - use VibrationEffect
                val vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            } else {
                // API 25 and below
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        toneGenerator.release()
    }
}