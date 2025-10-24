package com.example.grosapacks.data.model

import com.google.gson.annotations.SerializedName

data class LoginVerifyOTPResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: LoginVerifyData?
)

data class LoginVerifyData(
    @SerializedName("customer")
    val customer: Customer,

    @SerializedName("token")
    val token: String
)

