package com.example.bibliohub.fragments.adminOrderDetails

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.AdminOrderDetailsRecyclerItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object OrderDetailsComparator : DiffUtil.ItemCallback<OrderDetails>() {
    override fun areItemsTheSame(oldItem: OrderDetails, newItem: OrderDetails): Boolean {
        // Id is unique.
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(oldItem: OrderDetails, newItem: OrderDetails): Boolean {
        return oldItem == newItem
    }
}

class AdminOrderDetailsPagingDataAdapter(
    private val context: Context,
    private val listener: HomeListener,
) :
    PagingDataAdapter<OrderDetails, AdminOrderDetailsPagingDataAdapter.HomeViewHolder>(
        OrderDetailsComparator
    ) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = AdminOrderDetailsRecyclerItemBinding.inflate(inflater, parent, false)

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
    inner class HomeViewHolder(private val binding: AdminOrderDetailsRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderDetails: OrderDetails?, position: Int) {
            try {
                if (orderDetails == null) {
                    return
                }

                CoroutineScope(Dispatchers.Main).launch {
                    val product =
                        withContext(Dispatchers.IO) { listener.getProduct(productId = orderDetails.productId) }

                    with(binding) {
                        nameTextView.text = product?.title
                        authorTextView.text = product?.author
                    }
                }

                with(binding) {
                    priceTextView.text = orderDetails.price
                    quantityTextView.text = orderDetails.quantity.toString()
                    totalPriceTextView.text = ((orderDetails.price.toDoubleOrNull()?:0.0) * orderDetails.quantity).toString()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface HomeListener {
        suspend fun getProduct(productId: Int): Product?
    }
}