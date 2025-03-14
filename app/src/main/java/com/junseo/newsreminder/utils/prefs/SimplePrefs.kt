package com.msinfotech.delivery.utils.prefs

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

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

    /// json array로 저장된 리스트를 가져온다.
    fun getList(key: String): List<String> {
        val jsonString = mPrefs.getString(key, "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        return List(jsonArray.length()) { jsonArray.getString(it) }
    }

    /// json array로 리스트를 저장한다.
    fun setList(key: String, value: List<String>) {
        val jsonArray = JSONArray(value)
        mEditor.putString(key, jsonArray.toString()).apply()
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