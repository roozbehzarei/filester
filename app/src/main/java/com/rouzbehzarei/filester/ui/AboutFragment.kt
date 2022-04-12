package com.rouzbehzarei.filester.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rouzbehzarei.filester.databinding.FragmentAboutBinding

private const val GITHUB_URL = "https://github.com/rouzbehzarei"
private const val DONATE_URL = "https://www.buymeacoffee.com/rouzbehzarei/"
private const val TRANSFER_URL = "https://transfer.sh/"

class AboutFragment : Fragment() {

    // Binding object instance with access to the views in the fragment_about.xml layout
    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Open links when clicked on
         */
        binding.linkDonation.setOnClickListener {
            openLink(DONATE_URL)
        }
        binding.linkWebsite.setOnClickListener {
            openLink(GITHUB_URL)
        }
        binding.linkTransfer.setOnClickListener {
            openLink(TRANSFER_URL)
        }

    }

    private fun openLink(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        startActivity(intent)
    }

}