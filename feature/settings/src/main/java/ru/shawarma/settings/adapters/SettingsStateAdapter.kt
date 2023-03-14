package ru.shawarma.settings.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.shawarma.settings.InfoFragment
import ru.shawarma.settings.OrdersFragment

class SettingsStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> InfoFragment()
            1 -> OrdersFragment()
            else -> throw IllegalStateException()
        }
    }

}