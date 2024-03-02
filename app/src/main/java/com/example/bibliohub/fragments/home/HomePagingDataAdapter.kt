package com.example.bibliohub.fragments.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.core.widget.doAfterTextChanged
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bibliohub.R
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.HomeRecyclerItemBinding

object ProductComparator : DiffUtil.ItemCallback<Product>() {
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
    private val itemsInCart: List<OrderDetails>,
) :
    PagingDataAdapter<Product, HomePagingDataAdapter.HomeViewHolder>(ProductComparator) {
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
            fun updateOrderQtyView(orderQty: Int) {
                binding.quantityEditText.setText(orderQty.toString())
            }

            fun isMainButtonEnabled(isEnabled: Boolean) {
                binding.addToCartButton.isEnabled = isEnabled
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
                var orderQuantity = userItemInCart?.quantity ?: 0
                val isInCart = userItemInCart != null
                val cartButtonText =
                    if (isInCart) {
                        getString(context, R.string.update_cart)
                    } else {
                        getString(context, R.string.add_to_cart)
                    }
                val cartButtonColor =
                    if (isInCart) {
                        context.getColor(R.color.darkBlue)
                    } else {
                        context.getColor(R.color.lightGreen)
                    }
                //disable main button so user doesn't add to cart with qty of 0
                isMainButtonEnabled(false)

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
                binding.quantityEditText.doAfterTextChanged {
                    //check quantity change and activate button
                    val enteredQuantity = it.toString().toIntOrNull() ?: 0
                    //check if what user entered is equal to size of item in cart
                    if (enteredQuantity == (userItemInCart?.quantity ?: 0)) {
                        isMainButtonEnabled(false)
                        return@doAfterTextChanged
                    }
                    isMainButtonEnabled(
                        //if item is in cart activate button when quantity changes
                        if (isInCart) {
                            true
                        } else {
                            //if item not in cart ensure quantity is more than 0
                            //before user can add to cart
                            enteredQuantity > 0
                        }
                    )
                    //update order quantity
                    orderQuantity = enteredQuantity
                }
                binding.addToCartButton.text = cartButtonText
                binding.addToCartButton.setBackgroundColor(cartButtonColor)
                binding.nameTextView.text = product.name
                binding.authorTextView.text = product.author
                binding.priceTextView.text = product.price
                //make sure order quantity view has item to display
                updateOrderQtyView(orderQuantity)


                binding.addToCartButton.setOnClickListener {
                    listener.addOrUpdateCart(
                        product,
                        orderQuantity
                    )
                    notifyItemChanged(position)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface HomeListener {
        fun addOrUpdateCart(product: Product, quantity: Int)
    }
}