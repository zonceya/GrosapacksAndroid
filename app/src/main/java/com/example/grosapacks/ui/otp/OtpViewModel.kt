package com.example.grosapacks.ui.otp

import androidx.lifecycle.*
import com.example.grosapacks.data.local.Resource
import com.example.grosapacks.data.model.LoginVerifyOTPResponse
import com.example.grosapacks.data.model.SignupVerifyOTPRequest
import com.example.grosapacks.data.model.SignupVerifyOTPResponse
import com.example.grosapacks.data.retrofit.UserRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class OtpViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Separate LiveData for different response types
    private val _signupVerifyStatus = MutableLiveData<Resource<SignupVerifyOTPResponse?>>()
    val signupVerifyStatus: LiveData<Resource<SignupVerifyOTPResponse?>> = _signupVerifyStatus

    private val _loginVerifyStatus = MutableLiveData<Resource<LoginVerifyOTPResponse?>>()
    val loginVerifyStatus: LiveData<Resource<LoginVerifyOTPResponse?>> = _loginVerifyStatus

    /**
     * Verifies OTP for signup
     */
    fun verifySignupOtp(request: SignupVerifyOTPRequest) {
        viewModelScope.launch {
            try {
                _signupVerifyStatus.value = Resource.loading()
                val response = userRepository.verifySignupOtp(request)

                if (response.isSuccessful) {
                    val verifyResponse = response.body()
                    if (verifyResponse?.success == true) {
                        _signupVerifyStatus.value = Resource.success(verifyResponse)
                    } else {
                        val errorMessage = verifyResponse?.message ?: "OTP verification failed"
                        _signupVerifyStatus.value = Resource.error(null, message = errorMessage)
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid OTP or expired"
                        401 -> "OTP verification failed"
                        else -> "Verification failed: ${response.message()}"
                    }
                    _signupVerifyStatus.value = Resource.error(null, message = errorMessage)
                }
            } catch (e: Exception) {
                _signupVerifyStatus.value = when (e) {
                    is UnknownHostException -> Resource.offlineError()
                    else -> Resource.error(e, message = "Network error occurred")
                }
            }
        }
    }

    /**
     * Verifies OTP for login
     */
    fun verifyLoginOtp(request: SignupVerifyOTPRequest) {
        viewModelScope.launch {
            try {
                _loginVerifyStatus.value = Resource.loading()
                val response = userRepository.verifyLoginOtp(request)

                if (response.isSuccessful) {
                    val verifyResponse = response.body()
                    if (verifyResponse?.success == true) {
                        _loginVerifyStatus.value = Resource.success(verifyResponse)
                    } else {
                        val errorMessage = verifyResponse?.message ?: "Login verification failed"
                        _loginVerifyStatus.value = Resource.error(null, message = errorMessage)
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid OTP or expired"
                        401 -> "Login verification failed"
                        else -> "Verification failed: ${response.message()}"
                    }
                    _loginVerifyStatus.value = Resource.error(null, message = errorMessage)
                }
            } catch (e: Exception) {
                _loginVerifyStatus.value = when (e) {
                    is UnknownHostException -> Resource.offlineError()
                    else -> Resource.error(e, message = "Network error occurred")
                }
            }
        }
    }
}