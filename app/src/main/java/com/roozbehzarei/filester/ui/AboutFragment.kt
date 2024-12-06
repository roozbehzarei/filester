package com.roozbehzarei.filester.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.databinding.FragmentAboutBinding

const val WEBSITE_URL = "https://roozbehzarei.com/project/filester"
const val DONATE_URL = "https://roozbehzarei.com/donate"
const val PRIVACY_POLICY_URL = "https://roozbehzarei.com/filester/privacy-policy"
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
            val intent = CustomTabsIntent.Builder().build()
            try {
                intent.launchUrl(requireActivity(), Uri.parse(PRIVACY_POLICY_URL))
            } catch (_: Exception) {
                Toast.makeText(
                    requireContext(), getString(R.string.toast_app_not_found), Toast.LENGTH_LONG
                ).show()
            }
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