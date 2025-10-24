package com.example.grosapacks.ui.login

import androidx.lifecycle.*
import com.example.grosapacks.data.local.Resource
import com.example.grosapacks.data.model.LoginRequest
import com.example.grosapacks.data.model.LoginResponse
import com.example.grosapacks.data.retrofit.UserRepository
import com.example.grosapacks.data.model.UserModel
import kotlinx.coroutines.launch

import java.net.UnknownHostException


class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _performLogin = MutableLiveData<Resource<LoginResponse?>>() // Change to LoginResponse
    val performLoginStatus: LiveData<Resource<LoginResponse?>>
        get() = _performLogin

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            try {
                _performLogin.value = Resource.loading()
                val response = userRepository.login(loginRequest)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        _performLogin.value = Resource.success(loginResponse)
                    } else {
                        _performLogin.value = Resource.error(Exception("Empty response body"))
                    }
                } else {
                    // You might want to parse error body for better messages
                    val errorMessage = try {
                        response.errorBody()?.string() ?: "Login failed: ${response.code()}"
                    } catch (e: Exception) {
                        "Login failed: ${response.code()}"
                    }
                    _performLogin.value = Resource.error(Exception(errorMessage))
                }

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    _performLogin.value = Resource.offlineError()
                } else {
                    _performLogin.value = Resource.error(e)
                }
            }
        }
    }

}