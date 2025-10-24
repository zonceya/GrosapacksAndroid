package com.example.grosapacks.data.model

import com.google.gson.annotations.SerializedName

// Updated UserModel for signup
data class UserModel(
        @SerializedName("id")
        val userId: Int? = null,             // optional, can be null for new signup
        @SerializedName("email")
        val email: String? = null,
        @SerializedName("mobile")
        val mobile: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("oauthId")
        var oauthId: String? = null,
        @SerializedName("role")
        val role: String? = null,
        @SerializedName("password")
        val password: String? = null          // added password for signup
)

// Optional: Updated request class for signup
data class UpdateUserRequest(
        @SerializedName("userModel")
        val userModel: UserModel
)
