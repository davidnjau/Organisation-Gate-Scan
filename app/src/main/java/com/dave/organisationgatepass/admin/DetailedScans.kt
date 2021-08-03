package com.dave.organisationgatepass.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dave.organisationgatepass.R
import com.dave.organisationgatepass.helperClass.Formatter

class DetailedScans : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_scans)

        Formatter().customBottomNavigation(this)

    }

}