package com.example.bibliohub.fragments.checkout

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
import com.example.bibliohub.databinding.FragmentCheckoutBinding
import com.example.bibliohub.utils.FormFunctions
import kotlinx.coroutines.launch

class CheckoutFragment : Fragment() {

    private val viewModel: CheckoutViewModel by viewModels { CheckoutViewModel.Factory }
    private lateinit var binding: FragmentCheckoutBinding
    private var alertModalFragment = AlertModalFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        with(binding) {
            addressEditText.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.addressLayout)
            }
            postcodeEditText.doAfterTextChanged {
                FormFunctions.validatePostcode(it.toString(), binding.postcodeLayout)
            }
            cardNumberEditText.doAfterTextChanged {
                FormFunctions.validateEmail(it.toString(), binding.cardNumberLayout)
            }
            expiryEditText.doAfterTextChanged {
                FormFunctions.validateExpiryDate(it.toString(), binding.expiryLayout)
            }
            cvvEditText.doAfterTextChanged {
                FormFunctions.validateCVV(it.toString(), binding.cvvLayout)
            }
            pinEditText.doAfterTextChanged {
                FormFunctions.validatePIN(it.toString(), binding.pinLayout)
            }
            pinEditText.setOnEditorActionListener { _, actionId, event ->
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    btnRegister.performClick()
                }
                false
            }
            btnRegister.setOnClickListener {
                val (
                    address,
                    postcode,
                    cardNumber,
                    expiry,
                    cvv,
                    pin
                ) = viewModel?.checkoutModel?.value ?: CheckoutModel()
                val isFormValid = validateFields(
                    address = address,
                    postcode = postcode,
                    cardNumber = cardNumber,
                    expiry = expiry,
                    cvv = cvv,
                    pin = pin
                )
                if (isFormValid) {
                    completePayment()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Some fields require your attention.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return binding.root
    }

    private fun completePayment() {
        lifecycleScope.launch {
            alertModalFragment.show(requireActivity().supportFragmentManager, "AlertModalFragment")
        }
    }

    private fun validateFields(
        address: String,
        postcode: String,
        cardNumber: String,
        expiry: String,
        cvv: String,
        pin: String
    ): Boolean {
        val isAddressValid = FormFunctions.validateName(address, binding.addressLayout)
        val isPostcodeValid = FormFunctions.validateName(postcode, binding.postcodeLayout)
        val isCardNumberValid = FormFunctions.validateEmail(cardNumber, binding.cardNumberLayout)
        val isExpiryValid = FormFunctions.validatePassword(expiry, binding.expiryLayout)
        val isCVVValid = FormFunctions.validatePassword(cvv, binding.cvvLayout)
        val isPINValid = FormFunctions.validatePassword(pin, binding.pinLayout)


        return isAddressValid && isPostcodeValid && isCardNumberValid && isExpiryValid && isCVVValid && isPINValid
    }
}