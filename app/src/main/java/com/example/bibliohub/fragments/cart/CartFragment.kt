package com.example.bibliohub.fragments.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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

class CartFragment : BaseSearchableFragment<Product>(), CartPagingDataAdapter.CartListener {

    companion object {
        private val TAG = CartFragment::getTag
    }

    private val viewModel: CartViewModel by viewModels { CartViewModel.Factory }
    private lateinit var binding: FragmentCartBinding
    private lateinit var adapter: CartPagingDataAdapter
    override var viewModelFilterText: String? = null
    override var searchCallback: ((String) -> Unit)? = null
    override var searchButton: ImageButton? = null
    override var searchText: TextInputEditText? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel

        searchButton = binding.imageButtonStopSearch
        searchText = binding.etSearch
    }

    override fun initCompulsoryVariables() {
    }

    override fun returnBindingRoot(): View {
        return binding.root
    }

    override fun setBinding() {
        binding.btn.setOnClickListener { refresh() }
    }

    override fun initRecycler() {
        viewModel.initOrderDetails {
            adapter = CartPagingDataAdapter(requireContext(), this, viewModel.userOrderDetails)
            lifecycleScope.launch {
                var idList = mutableListOf<Int>()
                viewModel.userOrderDetails.forEach { it -> idList.add(it.productId) }
                viewModel.products(idList).collectLatest { pagingData ->
                    // submitData suspends until loading this generation of data stops
                    // so be sure to use collectLatest {} when presenting a Flow<PagingData>
                    adapter.submitData(lifecycle, pagingData)
                }
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.itemAnimator = DefaultItemAnimator()
            binding.recyclerView.adapter = adapter
            (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
        }
    }

    override fun addOrUpdateCart(product: Product, quantity: Int) {
        viewModel.createOrUpdateOrderDetails(product = product, quantity = quantity)
    }

    override fun deleteFromCart(productId: Int) {
        viewModel.deleteFromCart(productId)
    }

    private fun refresh() {
        viewModel.insertProducts()
    }
}