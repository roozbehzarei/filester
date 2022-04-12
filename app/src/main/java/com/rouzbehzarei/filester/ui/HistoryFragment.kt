package com.rouzbehzarei.filester.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rouzbehzarei.filester.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    // Binding object instance with access to the views in the fragment_upload.xml layout
    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

}