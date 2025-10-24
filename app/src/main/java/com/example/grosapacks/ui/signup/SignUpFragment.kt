package com.example.grosapacks.ui.signup

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
import com.example.grosapacks.data.model.SignupRequest
import com.example.grosapacks.databinding.FragmentSignUpBinding
import com.example.grosapacks.utils.AppConstants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.String

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val signUpViewModel: SignUpViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        setupClickListeners()
        observeSignupStatus()
    }

    private fun setupClickListeners() {
        binding.buttonRegister.setOnClickListener {
            val nameInput = binding.editName.text.toString().trim()
            val emailInput = binding.editEmail.text.toString().trim()
            val passwordInput = binding.editPassword.text.toString()
            val confirmPassword = binding.editConfirmPassword.text.toString()

            when {
                nameInput.isEmpty() -> {
                    Toast.makeText(requireContext(), "Name is blank", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                emailInput.isEmpty() -> {
                    Toast.makeText(requireContext(), "Email is blank", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                passwordInput.isEmpty() -> {
                    Toast.makeText(requireContext(), "Password is blank", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                !isValidPassword(passwordInput) -> {
                    Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                passwordInput != confirmPassword -> {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else -> {
                    // Call API for signup
                    val request = SignupRequest(
                        name = nameInput,
                        email = emailInput,
                        password = passwordInput,
                       passwordConfirmation = confirmPassword
                    )
                    signUpViewModel.sendSignupOtp(request)
                }
            }
        }
    }

    private fun observeSignupStatus() {
        signUpViewModel.signupStatus.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> showLoading(true)
                Resource.Status.SUCCESS -> {
                    showLoading(false)
                    val signupResponse = resource.data
                    if (signupResponse?.success == true) {
                        handleSuccessfulSignup(signupResponse.data?.authToken, binding.editEmail.text.toString().trim())
                    } else {
                        Toast.makeText(requireContext(), signupResponse?.message ?: "Signup failed", Toast.LENGTH_SHORT).show()
                    }
                }
                Resource.Status.ERROR -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), resource.message ?: "Signup failed", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.OFFLINE_ERROR -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.EMPTY -> showLoading(false)
            }
        }
    }

    private fun handleSuccessfulSignup(authToken: String?, email: String) {
        if (authToken != null) {
            preferencesHelper.tempOauthId = authToken

            val bundle = Bundle().apply {
                putString(AppConstants.AUTH_TOKEN, authToken)
                putString(AppConstants.VERIFICATION_TYPE, "SIGNUP")
                putString("USER_EMAIL", email)
            }

            findNavController().navigate(R.id.action_signUpFragment_to_otpFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Signup failed - no auth token", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6  // Removed the @ requirement
    }

    private fun showLoading(show: Boolean) {
        binding.buttonRegister.isEnabled = !show
        binding.buttonRegister.text = if (show) "Signing up..." else "Sign Up"
    }
}