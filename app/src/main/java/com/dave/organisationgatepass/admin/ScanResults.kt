package com.dave.organisationgatepass.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dave.organisationgatepass.R
import com.dave.organisationgatepass.helperClass.Formatter
import kotlinx.android.synthetic.main.activity_scan_results.*

class ScanResults : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_results)

        Formatter().customBottomNavigation(this)

        linearArrEarly.setOnClickListener {

            val intent = Intent(this, DetailedScans::class.java)
            startActivity(intent)

        }
        linearArrLate.setOnClickListener {

            val intent = Intent(this, DetailedScans::class.java)
            startActivity(intent)

        }
        linearLeftEarly.setOnClickListener {

            val intent = Intent(this, DetailedScans::class.java)
            startActivity(intent)

        }
        linearLeftLate.setOnClickListener {

            val intent = Intent(this, DetailedScans::class.java)
            startActivity(intent)

        }

    }
}