package com.example.bibliohub.fragments.productDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bibliohub.databinding.FragmentProductDetailsBinding
import com.example.bibliohub.fragments.register.RegisterViewModel

class ProductDetailsFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels { ProductDetailsViewModel.Factory }
    private lateinit var binding: FragmentProductDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailsBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        return binding.root
    }
}