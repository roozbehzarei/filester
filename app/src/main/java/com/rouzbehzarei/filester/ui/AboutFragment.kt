package com.rouzbehzarei.filester.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.rouzbehzarei.filester.R
import com.rouzbehzarei.filester.databinding.FragmentAboutBinding

private const val GITHUB_URL = "https://github.com/roozbehzarei"
private const val DONATE_URL = "https://www.buymeacoffee.com/roozbehzarei/"
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
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.action_about).isVisible = false
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    /**
     * Open the passed [url] in browser
     */
    private fun openLink(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        startActivity(intent)
    }

}