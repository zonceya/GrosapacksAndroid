package com.example.grosapacks.data.retrofit

import com.example.grosapacks.data.model.*
import retrofit2.Retrofit

class UserRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)

    // Signup - returns auth_token for OTP
    suspend fun signup(signupRequest: SignupRequest) = services.signup(signupRequest)

    // OTP Verification for Signup - returns customer + JWT token
    suspend fun verifySignupOtp(signupVerifyRequest: SignupVerifyOTPRequest) = services.verifySignupOtp(signupVerifyRequest)

    // Login - returns auth_token for OTP
    suspend fun login(loginRequest: LoginRequest) = services.login(loginRequest)

    // OTP Verification for Login - returns customer + JWT token
    suspend fun verifyLoginOtp(signupVerifyRequest: SignupVerifyOTPRequest) = services.verifyLoginOtp(signupVerifyRequest)

    // Other existing methods
    suspend fun updateUser(updateUserRequest: UpdateUserRequest) = services.updateUser(updateUserRequest)
    suspend fun updateFcmToken(notificationTokenUpdate: NotificationTokenUpdate) = services.updateFcmToken(notificationTokenUpdate)
}