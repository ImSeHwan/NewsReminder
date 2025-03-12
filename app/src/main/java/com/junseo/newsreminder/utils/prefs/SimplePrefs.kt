package com.msinfotech.delivery.utils.prefs

import android.content.Context
import android.content.SharedPreferences

open class SimplePrefs(context: Context, name: String) {

    private var mName: String = name
    private var mPrefs: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private var mEditor: SharedPreferences.Editor = mPrefs.edit()

    fun get(key: String): String? {
        return mPrefs.getString(key, null)
    }

    fun get(key: String, defValue: String): String? {
        return mPrefs.getString(key, defValue)
    }

    fun get(key: String, defValue: Int): Int {
        return mPrefs.getInt(key, defValue)
    }

    fun get(key: String, defValue: Long): Long {
        return mPrefs.getLong(key, defValue)
    }

    fun get(key: String, defValue: Float): Float {
        return mPrefs.getFloat(key, defValue)
    }

    fun get(key: String, defValue: Boolean): Boolean {
        return mPrefs.getBoolean(key, defValue)
    }

    fun set(key: String, value: String) {
        mEditor.putString(key, value).apply()
    }

    fun set(key: String, value: Int) {
        mEditor.putInt(key, value).apply()
    }

    fun set(key: String, value: Long) {
        mEditor.putLong(key, value).apply()
    }

    fun set(key: String, value: Float) {
        mEditor.putFloat(key, value).apply()
    }

    fun set(key: String, value: Boolean) {
        mEditor.putBoolean(key, value).apply()
    }

    fun apply() {
        mEditor.apply()
    }

    fun clear() {
        mEditor.clear().apply()
    }

    fun getWrappedSharedPreferences(): SharedPreferences {
        return mPrefs
    }

    fun getWrapperSharedPreferencesEditor(): SharedPreferences.Editor {
        return mEditor
    }

    fun getSharedPreferencesName(): String {
        return mName
    }
}