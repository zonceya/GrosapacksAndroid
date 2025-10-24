package com.example.grosapacks.data.model

import com.google.gson.annotations.SerializedName


data class OtpResponse(
        val success: Boolean,
        val message: String,
        val data: OtpData
    )

data class OtpData(
        @SerializedName("auth_token") val authToken: String,
        val otp: String
 )

