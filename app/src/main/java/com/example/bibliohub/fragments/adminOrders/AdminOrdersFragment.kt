package com.example.bibliohub.fragments.adminOrders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.example.bibliohub.data.entities.order.Order
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.databinding.FragmentAdminOrdersBinding
import com.example.bibliohub.utils.BaseSearchableFragment
import com.example.bibliohub.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@AndroidEntryPoint
class AdminOrdersFragment : BaseSearchableFragment<Product>(),
    AdminOrdersPagingDataAdapter.HomeListener {

    companion object {
        private val TAG = AdminOrdersFragment::getTag
    }

    private val viewModel: AdminOrdersViewModel by viewModels()
    private lateinit var binding: FragmentAdminOrdersBinding
    private lateinit var adapter: AdminOrdersPagingDataAdapter

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentAdminOrdersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel

        setOnBackPressedCallback()
    }

    override fun initCompulsoryVariables() {
    }

    override fun returnBindingRoot(): View {
        return binding.root
    }

    override fun setBinding() {
        setupStatusFilter()
        binding.btn.setOnClickListener { refresh() }
    }

    override fun initRecycler() {
        adapter = AdminOrdersPagingDataAdapter(requireContext(), this)
        lifecycleScope.launch {
            viewModel.orders.observe(viewLifecycleOwner) { pagingData ->
                // submitData suspends until loading this generation of data stops
                // so be sure to use collectLatest {} when presenting a Flow<PagingData>
                if (viewModel.selectedStatus?.isEmpty() == true) {
                    adapter.submitData(lifecycle, pagingData)
                } else {
                    adapter.submitData(
                        lifecycle,
                        pagingData.filter { order ->
                            order.status?.name.equals(
                                other = viewModel.selectedStatus,
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

    override fun setSearchResult(listSize: Int) {
        binding.noOfResultsTextview.visibility =
            View.VISIBLE
        val size = if (listSize >= 30) "$listSize+" else listSize.toString()
        binding.noOfResultsTextview.text = getString(R.string.y_results, size)
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
                    Constants.Status.COMPLETED.name,
                    Constants.Status.APPROVED.name,
                    Constants.Status.REJECTED.name,
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
            viewModel.selectedStatus = binding.acvFilter.text.toString()
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
            viewModel.selectedStatus = ""
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

    override suspend fun getUser(userId: Int): User? =
        viewModel.getUser(userId = userId)

    override suspend fun getAllOrderDetails(orderId: Int): List<OrderDetails>? =
        viewModel.getAllOrderDetails(orderId = orderId)

    override suspend fun updateOrder(order: Order, status: Constants.Status) {
        viewModel.updateOrder(order = order, status = status)
    }

    override fun viewOrderDetails(order: Order) {
        findNavController().navigate(
            AdminOrdersFragmentDirections.actionAdminOrdersFragmentToAdminOrderDetailsFragment(
                order = order
            )
        )
    }
}