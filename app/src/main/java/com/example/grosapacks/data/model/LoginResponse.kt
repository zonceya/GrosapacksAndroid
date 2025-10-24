package com.example.grosapacks.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: LoginData?
)

data class LoginData(
    @SerializedName("auth_token")
    val authToken: String,

    @SerializedName("otp")
    val otp: String? // Only in development
)