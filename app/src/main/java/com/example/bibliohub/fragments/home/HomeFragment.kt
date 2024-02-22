package com.example.bibliohub.fragments.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.bibliohub.R
import com.example.bibliohub.databinding.FragmentHomeBinding
import com.example.bibliohub.databinding.FragmentLoginBinding
import com.example.bibliohub.fragments.login.LoginFragment
import com.example.bibliohub.fragments.login.LoginViewModel

class HomeFragment : Fragment() {

    companion object {
        private val TAG = HomeFragment::getTag
    }

    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory }
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }
}