package com.example.bibliohub.fragments.cart

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.CartRecyclerItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ProductComparator : DiffUtil.ItemCallback<OrderDetails>() {
    override fun areItemsTheSame(oldItem: OrderDetails, newItem: OrderDetails): Boolean {
        // Id is unique.
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(oldItem: OrderDetails, newItem: OrderDetails): Boolean {
        return oldItem == newItem
    }
}

class CartPagingDataAdapter(
    private val context: Context,
    private val listener: CartListener,
) :
    PagingDataAdapter<OrderDetails, CartPagingDataAdapter.CartViewHolder>(ProductComparator) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = CartRecyclerItemBinding.inflate(inflater, parent, false)

        return CartViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = getItem(position)
        // Note that item may be null. ViewHolder must support binding a
        // null item as a placeholder.
        holder.bind(item, position)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a Product object.
    inner class CartViewHolder(private val binding: CartRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderDetails: OrderDetails?, position: Int) {
            fun updateOrderQtyView(orderQty: Int) {
                binding.quantityEditText.setText(orderQty.toString())
                if (orderDetails != null) {
                    listener.addOrUpdateCart(
                        orderDetails,
                        orderQty
                    )
                }
            }
            try {
                if (orderDetails == null) {
                    return
                }

                CoroutineScope(Dispatchers.Main).launch {
                    val product = listener.getProduct(orderDetails.productId)
                    product?.imgSrc?.let {
                        Glide.with(context).load(it)
                            .into(binding.memberImageView)
                    }
                    binding.nameTextView.text = product?.title
                    binding.authorTextView.text = product?.author
                    binding.priceTextView.text = product?.price

                    binding.addBtn.setOnClickListener {
                        val currentDisplayedQty =
                            binding.quantityEditText.text.toString().toIntOrNull() ?: 0
                        Log.d("CurrentQty", currentDisplayedQty.toString())
                        if (currentDisplayedQty < (product?.quantity?:0)) {
                            updateOrderQtyView(currentDisplayedQty + 1)
                        }
                    }
                }

                //setup cart qty control
                binding.subtractBtn.setOnClickListener {
                    // to make sure order qty doesn't go below 0
                    val currentDisplayedQty =
                        binding.quantityEditText.text.toString().toIntOrNull() ?: 0
                    if (currentDisplayedQty == 0) {
                        return@setOnClickListener
                    }
                    updateOrderQtyView(currentDisplayedQty - 1)
                }
                //make sure order quantity view has item to display
                updateOrderQtyView(orderDetails.quantity)

                binding.deleteFromCartButton.setOnClickListener {
                    orderDetails.productId.let { it1 -> listener.deleteFromCart(it1) }
                    notifyItemChanged(position)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface CartListener {
        suspend fun getProduct(productId: Int): Product?
        fun addOrUpdateCart(orderDetails: OrderDetails, quantity: Int)
        fun deleteFromCart(productId: Int)
    }
}