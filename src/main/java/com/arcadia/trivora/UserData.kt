package com.arcadia.trivora

data class UserData(
    val id: String? = null,
    val email: String,
    val name: String? = null

)

data class UserProfile(
    val user: UserData?
)