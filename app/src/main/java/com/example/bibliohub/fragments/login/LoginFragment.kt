package com.example.bibliohub.fragments.login

import android.os.Bundle
import android.util.Log
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
import com.example.bibliohub.databinding.FragmentLoginBinding
import com.example.bibliohub.utils.FormFunctions.Companion.validateEmail
import com.example.bibliohub.utils.FormFunctions.Companion.validatePassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginFragment : Fragment() {
    companion object {
        private val TAG = LoginFragment::class.java.name
    }

    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        Log.d(TAG, TAG)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        with(binding) {
            usernameEditText.doAfterTextChanged {
                validateEmail(it.toString(), binding.usernameLayout)
            }
            passwordEditText.doAfterTextChanged {
                validatePassword(it.toString(), binding.passwordLayout)
            }
            passwordEditText.setOnEditorActionListener { _, actionId, event ->
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    btnLogin.performClick()
                }
                false
            }
            btnLogin.setOnClickListener {
                val (email, password) = viewModel?.loginModel?.value ?: LoginModel()
                val isFormValid = validateFields(email = email, password = password)
                if (isFormValid) {
                    proceedToHomePage(email, password)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Some fields require your attention.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            newUserAction.setOnClickListener {
                navigateToRegisterScreen()
            }
        }

//        lifecycleScope.launch {
//            viewModel.loginModel.collectLatest {
//                Log.d("LogIn", it.email)
//                validateFields(email = it.email, password = it.password)
//            }
//        }

        return binding.root
    }

    private fun FragmentLoginBinding.proceedToHomePage(
        email: String,
        password: String
    ) {
        lifecycleScope.launch {
            val user =
                withContext(Dispatchers.IO) { viewModel?.getUserDetails(email = email) }
            if (user != null) {
                if (user.password == password) {
                    viewModel?.resetLoginModel()
                    viewModel?.savePreferences(user)
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    binding.passwordLayout.error = "Incorrect password"
                }
            } else {
                binding.usernameLayout.error = "This email is not registered"
            }
        }
    }

    private fun navigateToRegisterScreen() {
        findNavController().navigate(R.id.registerFragment)
    }

    private fun validateFields(email: String, password: String): Boolean {
        val isEmailValid = validateEmail(email, binding.usernameLayout)
        val isPasswordValid = validatePassword(password, binding.passwordLayout)

        return isEmailValid && isPasswordValid
    }
}