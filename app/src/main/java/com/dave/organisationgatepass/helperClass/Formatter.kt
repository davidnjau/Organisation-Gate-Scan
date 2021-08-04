package com.dave.organisationgatepass.helperClass

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.dave.organisationgatepass.R
import com.dave.organisationgatepass.admin.AdminLanding
import com.dave.organisationgatepass.admin.AdminScanQR
import com.dave.organisationgatepass.admin.GenerateQRCode
import com.dave.organisationgatepass.admin.ScanResults
import com.google.android.material.bottomnavigation.BottomNavigationView

class Formatter {

    private lateinit var context: Context


    fun customWaiterToolbar(context: Context, title: String){

        val customToolbar = (context as Activity).findViewById<View>(R.id.customToolbar)
        val tvToolBarTitle = customToolbar.findViewById<TextView>(R.id.tvToolBarTitle)
        val backButton = customToolbar.findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {

            when (title) {
                context.resources.getString(R.string.all_scans) -> {
                    val intent = Intent(context, AdminLanding::class.java)
                    context.startActivity(intent)
                }
                context.resources.getString(R.string.arrived_early) -> {
                    val intent = Intent(context, ScanResults::class.java)
                    context.startActivity(intent)
                }
                context.resources.getString(R.string.arrived_late) -> {
                    val intent = Intent(context, ScanResults::class.java)
                    context.startActivity(intent)
                }
                context.resources.getString(R.string.left_early) -> {
                    val intent = Intent(context, ScanResults::class.java)
                    context.startActivity(intent)
                }
                context.resources.getString(R.string.left_late) -> {
                    val intent = Intent(context, ScanResults::class.java)
                    context.startActivity(intent)
                }
                context.resources.getString(R.string.generate_qr) -> {
                    val intent = Intent(context, AdminLanding::class.java)
                    context.startActivity(intent)
                }
                context.resources.getString(R.string.scan_qr_code) -> {
                    val intent = Intent(context, AdminLanding::class.java)
                    context.startActivity(intent)
                }
                else ->{
                    val intent = Intent(context, AdminLanding::class.java)
                    context.startActivity(intent)
                }
            }

        }
        tvToolBarTitle.text = title
    }


    fun customBottomNavigation(context1: Context){

        context = context1

        val bottomNavigation = (context1 as Activity).findViewById<View>(R.id.bottom_navigation)
        val bottomNavigationView : BottomNavigationView = bottomNavigation.findViewById(R.id.bottom_navigationView)
        checkNavigation(bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener1)


    }

    private fun checkNavigation(bottomNavigationView: BottomNavigationView) {

        val sharedpreferences = context.getSharedPreferences(
            context.resources.getString(R.string.app_name_use), Context.MODE_PRIVATE)
        val pageName = sharedpreferences.getString("pageName", null)
        if (pageName != null){

            if (pageName == context.getString(R.string.home)){
                bottomNavigationView.selectedItemId = R.id.navigation_home
            }
            if (pageName == context.getString(R.string.scan_qr_code)){
                bottomNavigationView.selectedItemId = R.id.navigation_camera
            }
            if (pageName == context.getString(R.string.all_scans)){
                bottomNavigationView.selectedItemId = R.id.navigation_scans
            }
            if (pageName == context.getString(R.string.generate_qr)){
                bottomNavigationView.selectedItemId = R.id.navigation_generate
            }


        }




    }

    private var navigationItemSelectedListener1 =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {

                    val intent = Intent(context, AdminLanding::class.java)
                    context.startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_scans -> {
                    val intent = Intent(context, ScanResults::class.java)
                    context.startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_generate -> {
                    val intent = Intent(context, GenerateQRCode::class.java)
                    context.startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_camera -> {
                    val intent = Intent(context, AdminScanQR::class.java)
                    context.startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }

            }
            false
        }

    fun getHeaders(context: Context): HashMap<String, String> {


        val preferencesDetails = SharedPreferenceStorage(
            context,
            context.resources.getString(R.string.app_name))
        val getData: HashMap<String, String> = preferencesDetails.getSavedData("profile")
        val access_token = getData["accessToken"]
        var stringStringMap = HashMap<String, String>()
        stringStringMap["Authorization"] = " Bearer $access_token"

        return stringStringMap
    }

    fun addNavIds(pageName : String, context: Context){

        val sharedpreferences = context.getSharedPreferences(
            context.resources.getString(R.string.app_name_use), Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()

        editor.putString("pageName", pageName)
        editor.apply()

    }
}