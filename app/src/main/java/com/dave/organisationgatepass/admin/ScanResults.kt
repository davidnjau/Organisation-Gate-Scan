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
import kotlinx.android.synthetic.main.activity_scan_results.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.widget.DatePicker

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.SharedPreferences

import com.dave.organisationgatepass.security.MainActivity


class ScanResults : AppCompatActivity() {

    private lateinit var calendar: Calendar
    private var  year = 0
    private var  month:Int = 0
    private var  day:Int = 0

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_results)

        Formatter().customWaiterToolbar(this, resources.getString(R.string.all_scans))

        calendar = Calendar.getInstance()
        year=calendar.get(Calendar.YEAR)
        month=calendar.get(Calendar.MONTH)
        day=calendar.get(Calendar.DAY_OF_MONTH)

        preferences = getSharedPreferences(
            resources.getString(R.string.app_name_use),
            MODE_PRIVATE
        )
        editor = preferences.edit()

        linearArrEarly.setOnClickListener {

            val intent = Intent(this, DetailedScans::class.java)
            startActivity(intent)
            setSelectedTab(getString(R.string.arrived_early))

        }
        linearArrLate.setOnClickListener {

            val intent = Intent(this, DetailedScans::class.java)
            startActivity(intent)
            setSelectedTab(getString(R.string.arrived_late))

        }
        linearLeftEarly.setOnClickListener {

            val intent = Intent(this, DetailedScans::class.java)
            startActivity(intent)
            setSelectedTab(getString(R.string.left_early))
        }
        linearLeftLate.setOnClickListener {

            val intent = Intent(this, DetailedScans::class.java)
            startActivity(intent)
            setSelectedTab(getString(R.string.left_late))
        }
        tvCalendar.setOnClickListener {

            val datePickerDialog = DatePickerDialog(this@ScanResults,
                { _, year, month, dayOfMonth ->

                    var chosenMonth = ""
                    val selectedMonth = month + 1
                    chosenMonth = if (selectedMonth < 10){
                        "0$selectedMonth"
                    }else{
                        selectedMonth.toString()
                    }

                    val selectedDate = "${dayOfMonth}$chosenMonth$year"
                    getStats(selectedDate)

                    val setSelectedDate = "${dayOfMonth}/$chosenMonth/$year"

                    setDate(setSelectedDate)


                }, year, month, day
            )
            //shows DatePickerDialog
            //shows DatePickerDialog
            datePickerDialog.show()

        }

    }

    private fun setSelectedTab(tabName: String) {

        editor.putString("tabName", tabName)
        editor.apply()

    }

    override fun onStart() {
        super.onStart()

        Formatter().addNavIds(getString(R.string.all_scans), this)
        Formatter().customBottomNavigation(this)

        val formatter = SimpleDateFormat("ddMMyyyy")
        val date = Date()
        val selectedDate = formatter.format(date)
        getStats(selectedDate)

        val formatter1 = SimpleDateFormat("dd/MM/yyyy")
        val todayDate = formatter1.format(date)

        setDate(todayDate)


    }

    private fun setDate(selectedDate: String) {

        tvCalendar.text = selectedDate

    }

    private fun getStats(selectedDate: String) {

        /**
         * Check saved arrived time and
         */

        editor.putString("selectedDate", selectedDate)
        editor.apply()

        CoroutineScope(Dispatchers.IO).launch {

            val parser = SimpleDateFormat("HH:mm")

            val database = FirebaseDatabase.getInstance().reference
            val myRef = database.child(selectedDate)
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    val arrivedEarlyList = ArrayList<String>()
                    val arrivedLateList = ArrayList<String>()

                    val departureEarlyList = ArrayList<String>()
                    val departureLateList = ArrayList<String>()

                    if (dataSnapshot.exists()){
                        val scannedList = dataSnapshot.children.mapNotNull { it.getValue(DbScanned::class.java) }.toList()
                        for (scans in scannedList){

                            val arrivalTime = scans.arrivalTime
                            val departureTime = scans.departureTime
                            val userName = scans.userName

                            val morning = parser.parse("09:00")
                            val evening = parser.parse("17:00")

                            if (arrivalTime != null) {
                                val userArrival = parser.parse(arrivalTime)
                                if (userArrival != null){
                                    if (userArrival.before(morning)){
                                        arrivedEarlyList.add(userArrival.toString())
                                    }
                                    if (userArrival.after(morning)){
                                        arrivedLateList.add(userArrival.toString())
                                    }
                                }
                            }
                            if (departureTime != null) {
                                val userDeparture = parser.parse(departureTime)
                                if (userDeparture != null){
                                    if (userDeparture.before(evening)){
                                        departureEarlyList.add(userDeparture.toString())
                                    }
                                    if (userDeparture.after(evening)){
                                        departureLateList.add(userDeparture.toString())
                                    }
                                }
                            }


                        }

                    }

                    CoroutineScope(Dispatchers.Main).launch {

                        val arrivedEarly = arrivedEarlyList.size.toString()
                        val arrivedLate = arrivedLateList.size.toString()

                        val departureEarly = departureEarlyList.size.toString()
                        val departureLate = departureLateList.size.toString()

                        tvArrivedEarly.text = arrivedEarly
                        tvArrivedLate.text = arrivedLate

                        tvDeparturesEarly.text = departureEarly
                        tvDepartureLate.text = departureLate


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