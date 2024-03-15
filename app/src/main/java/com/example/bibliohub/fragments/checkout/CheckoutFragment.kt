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
import com.example.bibliohub.databinding.FragmentCheckoutBinding
import com.example.bibliohub.utils.Constants
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

        //check if user has existing order details
        viewModel.loggedInUser.observe(viewLifecycleOwner) { userInfo ->
            //get current order info else create new order
            lifecycleScope.launch {
                viewModel.currentOrder =
                    userInfo?.id?.let { viewModel.orderRepository.getStaticActiveOrderByUserId(it) }
            }
        }

        setBinding()

        return binding.root
    }

    private fun setBinding() {
        with(binding) {
            addressEditText.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.addressLayout)
            }
            postcodeEditText.doAfterTextChanged {
                FormFunctions.validatePostcode(it.toString(), binding.postcodeLayout)
            }
            cardNumberEditText.doAfterTextChanged {
                FormFunctions.validateCardNumber(it.toString(), binding.cardNumberLayout)
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
                    completePayment(address, postcode)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Some fields require your attention.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun completePayment(address: String, postcode: String) {
        lifecycleScope.launch {
            viewModel.currentOrder?.address = address
            viewModel.currentOrder?.postcode = postcode
            viewModel.updateOrder()
            viewModel.resetCheckoutModel()
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
        val isPostcodeValid = FormFunctions.validatePostcode(postcode, binding.postcodeLayout)
        val isCardNumberValid = FormFunctions.validateCardNumber(cardNumber, binding.cardNumberLayout)
        val isExpiryValid = FormFunctions.validateExpiryDate(expiry, binding.expiryLayout)
        val isCVVValid = FormFunctions.validateCVV(cvv, binding.cvvLayout)
        val isPINValid = FormFunctions.validatePIN(pin, binding.pinLayout)


        return isAddressValid && isPostcodeValid && isCardNumberValid && isExpiryValid && isCVVValid && isPINValid
    }
}