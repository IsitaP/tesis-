package com.rebottle.app.ui.screens.onBoarding

import android.content.Context

private const val PREFS_NAME = "rebottle_prefs"
private const val KEY_ONBOARDING_DONE = "onboarding_done"

object OnboardingPrefs {

    fun isOnboardingCompleted(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ONBOARDING_DONE, false)
    }

    fun setOnboardingCompleted(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
    }
}