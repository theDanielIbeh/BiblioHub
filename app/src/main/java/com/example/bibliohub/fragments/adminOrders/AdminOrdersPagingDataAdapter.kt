package com.example.bibliohub.fragments.adminOrders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bibliohub.data.entities.order.Order
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.databinding.AdminOrdersRecyclerItemBinding
import com.example.bibliohub.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ProductComparator : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}

class AdminOrdersPagingDataAdapter(
    private val context: Context,
    private val listener: HomeListener,
) :
    PagingDataAdapter<Order, AdminOrdersPagingDataAdapter.HomeViewHolder>(ProductComparator) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = AdminOrdersRecyclerItemBinding.inflate(inflater, parent, false)

        return HomeViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = getItem(position)
        // Note that item may be null. ViewHolder must support binding a
        // null item as a placeholder.
        holder.bind(item, position)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a Product object.
    inner class HomeViewHolder(private val binding: AdminOrdersRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order?, position: Int) {
            try {
                if (order == null) {
                    return
                }

                binding.hasStatusChanged =
                    (order.status != Constants.Status.COMPLETED)

                CoroutineScope(Dispatchers.Main).launch {
                    val user = withContext(Dispatchers.IO) { listener.getUser(order.customerId) }

                    val orderDetails =
                        withContext(Dispatchers.IO) { listener.getAllOrderDetails(orderId = order.id) }
                    val totalPrice = orderDetails?.sumOf { element ->
                        (element.quantity.times(
                            element.price.toDoubleOrNull() ?: 0.0
                        ))
                    }

                    with(binding) {
                        nameTextView.text = "${user?.firstName} ${user?.lastName}"
                        priceTextView.text = "$totalPrice"
                    }
                }

                with(binding) {
                    dateTextView.text = order.date
                    statusTextView.text = order.status?.name

                    info.setOnClickListener {
                        listener.viewOrderDetails(order = order)
                    }
                    approveProductButton.setOnClickListener {
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO) {
                                listener.updateOrder(
                                    order = order,
                                    status = Constants.Status.APPROVED
                                )
                            }
                        }
                    }
                    rejectProductButton.setOnClickListener {
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO) {
                                listener.updateOrder(
                                    order = order,
                                    status = Constants.Status.REJECTED
                                )
                            }
                        }
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface HomeListener {
        suspend fun getUser(userId: Int): User?
        suspend fun getAllOrderDetails(orderId: Int): List<OrderDetails>?
        suspend fun updateOrder(order: Order, status: Constants.Status)
        fun viewOrderDetails(order: Order)
    }
}