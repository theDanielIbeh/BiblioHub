package com.example.bibliohub.fragments.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bibliohub.R
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.HomeRecyclerItemBinding

object JokeComparator : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}

class HomePagingDataAdapter(
    private val context: Context,
    private val listener: HomeListener,
    var cart: List<OrderDetails>?,
) :
    PagingDataAdapter<Product, HomePagingDataAdapter.HomeViewHolder>(JokeComparator) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = HomeRecyclerItemBinding.inflate(inflater, parent, false)

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
    inner class HomeViewHolder(private val binding: HomeRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product?, position: Int) {
            try {
                if (product == null) {
                    return
                }
                Log.d("Cart", cart.toString())
                val isInCart = cart?.any { orderDetails -> orderDetails.productId == product.id }
                if (isInCart == true) {
                    binding.addToCartButton.text =
                        getString(context, R.string.update_cart)
                }

                binding.nameTextView.text = product.name
                binding.authorTextView.text = product.author
                binding.priceTextView.text = product.price

                binding.addToCartButton.setOnClickListener {
                    listener.addToCart(
                        product,
                        binding.priceTextView.text.toString()
                    )
                    notifyItemChanged(position)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface HomeListener {
        fun addToCart(product: Product, quantity: String)
    }
}