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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.rouzbehzarei.filester.BaseApplication
import com.rouzbehzarei.filester.R
import com.rouzbehzarei.filester.databinding.FragmentMainBinding
import com.rouzbehzarei.filester.viewmodel.FilesterViewModel
import com.rouzbehzarei.filester.viewmodel.FilesterViewModelFactory
import com.rouzbehzarei.filester.viewmodel.KEY_FILE_URI

class MainFragment : Fragment() {

    // Binding object instance with access to the views in the fragment_upload.xml layout
    private lateinit var binding: FragmentMainBinding

    private val viewModel: FilesterViewModel by activityViewModels {
        FilesterViewModelFactory(
            (activity?.application as BaseApplication).database.fileDao(),
            requireActivity().application
        )
    }

    private val fileSelector =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                viewModel.startUploadWork(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val adapter = HistoryListAdapter { file ->
            val clipboard =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("file url", file.fileUrl)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                context,
                getString(R.string.toast_clipboard),
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.fileListView.adapter = adapter
        viewModel.files.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.files.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.textNoUploads.visibility = View.VISIBLE
            } else {
                binding.textNoUploads.visibility = View.GONE
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            fileSelector.launch("*/*")
        }
        viewModel.outputWorkInfo.observe(viewLifecycleOwner, workInfoObserver())
    }

    private fun workInfoObserver(): Observer<List<WorkInfo>> {
        return Observer {
            // If there are no matching work info, do nothing
            if (it.isNullOrEmpty()) {
                return@Observer
            }
            val workInfo = it[0]
            val fileUrl = workInfo.outputData.getString(KEY_FILE_URI)
            if (!workInfo.state.isFinished) {
                isFabEnabled(false)
                Snackbar.make(
                    binding.root,
                    resources.getString(R.string.snackbar_uploading),
                    Snackbar.LENGTH_LONG
                )
                    .show()
            } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                showUploadFinished(true, fileUrl)
                viewModel.clearWorkQueue()
                isFabEnabled(true)

            } else {
                showUploadFinished(false, null)
                viewModel.clearWorkQueue()
                isFabEnabled(true)
            }
        }
    }

    private fun showUploadFinished(isUploadSuccessful: Boolean, fileUrl: String?) {
        val context = requireContext()
        val alertDialog = MaterialAlertDialogBuilder(context)
        if (isUploadSuccessful) {
            alertDialog.setTitle(resources.getString(R.string.dialog_title_success))
                .setMessage(fileUrl)
                .setPositiveButton(resources.getString(R.string.dialog_button_copy)) { _, _ ->
                    val clipboard =
                        activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("file url", fileUrl)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(
                        context,
                        getString(R.string.toast_clipboard),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNeutralButton(resources.getString(R.string.dialog_button_close)) { dialog, _ ->
                    dialog.dismiss()
                }
        } else {
            alertDialog.setTitle(resources.getString(R.string.dialog_title_error))
                .setMessage(resources.getString(R.string.dialog_message_error))
                .setNeutralButton(resources.getString(R.string.dialog_button_close)) { dialog, _ ->
                    dialog.dismiss()
                }
        }
            .setCancelable(false)
            .show()
    }

    private fun isFabEnabled(state: Boolean) {
        binding.fab.apply {
            if (state) {
                isEnabled = true
                setBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_light_primary))
            } else {
                isEnabled = false
                setBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_light_outline))
            }
        }
    }

}