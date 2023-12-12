package com.roozbehzarei.filester.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    // Binding object instance with access to the views in the fragment_upload.xml layout
    private lateinit var binding: FragmentMainBinding

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
                fileSelector.launch("*/*")
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
                    R.id.action_settings -> findNavController().navigate(MainFragmentDirections.actionMainFragmentToSettingsFragment())
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val adapter = HistoryListAdapter { file, action ->
            when (action) {
                0 -> {
                    val sendIntent: Intent = Intent().apply {
                        setAction(Intent.ACTION_SEND)
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "${file.fileUrl}\n${getString(R.string.message_shared_with_filester)}"
                        )
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }

                1 -> {
                    val clipboard =
                        activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("file url", file.fileUrl)
                    clipboard.setPrimaryClip(clip)
                    Snackbar.make(
                        binding.snackbarLayout,
                        getString(R.string.snackbar_clipboard),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                2 -> {
                    showDeleteDialog(file.fileName) {
                        viewModel.deleteFile(file)
                    }

                }
            }

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkNotificationPermission()
            } else {
                fileSelector.launch("*/*")
            }
        }
        viewModel.outputWorkInfo.observe(viewLifecycleOwner, workInfoObserver())

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isFileDeleted) {
                        Snackbar.make(
                            binding.snackbarLayout,
                            getString(R.string.snackbar_delete_successful),
                            Snackbar.LENGTH_LONG
                        ).show()
                        viewModel.uiStateConsumed()
                    }
                }
            }
        }

    }

    private fun workInfoObserver(): Observer<List<WorkInfo>> {
        return Observer {
            // If there are no matching work info, do nothing
            if (it.isEmpty()) {
                return@Observer
            }
            val workInfo = it[0]
            val fileUrl = workInfo.outputData.getString(KEY_FILE_URI)
            if (!workInfo.state.isFinished) {
                isUploadInProgress(true)
                Snackbar.make(
                    binding.snackbarLayout,
                    resources.getString(R.string.snackbar_uploading),
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                showUploadDialog(true, fileUrl)
                viewModel.clearWorkQueue()
                isUploadInProgress(false)

            } else {
                showUploadDialog(false, null)
                viewModel.clearWorkQueue()
                isUploadInProgress(false)
            }
        }
    }

    private fun showUploadDialog(isUploadSuccessful: Boolean, fileUrl: String?) {
        val context = requireContext()
        val alertDialog = MaterialAlertDialogBuilder(context)
        if (isUploadSuccessful) {
            alertDialog.setTitle(resources.getString(R.string.title_upload_success))
                .setMessage(getString(R.string.message_upload_success))
                .setPositiveButton(getString(R.string.share)) { _, _ ->
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "$fileUrl\n${getString(R.string.message_shared_with_filester)}"
                        )
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }.setNegativeButton(getString(R.string.dialog_button_copy)) { _, _ ->
                    val clipboard =
                        activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("file url", fileUrl)
                    clipboard.setPrimaryClip(clip)
                    Snackbar.make(
                        binding.snackbarLayout,
                        getString(R.string.snackbar_clipboard),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }.setNeutralButton(getString(R.string.dialog_button_close)) { dialog, _ ->
                    dialog.dismiss()
                }
        } else {
            alertDialog.setTitle(resources.getString(R.string.title_upload_error))
                .setMessage(getString(R.string.message_upload_error))
                .setPositiveButton(resources.getString(R.string.dialog_button_close)) { dialog, _ ->
                    dialog.dismiss()
                }
        }.setCancelable(false).show()
    }

    private fun showDeleteDialog(fileName: String, onPositiveButtonClicked: () -> Unit) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
        with(dialog) {
            setTitle(getString(R.string.dialog_delete_title))
            setMessage(getString(R.string.dialog_delete_message, fileName))
            setPositiveButton(getString(R.string.button_delete)) { _, _ ->
                onPositiveButtonClicked()
            }
            setNegativeButton(getString(R.string.button_cancel)) { dialog, _ ->
                dialog.dismiss()
            }.show()
        }
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // Explain to the user why the app needs this permission
                // To be implemented in future releases
            }

            else -> fileSelector.launch("*/*")
        }
    }
}