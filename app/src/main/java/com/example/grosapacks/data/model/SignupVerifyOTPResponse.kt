package com.example.grosapacks.data.model

import com.google.gson.annotations.SerializedName

data class SignupVerifyOTPResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: SignupVerifyData?
)

data class SignupVerifyData(
    @SerializedName("customer")
    val customer: Customer,

    @SerializedName("token")
    val token: String
)