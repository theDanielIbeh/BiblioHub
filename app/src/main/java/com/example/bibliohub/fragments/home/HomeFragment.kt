package com.example.bibliohub.fragments.home

import android.os.Bundle
import android.util.Log
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
import com.example.bibliohub.databinding.FragmentHomeBinding
import com.example.bibliohub.utils.BaseSearchableFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : BaseSearchableFragment<Product>(), HomePagingDataAdapter.HomeListener {

    companion object {
        private val TAG = HomeFragment::getTag
    }

    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory }
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomePagingDataAdapter
    override var viewModelFilterText: String? = null
    override var searchCallback: ((String) -> Unit)? = null
    override var searchButton: ImageButton? = null
    override var searchText: TextInputEditText? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel

        searchButton = binding.imageButtonStopSearch
        searchText = binding.etSearch
    }

    override fun initCompulsoryVariables() {
        viewModelFilterText = viewModel.mFilterText
        searchCallback = { it -> viewModel.search(it) }
    }

    override fun returnBindingRoot(): View {
        return binding.root
    }

    override fun setBinding() {
        binding.btn.setOnClickListener { refresh() }
    }

    override fun initRecycler() {
        viewModel.initOrderDetails {
            adapter = HomePagingDataAdapter(requireContext(), this, viewModel.userOrderDetails)
            lifecycleScope.launch {
                viewModel.products.collectLatest { pagingData ->
                    // submitData suspends until loading this generation of data stops
                    // so be sure to use collectLatest {} when presenting a Flow<PagingData>
                    adapter.submitData(lifecycle, pagingData) } }
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.itemAnimator = DefaultItemAnimator()
            binding.recyclerView.adapter = adapter
            (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
            viewModel.isRecyclerInitialized = true
        }
    }

    override fun addOrUpdateCart(product: Product, quantity: Int,itemPosition:Int) {
        viewModel.createOrUpdateOrderDetails(product = product, quantity = quantity){
            adapter.notifyItemChanged(itemPosition)
        }
    }

    private fun refresh() {
        viewModel.insertProducts()
    }
}