package com.example.plantmanager.prefs

import android.content.Context

class PreferencesManager(context: Context) {
    private val prefs = context.getSharedPreferences("plant_manager_prefs", Context.MODE_PRIVATE)

    fun getReminderLeadHours(): Int = prefs.getInt("reminder_lead_hours", 2)
    fun setReminderLeadHours(hours: Int) {
        prefs.edit().putInt("reminder_lead_hours", hours).apply()
    }

    fun getProfileName(): String = prefs.getString("profile_name", "") ?: ""
    fun setProfileName(name: String) {
        prefs.edit().putString("profile_name", name).apply()
    }

    fun getProfileEmail(): String = prefs.getString("profile_email", "") ?: ""
    fun setProfileEmail(email: String) {
        prefs.edit().putString("profile_email", email).apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)
    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", loggedIn).apply()
    }
    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }
    fun setCurrentUserId(id: Int) {
        prefs.edit().putInt("current_user_id", id).apply()
    }
    fun getCurrentUserId(): Int? {
        val id = prefs.getInt("current_user_id", -1)
        return if (id == -1) null else id
    }
}
