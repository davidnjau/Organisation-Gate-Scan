package com.dave.organisationgatepass.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dave.organisationgatepass.R
import com.dave.organisationgatepass.helperClass.Formatter
import kotlinx.android.synthetic.main.activity_admin_landing.*

class AdminLanding : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_landing)

        Formatter().customBottomNavigation(this)

        cardViewAllScans.setOnClickListener {
            val intent = Intent(this, ScanResults::class.java)
            startActivity(intent)
        }
        cardViewGenerateQR.setOnClickListener {
            val intent = Intent(this, GenerateQRCode::class.java)
            startActivity(intent)
        }
        cardViewScan.setOnClickListener {
            val intent = Intent(this, AdminScanQR::class.java)
            startActivity(intent)
        }



    }
}