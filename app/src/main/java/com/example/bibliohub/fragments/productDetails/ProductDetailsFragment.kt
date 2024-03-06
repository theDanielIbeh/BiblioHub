package com.example.bibliohub.fragments.productDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.bibliohub.R
import com.example.bibliohub.databinding.FragmentProductDetailsBinding

class ProductDetailsFragment : Fragment() {

    private val viewModel: ProductDetailsViewModel by viewModels { ProductDetailsViewModel.Factory }
    private lateinit var binding: FragmentProductDetailsBinding
    private val args: ProductDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        viewModel.product = args.product

        viewModel.product.imgSrc?.let {
            Glide.with(requireContext()).load(it)
                .into(binding.memberImageView)
        }

        initializeOrderDetails()
        return binding.root
    }

    private fun initializeOrderDetails() {
        viewModel.initOrderDetails {
            //to be used to track order info
            val userItemInCart =
                viewModel.userOrderDetails.firstOrNull { it.productId == viewModel.product.id }
            //get existing order quantity else assign to 0
            var orderQuantity = userItemInCart?.quantity ?: 0
            updateOrderQtyView(orderQuantity)
            val isInCart = userItemInCart != null
            val cartButtonText =
                if (isInCart) {
                    ContextCompat.getString(requireContext(), R.string.update_cart)
                } else {
                    ContextCompat.getString(requireContext(), R.string.add_to_cart)
                }
            val cartButtonColor =
                if (isInCart) {
                    requireContext().getColor(R.color.darkBlue)
                } else {
                    requireContext().getColor(R.color.lightGreen)
                }
            //disable main button so user doesn't add to cart with qty of 0
            isMainButtonEnabled(false)

            //setup cart qty control
            binding.addBtn.setOnClickListener {
                val currentDisplayedQty =
                    binding.quantityEditText.text.toString().toIntOrNull() ?: 0
                if (currentDisplayedQty < viewModel.product.quantity) {
                    updateOrderQtyView(currentDisplayedQty + 1)
                }
            }
            binding.subtractBtn.setOnClickListener {
                // to make sure order qty doesn't go below 0
                val currentDisplayedQty =
                    binding.quantityEditText.text.toString().toIntOrNull() ?: 0
                if (currentDisplayedQty == 0) {
                    return@setOnClickListener
                }
                updateOrderQtyView(currentDisplayedQty - 1)
            }
            binding.quantityEditText.doAfterTextChanged {
                //check quantity change and activate button
                val enteredQuantity = it.toString().toIntOrNull() ?: 0
                //check if what user entered is equal to size of item in cart
                if (enteredQuantity == (userItemInCart?.quantity ?: 0)) {
                    isMainButtonEnabled(false)
                    return@doAfterTextChanged
                }
                isMainButtonEnabled(
                    //if item is in cart activate button when quantity changes
                    if (isInCart) {
                        true
                    } else {
                        //if item not in cart ensure quantity is more than 0
                        //before user can add to cart
                        enteredQuantity > 0
                    }
                )
                //update order quantity
                orderQuantity = enteredQuantity
            }
            binding.addToCartButton.text = cartButtonText
            binding.addToCartButton.setBackgroundColor(cartButtonColor)
            binding.addToCartButton.setOnClickListener {
                viewModel.createOrUpdateOrderDetails(
                    viewModel.product,
                    orderQuantity
                )
            }
        }
    }

    private fun updateOrderQtyView(orderQty: Int) {
        binding.quantityEditText.setText(orderQty.toString())
    }

    private fun isMainButtonEnabled(isEnabled: Boolean) {
        binding.addToCartButton.isEnabled = isEnabled
    }
}