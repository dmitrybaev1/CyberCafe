package ru.shawarma.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import ru.shawarma.settings.adapters.SettingsStateAdapter
import ru.shawarma.settings.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager = binding!!.profileViewPager
        val tabLayout = binding!!.profileTabLayout
        viewPager.adapter = SettingsStateAdapter(this)
        TabLayoutMediator(tabLayout,viewPager){ tab,position ->
            when(position){
                0 -> tab.text = resources.getString(R.string.info)
                1 -> tab.text = resources.getString(R.string.orders)
            }
        }.attach()
    }
}