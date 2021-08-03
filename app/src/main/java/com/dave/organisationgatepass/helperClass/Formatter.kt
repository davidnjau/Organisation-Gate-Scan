package com.dave.organisationgatepass.helperClass

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.dave.organisationgatepass.R
import com.dave.organisationgatepass.admin.AdminLanding
import com.dave.organisationgatepass.admin.AdminScanQR
import com.dave.organisationgatepass.admin.GenerateQRCode
import com.dave.organisationgatepass.admin.ScanResults
import com.google.android.material.bottomnavigation.BottomNavigationView

class Formatter {

    private lateinit var context: Context


    fun customBottomNavigation(context1: Context){

        context = context1

        val bottomNavigation = (context1 as Activity).findViewById<View>(R.id.bottom_navigation)
        val bottomNavigationView : BottomNavigationView = bottomNavigation.findViewById(R.id.bottom_navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener1)

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

}