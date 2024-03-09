package com.example.bibliohub.fragments.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.FragmentCartBinding
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
        adapter = CartPagingDataAdapter(requireContext(), this)

        initOrderDetails {
            adapter.submitData(lifecycle, it)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }

    private fun initOrderDetails(onOrderDetailsInitialized: (orderDetails: PagingData<OrderDetails>) -> Unit) {
        //check if user has existing order details
        viewModel.loggedInUser.observe(viewLifecycleOwner) { userInfo ->
            if (userInfo != null) {
                Log.d("User", userInfo.toString())
                lifecycleScope.launch {
                    //get current order info else create new order
                    viewModel.currentOrder =
                        viewModel.orderRepository.getStaticActiveOrderByUserId(userInfo.id)
                            ?: viewModel.createNewOrder(
                                userInfo.id
                            )

                    viewModel.currentOrder?.id?.let {
                        viewModel.orderDetailsRepository.getOrderDetailsByOrderId(it)
                            .observe(viewLifecycleOwner) {
                                if (viewModel.orderDetails != it) {
                                    viewModel.orderDetails = it
                                    viewModel.orderSum = it.sumOf { orderDetails ->
                                        ((orderDetails.price.toDoubleOrNull()
                                            ?: 0.0) * orderDetails.quantity)
                                    }
                                    Log.d("OrderSum", viewModel.orderSum.toString())
                                }
                            }
                    }
                    //check if user already has an order saved and assign to order details list
                    viewModel.currentOrder?.id?.let {
                        viewModel.getOrderDetailsByIds(it)
                            .observe(viewLifecycleOwner) {
                                onOrderDetailsInitialized(it)
                            }
                    }
                }
            }
        }
    }


    override suspend fun getProduct(productId: Int): Product? =
        viewModel.getProduct(productId = productId)

    override fun addOrUpdateCart(orderDetails: OrderDetails, quantity: Int) {
        viewModel.createOrUpdateOrderDetails(orderDetails = orderDetails, quantity = quantity)
    }

    override fun deleteFromCart(productId: Int) {
        viewModel.deleteFromCart(productId)
    }
}