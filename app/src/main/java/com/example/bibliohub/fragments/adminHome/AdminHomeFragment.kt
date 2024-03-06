package com.example.bibliohub.fragments.adminHome

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.bibliohub.R
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.FragmentAdminHomeBinding
import com.example.bibliohub.utils.BaseSearchableFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AdminHomeFragment : BaseSearchableFragment<Product>(), AdminHomePagingDataAdapter.HomeListener {

    companion object {
        private val TAG = AdminHomeFragment::getTag
    }

    private val viewModel: AdminHomeViewModel by viewModels { AdminHomeViewModel.Factory }
    private lateinit var binding: FragmentAdminHomeBinding
    private lateinit var adapter: AdminHomePagingDataAdapter
    override var viewModelFilterText: String? = null
    override var searchCallback: ((String) -> Unit)? = null
    override var searchButton: ImageButton? = null
    override var searchText: TextInputEditText? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel

        searchButton = binding.imageButtonStopSearch
        searchText = binding.etSearch
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adminHome = activity?.findViewById<ActionMenuItemView>(R.id.admin_home_item)
        val order = activity?.findViewById<ActionMenuItemView>(R.id.order_item)
        adminHome?.setBackgroundColor(resources.getColor(R.color.darkBlue))
        order?.setBackgroundColor(resources.getColor(R.color.disabled))
    }

    override fun onPrepareOptionsMenu(menu: Menu){
        super.onPrepareOptionsMenu(menu)
        menu.getItem(R.id.admin_home_item).isVisible = true
        menu.getItem(R.id.order_item).isVisible = true
        menu.getItem(R.id.home_item).isVisible = false
        menu.getItem(R.id.cart_item).isVisible = false
    }

    override fun initCompulsoryVariables() {
        viewModelFilterText = viewModel.mFilterText
        searchCallback = { it -> viewModel.search(it) }
    }

    override fun returnBindingRoot(): View {
        return binding.root
    }

    override fun setBinding() {
        setupStatusFilter()
        binding.btn.setOnClickListener { refresh() }
    }

    override fun initRecycler() {
        viewModel.initOrderDetails {
            adapter = AdminHomePagingDataAdapter(requireContext(), this, viewModel.userOrderDetails)
            lifecycleScope.launch {
                viewModel.products.collectLatest { pagingData ->
                    // submitData suspends until loading this generation of data stops
                    // so be sure to use collectLatest {} when presenting a Flow<PagingData>
                    if (viewModel.selectedCategory?.isEmpty() == true) {
                        adapter.submitData(lifecycle, pagingData)
                    }else{
                        adapter.submitData(lifecycle, pagingData.filter { product -> product.category.equals(other = viewModel.selectedCategory, ignoreCase = true) })
                    }
                    adapter.loadStateFlow.map { it.refresh }
                        .distinctUntilChanged()
                        .collect {
                            if (it is LoadState.NotLoading) {
                                setSearchResult(adapter.itemCount)
                            }
                        }
                }
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.itemAnimator = DefaultItemAnimator()
            binding.recyclerView.adapter = adapter
            (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
        }
    }

    override fun setSearchResult(listSize: Int) {
        binding.noOfResultsTextview.visibility =
            View.VISIBLE
        val size = if (listSize >= 30) "$listSize+" else listSize.toString()
        binding.noOfResultsTextview.text = getString(R.string.y_results, size)
    }

    override fun addOrUpdateCart(product: Product, quantity: Int) {
        viewModel.createOrUpdateOrderDetails(product = product, quantity = quantity)
    }

    private fun refresh() {
        viewModel.insertProducts()
    }

    /**
     * Sets up status filter drop down, actions to be performed after item selection,
     * method to handle end icon to display, icon actions
     */
    private fun setupStatusFilter() {
        // set adapter
        binding.acvFilter.setAdapter(
            getAdapter(
                listOf(
                    getString(R.string.fiction),
                    getString(R.string.non_fiction),
                ).toTypedArray()
            )
        )

        // set text changed listener
        binding.acvFilter.doOnTextChanged { text, _, _, _ ->
            binding.shouldCancelShow = !text.isNullOrEmpty()
        }

        binding.acvFilter.setOnClickListener {
            binding.acvFilter.showDropDown()
        }

        binding.acvFilter.setOnItemClickListener { _, _, i, l ->
            viewModel.selectedCategory = binding.acvFilter.text.toString()
            initRecycler()
        }

        binding.acvFilter.doOnTextChanged { text, _, _, _ ->
            binding.shouldCancelShow = !text.isNullOrEmpty()
        }

        // set icon handlers
        binding.ivFilterDropDown.setOnClickListener {
            binding.acvFilter.showDropDown()
        }
        binding.ivFilterCancel.setOnClickListener {
            binding.acvFilter.text = null
            viewModel.selectedCategory = ""
            initRecycler()
        }
    }

    /**
     * Get Adapter object based on the entered string array
     */
    private fun getAdapter(stringArr: Array<String>): ArrayAdapter<String> {
        return ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, stringArr
        )
    }
}