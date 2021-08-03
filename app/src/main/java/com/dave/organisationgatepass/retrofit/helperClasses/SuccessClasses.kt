package com.dave.organisationgatepass.retrofit.helperClasses

data class DbUsers (
    var email: String,
    var id: String,
    var phone_number: String,
    var name: String,
    var verified: Boolean)


data class DbUserData(
    var userData : DbUsers,
    var rolesList: List<String>
)

data class DbStaffResults(
    var count: Int,
    var next: String?,
    var previous: String?,
    var results: List<DbUserData>
)

data class DbUserDetails(
    var details: DbUserData?
)