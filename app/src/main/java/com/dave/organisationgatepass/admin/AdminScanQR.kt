package com.dave.organisationgatepass.admin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.dave.organisationgatepass.R
import com.dave.organisationgatepass.helperClass.DbScanned
import com.dave.organisationgatepass.helperClass.Formatter
import com.google.android.material.chip.Chip
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_admin_scan_qr.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class AdminScanQR : AppCompatActivity() {

    private lateinit var chipStatus: Chip
    private lateinit var codeScanner: CodeScanner
    private val MY_PERMISSIONS_REQUEST_CODE = 123

    private lateinit var database : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_scan_qr)

        database = FirebaseDatabase.getInstance().reference

        Formatter().customWaiterToolbar(this, resources.getString(R.string.scan_qr_code))

        chipStatus = findViewById(R.id.chipStatus)



        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view1)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                chipStatus.text = "Scanning.."

                val scannedResult = it.text
                formatData(scannedResult)
//                val json = JSONObject(scannedResult)
//
//                linearChip.visibility = View.GONE
//                linearDetails.visibility = View.VISIBLE
//
//                val name = json.getString("userName")
//                val role = json.getString("roles")
//                val roleName = role.substring(0, role.length-1)


            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                checkForPermission()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        radioGroup.setOnCheckedChangeListener { _, _ ->
            linearScan.visibility = View.VISIBLE
            codeScanner.startPreview()

        }

        btnScan.setOnClickListener {

            codeScanner.startPreview()

            linearChip.visibility = View.VISIBLE
            linearDetails.visibility = View.GONE

//            radioGroup.clearCheck()

        }



    }

    private fun formatData(scannedResult: String?) {

        if (scannedResult != null){

            val userName = scannedResult.substring(scannedResult.indexOf("FN") + 3,
                scannedResult.indexOf("TITLE")-1)
            val designation = scannedResult.substring(scannedResult.indexOf("TITLE") + 6, scannedResult.indexOf("ORG"))

            linearChip.visibility = View.GONE
            linearDetails.visibility = View.VISIBLE

            val sdf = SimpleDateFormat("hh:mm a")
            val time: String = sdf.format(Date())

            tvTime.text = time
            tvName.text = userName
            tvRole.text = designation

            val newKey = Formatter().hashWithMD5(userName)

            val formatter = SimpleDateFormat("ddMMyyyy")
            val date = Date()
            val todayDate = formatter.format(date)

            val selectedId = radioGroup.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(selectedId)
            val selectedStatus = radioButton.text
            var  timeStatus = ""
            if (selectedStatus == "Arrival Time"){
                timeStatus = "arrivalTime"
            }
            if (selectedStatus == "Departure Time"){
                timeStatus = "departureTime"
            }

            val myRef = database.child(todayDate).child(newKey)
            myRef.child("userName").setValue(userName)
            myRef.child(timeStatus).setValue(time)

            Toast.makeText(this, "$selectedStatus saved successfully.", Toast.LENGTH_SHORT).show()

        }

    }



    private fun checkForPermission() {

        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            +
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            != PackageManager.PERMISSION_GRANTED
        ) {

            // Do something, when permissions not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            ) {
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                val builder = AlertDialog.Builder(this)
                builder.setMessage(
                    "Read and Write External" +
                            " Storage permissions are required for the app."
                )
                builder.setTitle("Please grant these permissions")
                builder.setPositiveButton(
                    "OK"
                ) { _, _ ->
                    ActivityCompat.requestPermissions(this,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE

                        ),
                        MY_PERMISSIONS_REQUEST_CODE
                    )
                }
                builder.setNeutralButton(
                    "Cancel"
                ) { _, _ ->
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    MY_PERMISSIONS_REQUEST_CODE
                )
            }
        }

    }

    override fun onStart() {
        super.onStart()

        checkForPermission()
        Formatter().addNavIds(getString(R.string.scan_qr_code), this)
        Formatter().customBottomNavigation(this)

    }
}