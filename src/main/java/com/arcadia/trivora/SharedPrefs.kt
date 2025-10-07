package com.arcadia.trivora

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

object SharedPrefs {
    private const val PREFS_NAME = "trivora_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_FIRST_LAUNCH = "first_launch"
    private const val KEY_SETTINGS_SOUND = "settings_sound"
    private const val KEY_SETTINGS_DIFFICULTIES = "settings_difficulties"
    private const val KEY_SETTINGS_VIBRATION = "settings_vibration"
    private const val KEY_SETTINGS_FEEDBACK = "settings_feedback"


    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Auth token management
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    // User info
    fun saveUserInfo(email: String, userId: String) {
        prefs.edit().putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_ID, userId)
            .apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    // App state
    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchCompleted() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    // Clear all data when users logout
    fun clearUserData() {
        prefs.edit().remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_ID)
            .apply()
    }

    // Settings management
    fun saveSettings(settings: Settings) {
        val difficultiesJson = Gson().toJson(settings.selectedDifficulties)
        prefs.edit()
            .putBoolean(KEY_SETTINGS_SOUND, settings.soundEnabled)
            .putString(KEY_SETTINGS_DIFFICULTIES, difficultiesJson)
            .putBoolean(KEY_SETTINGS_VIBRATION, settings.vibrationEnabled)
            .apply()
    }

    fun getSettings(): Settings {
        val difficultiesJson = prefs.getString(KEY_SETTINGS_DIFFICULTIES, null)
        val difficulties = if (difficultiesJson != null) {
            Gson().fromJson(difficultiesJson, Set::class.java) as Set<String>
        } else {
            setOf("Easy", "Medium", "Hard")
        }

        return Settings(
            soundEnabled = prefs.getBoolean(KEY_SETTINGS_SOUND, true),
            selectedDifficulties = difficulties,
            vibrationEnabled = prefs.getBoolean(KEY_SETTINGS_VIBRATION, true),

        )
    }
}