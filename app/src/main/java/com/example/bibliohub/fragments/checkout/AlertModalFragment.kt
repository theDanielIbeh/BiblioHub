package com.example.bibliohub.fragments.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.bibliohub.R
import com.example.bibliohub.databinding.FragmentAlertModalBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AlertModalFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAlertModalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentAlertModalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.isCancelable = false

        binding.okButton.setOnClickListener {
            dismiss()
            navigateToHome()
        }
    }

    /**
     * Navigates to the Home screen.
     */
    private fun navigateToHome() {
        findNavController().popBackStack(R.id.homeFragment, true)
    }
}