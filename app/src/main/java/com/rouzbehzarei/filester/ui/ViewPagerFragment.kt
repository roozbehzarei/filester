package com.rouzbehzarei.filester.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rouzbehzarei.filester.R
import com.rouzbehzarei.filester.databinding.FragmentPagerBinding

private const val NUM_PAGES = 2
private lateinit var viewPager: ViewPager2

class ViewPagerFragment : Fragment() {

    // Binding object instance with access to the views in the fragment_pager.xml layout
    private lateinit var binding: FragmentPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = FragmentPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = binding.pager
        val pagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        val tabLayout: TabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.tab_upload)
                    tab.icon = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_outline_cloud_upload_24
                    )
                }
                else -> {
                    tab.text = getString(R.string.tab_history)
                    tab.icon = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_outline_storage_24
                    )
                }
            }
        }.attach()
    }

    inner class ViewPagerAdapter(f: Fragment) : FragmentStateAdapter(f) {

        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> UploadFragment()
                else -> HistoryFragment()
            }
        }
    }
}