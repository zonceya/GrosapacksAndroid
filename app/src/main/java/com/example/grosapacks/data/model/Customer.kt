package com.example.grosapacks.data.model

import com.google.gson.annotations.SerializedName

data class Customer(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("mobile")
    val mobile: String?,

    @SerializedName("auth_mode")
    val authMode: Int = -1,
    @SerializedName("authToken")
    val authToken: String?,
)