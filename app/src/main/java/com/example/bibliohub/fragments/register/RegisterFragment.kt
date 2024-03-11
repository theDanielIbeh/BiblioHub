package com.example.bibliohub.fragments.register

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bibliohub.R
import com.example.bibliohub.databinding.FragmentRegisterBinding
import com.example.bibliohub.utils.FormFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels { RegisterViewModel.Factory }
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        with(binding) {
            firstNameEditText.requestFocus()
            firstNameEditText.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.firstNameLayout)
            }
            lastNameEditText.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.lastNameLayout)
            }
            emailEditText.doAfterTextChanged {
                FormFunctions.validateEmail(it.toString(), binding.emailLayout)
            }
            passwordEditText.doAfterTextChanged {
                FormFunctions.validatePassword(it.toString(), binding.passwordLayout)
            }
            confirmPasswordEditText.doAfterTextChanged {
                viewModel?.registerModel?.value?.password?.let { password ->
                    FormFunctions.validateConfirmPassword(
                        it.toString(),
                        password,
                        binding.confirmPasswordLayout
                    )
                }
            }
            confirmPasswordEditText.setOnEditorActionListener { _, actionId, event ->
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    btnRegister.performClick()
                }
                false
            }
            btnRegister.setOnClickListener {
                val (
                    firstName,
                    lastName,
                    email,
                    password,
                    confirmPassword
                ) = viewModel?.registerModel?.value ?: RegisterModel()
                val isFormValid = validateFields(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword
                )
                if (isFormValid) {
                    proceedToLoginScreen(email = email)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Some fields require your attention.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            existingUserAction.setOnClickListener {
                navigateToLoginScreen()
            }
        }

        return binding.root
    }

    private fun proceedToLoginScreen(email: String) {
        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) { viewModel.getUserDetails(email = email) }
            if (user == null) {
                viewModel.insertUser()
                navigateToLoginScreen()
                viewModel.resetRegisterModel()
            } else {
                Toast.makeText(
                    requireContext(),
                    "User already exists!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToLoginScreen() {
        findNavController().navigate(R.id.loginFragment)
    }

    private fun validateFields(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        val isFirstNameValid = FormFunctions.validateName(firstName, binding.firstNameLayout)
        val isLastNameValid = FormFunctions.validateName(lastName, binding.lastNameLayout)
        val isEmailValid = FormFunctions.validateEmail(email, binding.emailLayout)
        val isPasswordValid = FormFunctions.validatePassword(password, binding.passwordLayout)
        val isConfirmPasswordValid = FormFunctions.validateConfirmPassword(
            confirmPassword,
            password,
            binding.confirmPasswordLayout
        )

        return isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid
    }
}