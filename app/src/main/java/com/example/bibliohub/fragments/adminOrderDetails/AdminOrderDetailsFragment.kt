package com.example.bibliohub.fragments.adminOrderDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.bibliohub.R
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.FragmentAdminOrderDetailsBinding
import com.example.bibliohub.utils.Constants
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

class AdminOrderDetailsFragment : Fragment(), AdminOrderDetailsPagingDataAdapter.HomeListener {

    private val viewModel: AdminOrderDetailsViewModel by viewModels { AdminOrderDetailsViewModel.Factory }
    private lateinit var binding: FragmentAdminOrderDetailsBinding
    private lateinit var adapter: AdminOrderDetailsPagingDataAdapter
    private val args: AdminOrderDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminOrderDetailsBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.order = args.order
        viewModel.order.status?.name?.let { viewModel.status = it }
        lifecycleScope.launch {
            viewModel.orderDetails = viewModel.getOrderDetailsByOrderId()
            viewModel.getUserById()?.let {
                viewModel.user = it
            }
            viewModel.name = "${viewModel.user.firstName} ${viewModel.user.lastName}"

            viewModel.total =
                viewModel.orderDetailsRepository.getStaticOrderDetailsByOrderId(viewModel.order.id)
                    .sumOf { orderDetails ->
                        ((orderDetails.price.toDoubleOrNull()
                            ?: 0.0) * orderDetails.quantity)
                    }.toString()
            Log.d("Total", viewModel.total.toString())
        }

        initRecycler()

        setOnBackPressedCallback()
        return binding.root
    }

    private fun initRecycler() {
        adapter = AdminOrderDetailsPagingDataAdapter(requireContext(), this)
        viewModel.orderDetails.observe(viewLifecycleOwner) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }

    override suspend fun getProduct(productId: Int): Product? =
        viewModel.getProductById(productId = productId)

    private fun setOnBackPressedCallback() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.adminOrdersFragment, false)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}