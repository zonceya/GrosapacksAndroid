package com.example.grosapacks.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.grosapacks.R
import com.example.grosapacks.data.local.PreferencesHelper
import com.example.grosapacks.data.local.Resource
import com.example.grosapacks.data.model.LoginRequest
import com.example.grosapacks.databinding.FragmentLoginBinding
import com.example.grosapacks.utils.AppConstants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val preferencesHelper: PreferencesHelper by inject()
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        // REMOVED the auto-redirect check - always show login screen
        setListeners()
        observeLoginStatus()
    }

    private fun setListeners() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.editEmail.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()

            when {
                email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(requireContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    binding.editPassword.error = "Password must be at least 6 characters"
                }
                else -> {
                    val request = LoginRequest(email = email, password = password)
                    loginViewModel.login(request)
                }
            }
        }

        binding.textSignupLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun observeLoginStatus() {
        loginViewModel.performLoginStatus.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> showLoading(true)
                Resource.Status.SUCCESS -> {
                    showLoading(false)
                    val loginResponse = resource.data
                    if (loginResponse?.success == true) {
                        handleSuccessfulLogin(loginResponse.data?.authToken)
                    } else {
                        Toast.makeText(requireContext(), loginResponse?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
                Resource.Status.ERROR -> {
                    showLoading(false)
                    val errorMsg = resource.message ?: "Login failed"
                    // You could show more specific messages based on error type
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
                Resource.Status.OFFLINE_ERROR -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.EMPTY -> showLoading(false)
            }
        }
    }

    private fun handleSuccessfulLogin(authToken: String?) {
        if (authToken != null) {
            preferencesHelper.tempOauthId = authToken
            val userEmail = binding.editEmail.text.toString().trim()

            val bundle = Bundle().apply {
                putString(AppConstants.AUTH_TOKEN, authToken)
                putString(AppConstants.VERIFICATION_TYPE, "LOGIN")
                putString("USER_EMAIL", userEmail)
            }

            findNavController().navigate(R.id.action_loginFragment_to_otpFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(show: Boolean) {
        binding.buttonLogin.isEnabled = !show
        binding.buttonLogin.text = if (show) "Logging in..." else "Login"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}