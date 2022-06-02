package com.rouzbehzarei.filester.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.work.WorkInfo
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rouzbehzarei.filester.BaseApplication
import com.rouzbehzarei.filester.R
import com.rouzbehzarei.filester.databinding.FragmentPagerBinding
import com.rouzbehzarei.filester.viewmodel.FilesterViewModel
import com.rouzbehzarei.filester.viewmodel.FilesterViewModelFactory

private const val NUM_PAGES = 2
private lateinit var viewPager: ViewPager2

class ViewPagerFragment : Fragment() {

    // Binding object instance with access to the views in the fragment_pager.xml layout
    private lateinit var binding: FragmentPagerBinding

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
        binding = FragmentPagerBinding.inflate(inflater, container, false)
        viewModel.outputWorkInfo.observe(viewLifecycleOwner, workInfoObserver())
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

    private fun workInfoObserver(): Observer<List<WorkInfo>> {
        return Observer {
            // If there are no matching work info, do nothing
            if (it.isNullOrEmpty()) {
                return@Observer
            }
            val workInfo = it[0]
            if (!workInfo.state.isFinished) {
                Log.d("ViewPagerFragment", "View set to be visible")
                binding.progressIndicator.visibility = View.VISIBLE
            } else {
                Log.d("ViewPagerFragment", "View set to be gone")
                binding.progressIndicator.visibility = View.GONE
            }
        }

    }

}