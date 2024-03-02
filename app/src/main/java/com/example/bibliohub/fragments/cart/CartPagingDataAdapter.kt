package com.example.bibliohub.fragments.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.CartRecyclerItemBinding

object ProductComparator : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}

class CartPagingDataAdapter(
    private val context: Context,
    private val listener: CartListener,
    private val itemsInCart: List<OrderDetails>,
) :
    PagingDataAdapter<Product, CartPagingDataAdapter.CartViewHolder>(ProductComparator) {
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
        fun bind(product: Product?, position: Int) {
            fun updateOrderQtyView(orderQty: Int) {
                binding.quantityEditText.setText(orderQty.toString())
                if (product != null) {
                    listener.addOrUpdateCart(
                        product,
                        orderQty
                    )
                }
            }
            try {
                if (product == null) {
                    return
                }
                Glide.with(context).load(product.imgSrc)
                    .into(binding.memberImageView)

                //to be used to track order info
                val userItemInCart = itemsInCart.firstOrNull { it.productId == product.id }
                //get existing order quantity else assign to 0
                val orderQuantity = userItemInCart?.quantity ?: 0

                //setup cart qty control
                binding.addBtn.setOnClickListener {
                    val currentDisplayedQty =
                        binding.quantityEditText.text.toString().toIntOrNull() ?: 0
                    if (currentDisplayedQty < product.quantity) {
                        updateOrderQtyView(currentDisplayedQty + 1)
                    }
                }
                binding.subtractBtn.setOnClickListener {
                    // to make sure order qty doesn't go below 0
                    val currentDisplayedQty =
                        binding.quantityEditText.text.toString().toIntOrNull() ?: 0
                    if (currentDisplayedQty == 0) {
                        return@setOnClickListener
                    }
                    updateOrderQtyView(currentDisplayedQty - 1)
                }
                binding.nameTextView.text = product.name
                binding.authorTextView.text = product.author
                binding.priceTextView.text = product.price
                //make sure order quantity view has item to display
                updateOrderQtyView(orderQuantity)


                binding.deleteFromCartButton.setOnClickListener {
                    listener.deleteFromCart(product.id)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface CartListener {
        fun addOrUpdateCart(product: Product, quantity: Int)
        fun deleteFromCart(productId: Int)
    }
}