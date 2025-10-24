package com.example.grosapacks.ui.signup

import androidx.lifecycle.*
import com.example.grosapacks.data.local.Resource
import com.example.grosapacks.data.model.SignupRequest
import com.example.grosapacks.data.model.SignupResponse
import com.example.grosapacks.data.retrofit.UserRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _signupStatus = MutableLiveData<Resource<SignupResponse?>>()
    val signupStatus: LiveData<Resource<SignupResponse?>> = _signupStatus

    fun sendSignupOtp(signupRequest: SignupRequest) {
        viewModelScope.launch {
            try {
                _signupStatus.value = Resource.loading()
                val response = userRepository.signup(signupRequest)

                if (response.isSuccessful) {
                    val signupResponse = response.body()
                    if (signupResponse?.success == true) {
                        _signupStatus.value = Resource.success(signupResponse)
                    } else {
                        val errorMessage = signupResponse?.message ?: "Signup failed"
                        _signupStatus.value = Resource.error(null, message = errorMessage)
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        409 -> "An account with this email already exists"
                        400 -> "Invalid request data"
                        422 -> "Validation failed"
                        else -> "Signup failed: ${response.message()}"
                    }
                    _signupStatus.value = Resource.error(null, message = errorMessage)
                }
            } catch (e: Exception) {
                _signupStatus.value = when (e) {
                    is UnknownHostException -> Resource.offlineError()
                    else -> Resource.error(e, message = "Network error occurred")
                }
            }
        }
    }
}