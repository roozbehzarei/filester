package com.rouzbehzarei.filester.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rouzbehzarei.filester.R
import com.rouzbehzarei.filester.databinding.FragmentUploadBinding
import com.rouzbehzarei.filester.model.TransferApiStatus
import com.rouzbehzarei.filester.model.UploadViewModel

class UploadFragment : Fragment() {

    private val viewModel: UploadViewModel by viewModels()

    // Binding object instance with access to the views in the fragment_history.xml layout
    private lateinit var binding: FragmentUploadBinding

    private val result =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                viewModel.prepareForUpload(uri, requireContext().contentResolver)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout XML file and return a binding object instance
        binding = FragmentUploadBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assign the component to a property in the binding class
        binding.viewModel = viewModel

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.buttonUpload.setOnClickListener {
            result.launch("*/*")
        }

        viewModel.responseStatus.observe(viewLifecycleOwner) {
            val resultDialog = MaterialAlertDialogBuilder(requireContext())
            when (viewModel.responseStatus.value) {
                TransferApiStatus.DONE -> {
                    resultDialog
                        .setTitle(getString(R.string.dialog_success_title))
                        .setMessage(
                            viewModel.apiResponse.value?.body()
                        )
                        .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(getString(R.string.dialog_button_copy)) { _, _ ->
                            val clipboard =
                                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip: ClipData =
                                ClipData.newPlainText("link", viewModel.apiResponse.value?.body())
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.toast_clipboard),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setCancelable(false)
                        .show()
                    viewModel.resetStatus()
                }
                TransferApiStatus.ERROR -> {
                    resultDialog
                        .setTitle(getString(R.string.dialog_error_title))
                        .setMessage(
                            R.string.dialog_error_message,
                        )
                        .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .show()
                    viewModel.resetStatus()
                }
                TransferApiStatus.LOADING -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_loading),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

}