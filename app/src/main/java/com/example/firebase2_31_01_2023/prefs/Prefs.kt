package com.example.firebase2_31_01_2023.prefs

import android.content.Context

class Prefs(c: Context) {
    val storage = c.getSharedPreferences("Articulos", 0)

    fun guardarEmail(email: String) {
        storage.edit().putString("email", email).apply()
    }

    fun obtenerEmail(): String {
        return storage.getString("email", "") ?: ""
    }

    fun deleteAll() {
        storage.edit().clear().apply()
    }
}