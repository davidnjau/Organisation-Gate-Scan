package com.dave.organisationgatepass.admin

import android.content.Context
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dave.organisationgatepass.R
import com.dave.organisationgatepass.adapter.UsersAdapter
import com.dave.organisationgatepass.helperClass.Formatter
import com.dave.organisationgatepass.retrofit.requests.RetrofitCalls
import kotlinx.android.synthetic.main.activity_generate_qrcode.*
import android.view.WindowManager
import androidmads.library.qrgenearator.QRGContents

import androidmads.library.qrgenearator.QRGEncoder
import com.google.zxing.WriterException

import android.content.ContentValues
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dave.organisationgatepass.helperClass.QRGEncoderLocal
import com.dave.organisationgatepass.retrofit.helperClasses.DbUserDetails
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class GenerateQRCode : AppCompatActivity() {

    private var retrofitCalls = RetrofitCalls()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var sharedpreferences :SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_qrcode)

        sharedpreferences = getSharedPreferences(resources.getString(R.string.app_name_use),
            Context.MODE_PRIVATE)
        editor = sharedpreferences.edit()

        Formatter().customBottomNavigation(this)
        linearLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        recyclerViewUsers.layoutManager = linearLayoutManager
        recyclerViewUsers.setHasFixedSize(true)

        cardViewGenerateQR.setOnClickListener {

            val getIdList = getPositionSet()
            for (items in getIdList){

                val ids = items
                val userData = retrofitCalls.userDetail(this, ids)
                generateQRCode(userData)



            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateQRCode(userData: DbUserDetails) {

        val details = userData.details?.userData
        val rolesList = userData.details?.rolesList

        if (details != null && rolesList != null) {

            val email = details.email
            val userName = details.name
            val id = details.id
            val phoneNumber = details.phone_number
            val name = details.name
            val roles = rolesList

            var json = JSONObject()
            json.put("email", email)
            json.put("userName", userName)
            json.put("id", id)
            json.put("phoneNumber", phoneNumber)
            json.put("name", name)
            json.put("roles", roles)


            val manager = getSystemService(WINDOW_SERVICE) as WindowManager
            val display = manager.defaultDisplay
            val point = Point()
            display.getSize(point)

            val width = point.x
            val height = point.y

            var dimen = if (width < height) width else height
            dimen = dimen * 3 / 4
            val qrgEncoder = QRGEncoderLocal(json.toString(), null, QRGContents.Type.TEXT, dimen, this)


            try {
                val bitmap = qrgEncoder.encodeAsBitmap()

                saveMediaToStorage(bitmap, userName)


            } catch (e: WriterException) {
                Toast.makeText(this, "Please try again..", Toast.LENGTH_SHORT).show()
                Log.e("Tag", e.toString())
            }
        }

    }

    override fun onStart() {
        super.onStart()

        populateRecyclerView()


    }

    private fun populateRecyclerView() {
        val userList = retrofitCalls.userList(this)


        val usersAdapter = UsersAdapter(
            userList, application
        )
        recyclerViewUsers.adapter = usersAdapter

    }

    private fun getPositionSet() : ArrayList<String>{

        val checkPosList = ArrayList<String>()

        val plupaQuery = sharedpreferences.getStringSet("checkedNames", null)
        if (plupaQuery != null) {
            val list = ArrayList<String>(plupaQuery)
            for (items in list){
                checkPosList.add(items)
            }
        }
        return checkPosList
    }

    fun saveMediaToStorage(bitmap: Bitmap, staffName: String) {
        //Generating a file name
        val filename = "$staffName ${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            this.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            this.toast("Saved to Photos")
        }
    }
    private fun Context.toast(text: String?) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}