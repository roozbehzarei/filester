package com.roozbehzarei.filester.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.databinding.DialogUpdateBinding

private const val UPDATE_URL = "https://roozbehzarei.me/filester/download"

class UpdateDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogUpdateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogUpdateBinding.inflate(layoutInflater)

        binding.actionButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UPDATE_URL))
            try {
                startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(
                    requireContext(), getString(R.string.toast_app_not_found), Toast.LENGTH_LONG
                ).show()
            }
            dismiss()
        }

        return binding.root
    }

    companion object {
        const val TAG = "UpdateDialog"
    }

}