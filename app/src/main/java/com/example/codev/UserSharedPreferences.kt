package com.example.codev

import android.content.Context
import android.content.SharedPreferences

object UserSharedPreferences {
    private const val ACCOUNT : String = "account"

    fun setAutoLogin(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("STATE", input)
        editor.commit()
    }

    fun getAutoLogin(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("STATE", "").toString()
    }

    fun setUserAccessToken(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("accessToken", input)
        editor.commit()
    }

    fun getUserAccessToken(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("accessToken", "").toString()
    }

    fun setUserRefreshToken(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("refreshToken", input)
        editor.commit()
    }

    fun getUserRefreshToken(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("refreshToken", "").toString()
    }

    fun clearUser(context: Context) {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.commit()
    }
}