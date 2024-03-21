package com.example.bibliohub.fragments.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.bibliohub.R
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.FragmentHomeBinding
import com.example.bibliohub.utils.BaseSearchableFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


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

        setOnBackPressedCallback()
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
    }

    override fun initRecycler() {
        initOrderDetails {
            adapter = HomePagingDataAdapter(requireContext(), this, viewModel.userOrderDetails)
            lifecycleScope.launch {
                viewModel.products.observe(viewLifecycleOwner) { pagingData ->
                    // submitData suspends until loading this generation of data stops
                    // so be sure to use collectLatest {} when presenting a Flow<PagingData>
                    if (viewModel.selectedCategory?.isEmpty() == true) {
                        adapter.submitData(lifecycle, pagingData)
                    } else {
                        adapter.submitData(
                            lifecycle,
                            pagingData.filter { product ->
                                product.category.equals(
                                    other = viewModel.selectedCategory,
                                    ignoreCase = true
                                )
                            })
                    }
                    lifecycleScope.launch {
                        adapter.loadStateFlow.map { it.refresh }
                            .distinctUntilChanged()
                            .collect {
                                if (it is LoadState.NotLoading) {
                                    setSearchResult(adapter.itemCount)
                                }
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

    private fun initOrderDetails(onOrderDetailsInitialized: () -> Unit) {
        //check if user has existing order details
        viewModel.loggedInUser.observe(viewLifecycleOwner) { userInfo ->
            Log.d("User", userInfo.toString())
            if (userInfo != null) {
                lifecycleScope.launch {
                    //get current order info else create new order
                    viewModel.currentOrder =
                        viewModel.orderRepository.getStaticActiveOrderByUserId(userInfo.id)
                            ?: viewModel.createNewOrder(
                                userInfo.id
                            )
                    Log.d("Current Order", viewModel.currentOrder.toString())
                    //check if user already has an order saved and assign to order details list
                    viewModel.userOrderDetailsLive =
                        viewModel.orderDetailsRepository.getOrderDetailsByOrderId(
                            viewModel.currentOrder?.id ?: -1
                        )
                    viewModel.userOrderDetailsLive.observe(viewLifecycleOwner) {
                        Log.d("Order", it.toString())
                        viewModel.updateOrderDetailsList(it) {
                            //run callback after order details initialized with check if recycler has
                            // already ben initialized
                            onOrderDetailsInitialized()
                        }
                    }
                }
            }
        }
    }

    override fun setSearchResult(listSize: Int) {
        binding.noOfResultsTextview.visibility =
            View.VISIBLE
        val size = if (listSize >= 30) "$listSize+" else listSize.toString()
        binding.noOfResultsTextview.text = getString(R.string.y_results, size)
    }

    override fun addOrUpdateCart(product: Product, quantity: Int, itemPosition: Int) {
        viewModel.createOrUpdateOrderDetails(product = product, quantity = quantity) {
            adapter.notifyItemChanged(itemPosition)
        }
    }

    override fun viewProduct(product: Product) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(
                product = product
            )
        )
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

    private fun setOnBackPressedCallback() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                    exitProcess(0)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}