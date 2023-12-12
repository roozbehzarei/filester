package com.roozbehzarei.filester.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.databinding.FragmentAboutBinding

private const val WEBSITE_URL = "https://roozbehzarei.me/project/filester"
private const val DONATE_URL = "https://roozbehzarei.me/donate"
private const val PRIVACY_POLICY_URL = "https://roozbehzarei.me/filester/privacy-policy"

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
            openLink(WEBSITE_URL)
        }

        binding.donateViewHolder.setOnClickListener {
            openLink(DONATE_URL)
        }

        binding.privacyPolicyViewHolder.setOnClickListener {
            openLink(PRIVACY_POLICY_URL)
        }

    }

    /**
     * Open the passed [url] in browser
     */
    private fun openLink(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(url)
        )
        startActivity(intent)
    }

}