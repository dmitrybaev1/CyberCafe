package ru.shawarma.settings

import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.shawarma.settings.viewmodels.SettingsViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by hiltNavGraphViewModels(R.id.settings_nav_graph)
}