package com.dave.organisationgatepass.retrofit.requests

import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import com.dave.organisationgatepass.helperClass.Formatter
import com.dave.organisationgatepass.helperClass.UrlData
import com.dave.organisationgatepass.retrofit.builder.RetrofitBuilder
import com.dave.organisationgatepass.retrofit.helperClasses.DbStaffResults
import com.dave.organisationgatepass.retrofit.helperClasses.DbUserData
import com.dave.organisationgatepass.retrofit.helperClasses.DbUserDetails
import com.dave.organisationgatepass.retrofit.interfaceClass.Interface
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap


class RetrofitCalls {


    fun userList(context: Context) = runBlocking{
        fetchUsers(context)
    }
    private suspend fun fetchUsers(context: Context):List<DbUserData> {

//        val stringStringMap = Formatter().getHeaders(context)

        var dbStaffResultsList = ArrayList<DbUserData>()


        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {

            val baseUrl = context.getString(UrlData.BASE_URL_JAVA.message)
            val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)
            val callSync: Call<DbStaffResults> = apiService.getAllStaff()

            try {
                val response: Response<DbStaffResults> = callSync.execute()
                if (response.body() != null){

                    val resultsList = response.body()!!.results
                    for (users in resultsList){

                        val userData = users.userData
                        val roleList = users.rolesList

                        val dbUserData = DbUserData(userData, roleList)
                        dbStaffResultsList.add(dbUserData)

                    }



                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }.join()

        return dbStaffResultsList

    }


    fun userDetail(context: Context, userId :String) = runBlocking{
        fetchUserDetail(context, userId)
    }
    private suspend fun fetchUserDetail(context: Context, userId: String):DbUserDetails {

//        val stringStringMap = Formatter().getHeaders(context)

        var dbUserDetails = DbUserDetails(null)

        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {

            val baseUrl = context.getString(UrlData.BASE_URL_JAVA.message)
            val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)
            val callSync: Call<DbUserDetails> = apiService.getStaffDetails(userId)

            try {
                val response: Response<DbUserDetails> = callSync.execute()
                if (response.body() != null){

                    val results = response.body()!!.details
                    val userData = results?.userData
                    val roleList = results?.rolesList

                    if (userData != null && roleList != null){

                        val dbUserData = DbUserData(userData, roleList)
                        dbUserDetails = DbUserDetails(dbUserData)
                    }


                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }.join()

        return dbUserDetails

    }




}