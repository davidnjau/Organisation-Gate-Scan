package com.dave.organisationgatepass.helperClass

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class SharedPreferenceStorage(context1: Context, myPreferences1: String) {

    private var context: Context = context1
    private var  myPreferences = myPreferences1
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var  editor: SharedPreferences.Editor


    fun saveData(
        hashMap: HashMap<String, String>, keyName: String) {

        sharedPreference = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
        editor = sharedPreference.edit()

        val gson = Gson()
        val hashMapString = gson.toJson(hashMap)

        sharedPreference.edit().putString(keyName, hashMapString).apply()
    }

    fun getSavedData(keyName: String): HashMap<String, String> {
        sharedPreference = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)

        val gson = Gson()
        val storedHashMapString = sharedPreference.getString(keyName, "No Data Found")
        val type: Type = object : TypeToken<HashMap<String, String>>() {}.type

        return gson.fromJson<HashMap<String, String>>(storedHashMapString, type)
    }

}