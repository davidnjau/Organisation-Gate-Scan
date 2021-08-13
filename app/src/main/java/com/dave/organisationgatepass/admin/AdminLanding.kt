package com.dave.organisationgatepass.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dave.organisationgatepass.R
import com.dave.organisationgatepass.helperClass.DbScanned
import com.dave.organisationgatepass.helperClass.Formatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_admin_landing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdminLanding : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_landing)


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

    override fun onStart() {
        super.onStart()

        Formatter().addNavIds(getString(R.string.home), this)
        Formatter().customBottomNavigation(this)

        val formatter = SimpleDateFormat("ddMMyyyy")
        val date = Date()
        val todayDate = formatter.format(date)

        getStats(todayDate)



    }

    private fun getStats(todayDate: String) {

        CoroutineScope(Dispatchers.IO).launch {

            val database = FirebaseDatabase.getInstance().reference
            val myRef = database.child(todayDate)
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    val arrivedList = ArrayList<String>()
                    val departureList = ArrayList<String>()

                    if (dataSnapshot.exists()){
                        val scannedList = dataSnapshot.children.mapNotNull { it.getValue(DbScanned::class.java) }.toList()
                        for (scans in scannedList){

                            val arrivalTime = scans.arrivalTime
                            val departureTime = scans.departureTime
                            val userName = scans.userName

                            if (departureTime != null)
                                departureList.add(departureTime)

                            if (arrivalTime != null)
                                arrivedList.add(arrivalTime)


                        }

                    }

                    CoroutineScope(Dispatchers.Main).launch {

                        val arrivedNo = arrivedList.size.toString()
                        val departureNo = departureList.size.toString()

                        tvArrivals.text = arrivedNo
                        tvDepartures.text = departureNo

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })

        }

    }
}