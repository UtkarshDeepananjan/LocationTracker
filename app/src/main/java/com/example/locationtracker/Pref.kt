package com.example.locationtracker

import android.content.Context
import android.content.SharedPreferences


class Pref {
    companion object
    {
        private val PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_")
        private var sharedPreferences: SharedPreferences? = null

        private fun openPref(context: Context) {
            sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        }

        fun getBooleanValue(context: Context, key: String): Boolean {
            openPref(context)
            val result: Boolean = sharedPreferences!!.getBoolean(key, false)
            sharedPreferences = null
            return result
        }

        fun setBooleanValue(context: Context, key: String?, value: Boolean) {
            openPref(context)
            val prefsPrivateEditor: SharedPreferences.Editor = sharedPreferences!!.edit()
            prefsPrivateEditor.putBoolean(key, value)
            prefsPrivateEditor.apply()
            sharedPreferences = null
        }
    }
}