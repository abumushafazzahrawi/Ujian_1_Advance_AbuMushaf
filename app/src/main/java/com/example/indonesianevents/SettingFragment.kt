package com.example.indonesianevents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.event.Event
import com.example.indonesianevents.databinding.FragmentSettingBinding
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ViewModelFactory

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = SettingPreference.getInstance(requireContext().dataStore)
        val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]

        mainViewModel.getThemeSettings()
            .observe(viewLifecycleOwner) { isDarkModeActive: Boolean, ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    binding.switchTheme.isChecked = true
                    binding.tvEnableDarkMode.visibility = View.VISIBLE
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    binding.switchTheme.isChecked = false
                    binding.tvDisableDarkMode.visibility = View.VISIBLE
                    binding.tvEnableDarkMode.visibility = View.GONE
                }

                binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
                    mainViewModel.saveThemeSetting(isChecked)
                }
            }
    }
}