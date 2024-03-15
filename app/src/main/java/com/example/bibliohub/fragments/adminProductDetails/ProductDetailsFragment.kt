package com.example.bibliohub.fragments.adminProductDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.bibliohub.R
import com.example.bibliohub.databinding.FragmentAdminProductDetailsBinding
import com.example.bibliohub.databinding.FragmentProductDetailsBinding
import com.example.bibliohub.utils.Constants
import com.example.bibliohub.utils.HelperFunctions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AdminProductDetailsFragment : Fragment() {

    private val viewModel: AdminProductDetailsViewModel by viewModels { AdminProductDetailsViewModel.Factory }
    private lateinit var binding: FragmentAdminProductDetailsBinding
    private val args: AdminProductDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminProductDetailsBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.product = args.product

        val sdf = SimpleDateFormat(Constants.DATE_FORMAT_HYPHEN_DMY, Locale.getDefault())
        val date = args.product.pubDate?.let { sdf.parse(it) }
        viewModel.product.pubDate = date?.let { HelperFunctions.getDateString(Constants.DATE_FORMAT_FULL, it) }

        viewModel.product.let { product ->
            product.imgSrc?.let {
                loadImage(it)
            }
        }

        return binding.root
    }

    private fun loadImage(imgSrc: String) =
        Glide.with(requireContext()).load(imgSrc)
            .into(binding.productImageView)
}