package com.dave.organisationgatepass.admin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.dave.organisationgatepass.helperClass.Formatter
import com.dave.organisationgatepass.retrofit.helperClasses.DbUserDetails
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_admin_scan_qr.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AdminScanQR : AppCompatActivity() {

    private lateinit var chipStatus: Chip
    private lateinit var codeScanner: CodeScanner
    private val MY_PERMISSIONS_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_scan_qr)


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
                val json = JSONObject(scannedResult)

                linearChip.visibility = View.GONE
                linearDetails.visibility = View.VISIBLE

                val name = json.getString("userName")
                val role = json.getString("roles")
                val roleName = role.substring(0, role.length-1)

                val sdf = SimpleDateFormat("HH:mm:ss")
                val str: String = sdf.format(Date())

                tvTime.text = str
                tvName.text = name
                tvRole.text = roleName

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