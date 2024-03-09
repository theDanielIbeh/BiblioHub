package com.example.bibliohub.fragments.adminOrderDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.FragmentAdminOrderDetailsBinding
import com.example.bibliohub.utils.Constants
import kotlinx.coroutines.launch

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
        viewModel.order = args.order

        initRecycler()

        with(binding) {
            hasStatusChanged =
                (viewModel?.order?.status != Constants.Status.COMPLETED)

            rejectProductButton.setOnClickListener {
                lifecycleScope.launch {
                    viewModel?.order?.id?.let { it1 -> viewModel?.changeOrderStatus(it1, Constants.Status.REJECTED) }
                }
            }
            approveProductButton.setOnClickListener {
                lifecycleScope.launch {
                    viewModel?.order?.id?.let { it1 -> viewModel?.changeOrderStatus(it1, Constants.Status.APPROVED) }
                }
            }
        }

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
}