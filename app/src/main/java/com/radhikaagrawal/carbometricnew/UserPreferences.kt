package com.radhikaagrawal.carbometricnew

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.edit

class UserPreferences(context: Context) {

  private val sharedPreferences: SharedPreferences =
    context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

  companion object {
    private const val PREF_NAME = "carbometric_user_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_HAS_COMPLETED_DETAILS = "has_completed_details"
  }

  fun setLoggedIn(isLoggedIn: Boolean) {
    sharedPreferences.edit { putBoolean(KEY_IS_LOGGED_IN, isLoggedIn) }
  }

  fun isLoggedIn(): Boolean {
    return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
  }

  fun setUserEmail(email: String) {
    sharedPreferences.edit { putString(KEY_USER_EMAIL, email) }
  }

  fun getUserEmail(): String? {
    return sharedPreferences.getString(KEY_USER_EMAIL, null)
  }

  fun setUserName(name: String) {
    sharedPreferences.edit { putString(KEY_USER_NAME, name) }
  }

  fun getUserName(): String? {
    return sharedPreferences.getString(KEY_USER_NAME, null)
  }

  fun setHasCompletedDetails(completed: Boolean) {
    sharedPreferences.edit { putBoolean(KEY_HAS_COMPLETED_DETAILS, completed) }
  }

  fun hasCompletedDetails(): Boolean {
    return sharedPreferences.getBoolean(KEY_HAS_COMPLETED_DETAILS, false)
  }

  fun clearUserData() {
    sharedPreferences.edit { clear() }
  }

  fun logout() {
    // Clear all user data and Firebase auth
    clearUserData()
    FirebaseAuth.getInstance().signOut()
  }
}
