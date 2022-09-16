package com.roozbehzarei.filester.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.roozbehzarei.filester.BaseApplication
import com.roozbehzarei.filester.HistoryListAdapter
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.databinding.FragmentMainBinding
import com.roozbehzarei.filester.viewmodel.FilesterViewModel
import com.roozbehzarei.filester.viewmodel.FilesterViewModelFactory
import com.roozbehzarei.filester.viewmodel.KEY_FILE_URI

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
                val fileName = DocumentFile.fromSingleUri(requireContext(), uri)?.name
                viewModel.startUploadWork(uri, fileName ?: "null")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = FragmentMainBinding.inflate(inflater, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.app_bar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_about -> findNavController().navigate(MainFragmentDirections.actionMainFragmentToAboutFragment())
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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
                binding.fileListView.visibility = View.GONE
            } else {
                binding.textNoUploads.visibility = View.GONE
                binding.fileListView.visibility = View.VISIBLE
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
                isUploadInProgress(true)
                Snackbar.make(
                    binding.root,
                    resources.getString(R.string.snackbar_uploading),
                    Snackbar.LENGTH_LONG
                )
                    .show()
            } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                showDialog(true, fileUrl)
                viewModel.clearWorkQueue()
                isUploadInProgress(false)

            } else {
                showDialog(false, null)
                viewModel.clearWorkQueue()
                isUploadInProgress(false)
            }
        }
    }

    private fun showDialog(isUploadSuccessful: Boolean, fileUrl: String?) {
        val context = requireContext()
        val alertDialog = MaterialAlertDialogBuilder(context)
        if (isUploadSuccessful) {
            alertDialog.setTitle(resources.getString(R.string.title_upload_success))
                .setMessage(getString(R.string.message_upload_success))
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
                .setNegativeButton(resources.getString(R.string.dialog_button_close)) { dialog, _ ->
                    dialog.dismiss()
                }
        } else {
            alertDialog.setTitle(resources.getString(R.string.title_upload_error))
                .setMessage(getString(R.string.message_upload_error))
                .setPositiveButton(resources.getString(R.string.dialog_button_close)) { dialog, _ ->
                    dialog.dismiss()
                }
        }
            .setCancelable(false)
            .show()
    }

    private fun isUploadInProgress(state: Boolean) {
        if (state) {
            binding.fab.hide()
            binding.progressIndicator.visibility = View.VISIBLE
        } else {
            binding.fab.show()
            binding.progressIndicator.visibility = View.GONE
        }
    }

}