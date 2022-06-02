package com.rouzbehzarei.filester.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rouzbehzarei.filester.BaseApplication
import com.rouzbehzarei.filester.R
import com.rouzbehzarei.filester.databinding.FragmentHistoryBinding
import com.rouzbehzarei.filester.viewmodel.FilesterViewModel
import com.rouzbehzarei.filester.viewmodel.FilesterViewModelFactory

class HistoryFragment : Fragment() {

    // Binding object instance with access to the views in the fragment_upload.xml layout
    private lateinit var binding: FragmentHistoryBinding

    private val viewModel: FilesterViewModel by activityViewModels {
        FilesterViewModelFactory(
            (activity?.application as BaseApplication).database.fileDao(),
            requireActivity().application
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

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

}