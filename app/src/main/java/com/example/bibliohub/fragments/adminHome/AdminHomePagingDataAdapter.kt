package com.example.bibliohub.fragments.adminHome

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.AdminHomeRecyclerItemBinding

object ProductComparator : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}

class AdminHomePagingDataAdapter(
    private val context: Context,
    private val listener: HomeListener,
) :
    PagingDataAdapter<Product, AdminHomePagingDataAdapter.HomeViewHolder>(ProductComparator) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = AdminHomeRecyclerItemBinding.inflate(inflater, parent, false)

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
    inner class HomeViewHolder(private val binding: AdminHomeRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product?, position: Int) {
            try {
                if (product == null) {
                    return
                }

                product.imgSrc?.let {
                    Glide.with(context).load(it)
                        .into(binding.memberImageView)
                }
                //setup cart qty control
                binding.editProductButton.setOnClickListener {
                    listener.editProduct(product = product)
                }
                binding.deleteProductButton.setOnClickListener {
                    listener.deleteProduct(product = product)
                }

                binding.nameTextView.text = product.title
                binding.authorTextView.text = product.author
                binding.priceTextView.text = product.price
                binding.quantityTextView.text = product.quantity.toString()

                binding.info.setOnClickListener {
                    listener.viewProduct(product = product)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface HomeListener {
        fun editProduct(product: Product)
        fun deleteProduct(product: Product)
        fun viewProduct(product: Product)
    }
}