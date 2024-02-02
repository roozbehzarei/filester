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

class UpdateDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogUpdateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogUpdateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val downloadUrl = arguments?.getString(VER_URL_KEY) ?: ""

        binding.actionButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
            try {
                startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(
                    requireContext(), getString(R.string.toast_app_not_found), Toast.LENGTH_LONG
                ).show()
            }
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "UpdateDialog"
        const val VER_URL_KEY = "versionUrl"
    }

}