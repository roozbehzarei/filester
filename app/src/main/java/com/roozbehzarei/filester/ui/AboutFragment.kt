package com.roozbehzarei.filester.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.databinding.FragmentAboutBinding

const val BASE_URL = "https://roozbehzarei.me"
const val WEBSITE_URL = "https://roozbehzarei.me/project/filester"
const val DONATE_URL = "https://roozbehzarei.me/donate"
const val PRIVACY_POLICY_URL = "https://roozbehzarei.me/filester/privacy-policy"
const val STATUS_URL = "https://roozbehzarei.github.io/filester-status"

class AboutFragment : Fragment() {

    // Binding object instance with access to the views in the fragment_about.xml layout
    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = FragmentAboutBinding.inflate(inflater, container, false)

        binding.appVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.websiteViewHolder.setOnClickListener {
            openInBrowser(WEBSITE_URL)
        }

        binding.donateViewHolder.setOnClickListener {
            openInBrowser(DONATE_URL)
        }

        binding.privacyPolicyViewHolder.setOnClickListener {
            findNavController().navigate(
                AboutFragmentDirections.actionGlobalWebFragment(
                    PRIVACY_POLICY_URL, arrayOf(BASE_URL, PRIVACY_POLICY_URL)
                )
            )
        }

    }

    /**
     * Open passed [url] in web browser
     */
    private fun openInBrowser(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(url)
        )
        try {
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(
                requireContext(), getString(R.string.toast_app_not_found), Toast.LENGTH_LONG
            ).show()
        }
    }

}