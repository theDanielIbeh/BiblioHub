package com.example.bibliohub.fragments.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.FragmentCartBinding
import com.example.bibliohub.utils.BaseSearchableFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartFragment : Fragment(), CartPagingDataAdapter.CartListener {

    private val viewModel: CartViewModel by viewModels { CartViewModel.Factory }
    private lateinit var binding: FragmentCartBinding
    private lateinit var adapter: CartPagingDataAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel

        initRecycler()

        return binding.root
    }

    private fun initRecycler() {
        viewModel.initOrderDetails {
            adapter = CartPagingDataAdapter(requireContext(), this, viewModel.userOrderDetails)
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.itemAnimator = DefaultItemAnimator()
            binding.recyclerView.adapter = adapter
            (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false

            viewModel.getProductsByIds(
                viewModel.userOrderDetails.map { it.productId }
            ).collectLatest { productPagingData ->
                    adapter.submitData(lifecycle, productPagingData)
            }
        }
    }

    override fun addOrUpdateCart(product: Product, quantity: Int) {
        viewModel.createOrUpdateOrderDetails(product = product, quantity = quantity)
    }

    override fun deleteFromCart(productId: Int) {
        viewModel.deleteFromCart(productId)
    }
}