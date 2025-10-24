package com.example.grosapacks.ui.otp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.example.grosapacks.R
import com.example.grosapacks.data.local.PreferencesHelper
import com.example.grosapacks.data.local.Resource
import com.example.grosapacks.data.model.SignupVerifyOTPRequest
import com.example.grosapacks.databinding.FragmentOtpBinding
import com.example.grosapacks.utils.AppConstants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class OtpFragment : Fragment() {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private var countDownTimer: CountDownTimer? = null
    private var isResendEnabled = false
    private val otpInputs = mutableListOf<EditText>()

    private val preferencesHelper: PreferencesHelper by inject()
    private val otpViewModel: OtpViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        // Get data from arguments
        val userEmail = arguments?.getString("USER_EMAIL") ?: ""
        val authToken = arguments?.getString(AppConstants.AUTH_TOKEN) ?: ""
        val verificationType = arguments?.getString(AppConstants.VERIFICATION_TYPE) ?: ""

        // Update description with actual email
        updateDescriptionWithEmail(userEmail)

        initializeOtpInputs()
        setupOtpAutoMove()
        setupClickListeners()
        observeVerificationStatus()
        startResendTimer()
    }

    private fun updateDescriptionWithEmail(email: String) {
        val colorHex = getColorGrosaPackHex()
        val htmlText = "Enter the security code we just sent to<br><font color='$colorHex'>$email</font>"
        binding.descriptionText.text =
            HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun getColorGrosaPackHex(): String {
        val color = ContextCompat.getColor(requireContext(), R.color.colorGrosaPack)
        return String.format("#%06X", 0xFFFFFF and color)
    }

    private fun initializeOtpInputs() {
        otpInputs.apply {
            add(binding.otpInput1)
            add(binding.otpInput2)
            add(binding.otpInput3)
            add(binding.otpInput4)
            add(binding.otpInput5)
            add(binding.otpInput6)
        }

        binding.otpInput1.requestFocus()
        showCursor(binding.otpInput1)
    }

    private fun setupOtpAutoMove() {
        otpInputs.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1) {
                        if (index < otpInputs.size - 1) {
                            otpInputs[index + 1].requestFocus()
                            showCursor(otpInputs[index + 1])
                        } else {
                            editText.clearFocus()
                        }
                    } else if (s?.isEmpty() == true && index > 0) {
                        otpInputs[index - 1].requestFocus()
                        showCursor(otpInputs[index - 1])
                        otpInputs[index - 1].text.clear()
                    }

                    updateVerifyButtonState()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnClickListener {
                editText.requestFocus()
                showCursor(editText)
            }

            editText.setOnLongClickListener {
                editText.requestFocus()
                showCursor(editText)
                true
            }
        }
    }

    private fun showCursor(editText: EditText) {
        editText.isCursorVisible = true
        editText.setSelection(editText.text.length)
    }

    private fun updateVerifyButtonState() {
        val isOtpComplete = otpInputs.all { it.text?.length == 1 }
        binding.buttonVerify.isEnabled = isOtpComplete
        binding.buttonVerify.alpha = if (isOtpComplete) 1.0f else 0.5f
    }

    private fun setupClickListeners() {
        binding.buttonVerify.setOnClickListener {
            verifyOtp()
        }

        binding.textResendOtpButton.setOnClickListener {
            if (isResendEnabled) {
                resendOtp()
            }
        }
    }

    private fun observeVerificationStatus() {
        // Observe signup verification
        otpViewModel.signupVerifyStatus.observe(viewLifecycleOwner) { resource ->
            handleVerificationResponse(resource)
        }

        // Observe login verification
        otpViewModel.loginVerifyStatus.observe(viewLifecycleOwner) { resource ->
            handleVerificationResponse(resource)
        }
    }

    private fun handleVerificationResponse(resource: Resource<*>) {
        when (resource.status) {
            Resource.Status.LOADING -> showLoading(true)
            Resource.Status.SUCCESS -> {
                showLoading(false)
                Toast.makeText(requireContext(), "Verification Successful!", Toast.LENGTH_SHORT).show()

                // Extract and save user data from the response
                saveUserCredentials(resource.data)
                navigateToMain()
            }
            Resource.Status.ERROR -> {
                showLoading(false)
                Toast.makeText(requireContext(), resource.message ?: "Verification failed", Toast.LENGTH_SHORT).show()
                resetOtpFields()
            }
            Resource.Status.OFFLINE_ERROR -> {
                showLoading(false)
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                resetOtpFields()
            }
            Resource.Status.EMPTY -> showLoading(false)
        }
    }

    private fun saveUserCredentials(data: Any?) {
        when (data) {
            is com.example.grosapacks.data.model.SignupVerifyOTPResponse -> {
                data.data?.let { verifyData ->
                    // Use the helper method to save with authMode
                    preferencesHelper.saveUserAfterVerification(
                        userId = verifyData.customer.id,
                        name = verifyData.customer.name,
                        email = verifyData.customer.email,
                        userToken = verifyData.token, // JWT token
                        mobile = verifyData.customer.mobile,
                        authMode = verifyData.customer.authMode
                    )
                }
            }
            is com.example.grosapacks.data.model.LoginVerifyOTPResponse -> {
                data.data?.let { verifyData ->
                    // Use the helper method to save with authMode
                    preferencesHelper.saveUserAfterVerification(
                        userId = verifyData.customer.id,
                        name = verifyData.customer.name,
                        email = verifyData.customer.email,
                        userToken = verifyData.token, // JWT token
                        mobile = verifyData.customer.mobile,
                        authMode = verifyData.customer.authMode
                    )
                }
            }
        }
    }

    private fun startResendTimer() {
        val totalTime = 60 * 1000L
        val interval = 1000L

        countDownTimer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.textTimer.text = "${secondsRemaining}s"
            }

            override fun onFinish() {
                isResendEnabled = true
                binding.textResendOtp.visibility = View.GONE
                binding.textTimer.visibility = View.GONE
                binding.textResendOtpButton.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun verifyOtp() {
        val otp = otpInputs.joinToString("") { it.text.toString() }

        if (otp.length == 6) {
            val authToken = arguments?.getString(AppConstants.AUTH_TOKEN) ?: ""
            val verificationType = arguments?.getString(AppConstants.VERIFICATION_TYPE) ?: ""
            val userEmail = arguments?.getString("USER_EMAIL") ?: ""

            val request = SignupVerifyOTPRequest(
                authToken = authToken,
                otp = otp
            )

            // Call appropriate API based on verification type
            if (verificationType == "SIGNUP") {
                otpViewModel.verifySignupOtp(request)
            } else {
                otpViewModel.verifyLoginOtp(request)
            }
        } else {
            Toast.makeText(requireContext(), "Please enter complete OTP", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resendOtp() {
        if (isResendEnabled) {
            // TODO: Implement actual resend OTP API call
            binding.textResendOtpButton.text = "Sending..."
            binding.textResendOtpButton.isEnabled = false

            simulateResendOtp()

            isResendEnabled = false
            binding.textResendOtpButton.visibility = View.GONE
            binding.textResendOtp.visibility = View.VISIBLE
            binding.textTimer.visibility = View.VISIBLE

            startResendTimer()
        }
    }

    private fun simulateResendOtp() {
        // TODO: Replace with actual API call
        binding.root.postDelayed({
            Toast.makeText(requireContext(), "OTP sent successfully!", Toast.LENGTH_SHORT).show()
            binding.textResendOtpButton.text = "Resend OTP"
            binding.textResendOtpButton.isEnabled = true
        }, 1000)
    }

    private fun resetOtpFields() {
        otpInputs.forEach {
            it.text.clear()
            it.isCursorVisible = false
        }
        binding.otpInput1.requestFocus()
        showCursor(binding.otpInput1)
        updateVerifyButtonState()

        binding.buttonVerify.text = "Verify"
        binding.buttonVerify.isEnabled = true
    }

    private fun showLoading(show: Boolean) {
        binding.buttonVerify.isEnabled = !show
        binding.buttonVerify.text = if (show) "Verifying..." else "Verify"
    }

    private fun navigateToMain() {
        val intent = Intent(requireContext(), com.example.grosapacks.MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}