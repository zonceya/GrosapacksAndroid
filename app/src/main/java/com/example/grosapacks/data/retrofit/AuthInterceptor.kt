package com.example.grosapacks.data.retrofit

import com.example.grosapacks.data.local.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val preferences: PreferencesHelper) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Endpoints that don't require authentication headers
        val publicEndpoints = listOf(
            "/auth/signup",
            "/auth/verify_signup",
            "/auth/login",
            "/auth/verify_login"
        )

        val requestBuilder = originalRequest.newBuilder()

        // Only add auth headers for non-public endpoints
        if (!publicEndpoints.contains(originalRequest.url.encodedPath)) {
            // Add Bearer token if available
            preferences.oauthId?.let { token ->
                if (token.isNotEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            }

            // Add user headers
            if (preferences.userId != -1) {
                requestBuilder.addHeader("user-id", preferences.userId.toString())
            }

            preferences.role?.let { role ->
                if (role.isNotEmpty()) {
                    requestBuilder.addHeader("user-role", role)
                }
            }
        }

        // Add common headers for all requests
        requestBuilder
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}