package com.example.grosapacks.data.model

import com.google.gson.annotations.SerializedName

data class SignupVerifyOTPRequest(
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("otp")
    val otp: String
)
