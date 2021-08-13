package com.dave.organisationgatepass.admin

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.dave.organisationgatepass.R
import com.dave.organisationgatepass.adapter.TimeStatusAdapter
import com.dave.organisationgatepass.adapter.UsersAdapter
import com.dave.organisationgatepass.helperClass.DbScanned
import com.dave.organisationgatepass.helperClass.DbScanned1
import com.dave.organisationgatepass.helperClass.Formatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_detailed_scans.*
import kotlinx.android.synthetic.main.activity_generate_qrcode.*
import kotlinx.android.synthetic.main.activity_scan_results.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class DetailedScans : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_scans)

        Formatter().customBottomNavigation(this)
        //Check to have this update automatically

        preferences = getSharedPreferences(
            resources.getString(R.string.app_name_use),
            MODE_PRIVATE
        )
        editor = preferences.edit()

        linearLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)

    }

    override fun onStart() {
        super.onStart()

        val tabName = preferences.getString("tabName", null)
        val selectedDate = preferences.getString("selectedDate", null)

        if (tabName != null && selectedDate != null){

            Formatter().customWaiterToolbar(this, tabName)
            tvTimeStatus.text = tabName

            getStats(selectedDate,tabName)

        }else{

            val intent = Intent(this, AdminLanding::class.java)
            startActivity(intent)
            finish()

        }



    }

    private fun getStats(selectedDate: String, tabName: String) {

        /**
         * Check saved arrived time and
         */

        CoroutineScope(Dispatchers.IO).launch {

            val parser = SimpleDateFormat("HH:mm")

            val database = FirebaseDatabase.getInstance().reference
            val myRef = database.child(selectedDate)
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    val dataList = ArrayList<DbScanned1>()
                    if (dataSnapshot.exists()){
                        val scannedList = dataSnapshot.children.mapNotNull { it.getValue(DbScanned::class.java) }.toList()
                        val key = dataSnapshot.key
                        Log.e("-*-*-* ",key.toString())
                        for (scans in scannedList){

                            val arrivalTime = scans.arrivalTime
                            val departureTime = scans.departureTime
                            val userName = scans.userName

                            val morning = parser.parse("09:00")
                            val evening = parser.parse("17:00")

                            if (tabName == getString(R.string.arrived_early)){
                                if (arrivalTime != null) {
                                    val userArrival = parser.parse(arrivalTime)
                                    if (userArrival != null){
                                        if (userArrival.before(morning)){
                                            val dbScanned = DbScanned1(key,arrivalTime, departureTime, userName)
                                            dataList.add(dbScanned)
                                        }

                                    }
                                }
                            }
                            if (tabName == getString(R.string.arrived_late)){
                                if (arrivalTime != null) {
                                    val userArrival = parser.parse(arrivalTime)
                                    if (userArrival != null){
                                        if (userArrival.after(morning)){

                                            val dbScanned = DbScanned1(key,arrivalTime, departureTime, userName)
                                            dataList.add(dbScanned)
                                        }

                                    }
                                }
                            }
                            if (tabName == getString(R.string.left_early)){
                                if (departureTime != null) {
                                    val userDeparture = parser.parse(departureTime)
                                    if (userDeparture != null){
                                        if (userDeparture.before(evening)){
                                            val dbScanned = DbScanned1(key,arrivalTime, departureTime, userName)
                                            dataList.add(dbScanned)
                                        }
                                    }
                                }
                            }
                            if (tabName == getString(R.string.left_late)){
                                if (departureTime != null) {
                                    val userDeparture = parser.parse(departureTime)
                                    if (userDeparture != null){
                                        if (userDeparture.after(evening)){
                                            val dbScanned = DbScanned1(key,arrivalTime, departureTime, userName)
                                            dataList.add(dbScanned)
                                        }
                                    }
                                }
                            }

                        }

                    }

                    CoroutineScope(Dispatchers.Main).launch {

                        val timeStatusAdapter = TimeStatusAdapter(
                            dataList, application)
                        recyclerView.adapter = timeStatusAdapter

                        val timeStatusNo = dataList.size.toString()
                        tvTimeStatusNo.text = timeStatusNo

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