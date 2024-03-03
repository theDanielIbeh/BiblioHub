package com.example.bibliohub.fragments.productDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bibliohub.BiblioHubApplication

class ProductDetailsViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BiblioHubApplication)
                ProductDetailsViewModel(
                )
            }
        }
    }
}