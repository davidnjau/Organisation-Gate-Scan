package com.dave.organisationgatepass.retrofit.interfaceClass

import com.dave.organisationgatepass.retrofit.helperClasses.DbStaffResults
import com.dave.organisationgatepass.retrofit.helperClasses.DbUserDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path

interface Interface {

    @GET("/api/v1/users/get_all_staff/")
    fun getAllStaff(): Call<DbStaffResults>

    @GET("/api/v1/users/get_user_detail/{user_id}")
    fun getStaffDetails(@Path("user_id") id: String): Call<DbUserDetails>


}