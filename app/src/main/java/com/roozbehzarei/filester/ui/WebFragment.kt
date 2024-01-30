package com.roozbehzarei.filester.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.databinding.FragmentWebBinding

class WebFragment : Fragment() {

    private lateinit var binding: FragmentWebBinding
    private lateinit var webView: WebView
    private val args: WebFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebBinding.inflate(layoutInflater)

        // Initialize appbar menu specific to this fragment
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.app_bar_web, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_refresh -> webView.reload()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // If possible, go back to previous webpage
        // Otherwise, remove current fragment from back stack
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (webView.canGoBack()) webView.goBack()
            else findNavController().popBackStack()
        }.isEnabled

        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure webView settings
        webView = binding.webView
        webView.webViewClient = FilesterWebViewClient()
        webView.webChromeClient = FilesterWebChromeClient()
        with(webView.settings) {
            // Enable JavaScript execution
            javaScriptEnabled = true
            // Enable DOM storage API
            domStorageEnabled = true
            // Disable support for zooming using webView's on-screen zoom controls and gestures
            setSupportZoom(false)
        }
        // If dark theme is turned on, automatically render all web contents using a dark theme
        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(webView.settings, true)
                }

                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(webView.settings, false)
                }
            }
        }

        webView.loadUrl(args.webpageUrl)
    }

    /**
     * Check if any item of a given array contains the given input
     */
    private fun isInArray(input: String, collection: Array<String>): Boolean {
        collection.forEach {
            if (it in input) {
                return true
            }
        }
        return false
    }

    private inner class FilesterWebViewClient : WebViewClient() {

        /**
         * Let [webView] load the webpage if its URL is allowed
         * Otherwise, launch another app to open the URL
         */
        override fun shouldOverrideUrlLoading(
            view: WebView?, request: WebResourceRequest?
        ): Boolean {
            val currentUrl = request?.url
            if (isInArray(currentUrl.toString(), args.allowedUrls)) {
                return false
            }
            try {
                Intent(Intent.ACTION_VIEW, currentUrl).apply {
                    startActivity(this)
                }
            } catch (_: Exception) {
                Toast.makeText(
                    requireContext(), getString(R.string.toast_app_not_found), Toast.LENGTH_LONG
                ).show()
            }
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.progressIndicator.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.progressIndicator.visibility = View.GONE
        }

    }

    /**
     * Update the progress bar when loading a webpage
     */
    private inner class FilesterWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            binding.progressIndicator.progress = newProgress
        }
    }
}
