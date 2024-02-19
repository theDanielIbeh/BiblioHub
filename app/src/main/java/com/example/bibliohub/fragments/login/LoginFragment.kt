package com.example.bibliohub.fragments.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.bibliohub.R
import com.example.bibliohub.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var viewModelFactory: LoginViewModel.LoginViewModelFactory
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelFactory = activity?.let { LoginViewModel.LoginViewModelFactory(application = it.application) }!!
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        // TODO: Use the ViewModel
    }

}