package com.example.indonesianevents

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.indonesianevents.databinding.FragmentSettingBinding
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ViewModelFactory
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Notifications permission rejected", Toast.LENGTH_SHORT).show()
                binding.switchReminder.isChecked = false
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = SettingPreference.getInstance(requireContext().dataStore)
        val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]

        mainViewModel.getThemeSettings()
            .observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    binding.switchTheme.isChecked = true
                    binding.tvEnableDarkMode.visibility = View.VISIBLE
                    binding.tvDisableDarkMode.visibility = View.GONE
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    binding.switchTheme.isChecked = false
                    binding.tvDisableDarkMode.visibility = View.VISIBLE
                    binding.tvEnableDarkMode.visibility = View.GONE
                }
            }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            mainViewModel.saveThemeSetting(isChecked)
        }

        mainViewModel.getReminderSettings().observe(viewLifecycleOwner) { isReminderActive: Boolean ->
            binding.switchReminder.setOnCheckedChangeListener(null)
            binding.switchReminder.isChecked = isReminderActive
            if (isReminderActive) {
                binding.tvDisableNotification.visibility = View.VISIBLE
                binding.tvEnableNotification.visibility = View.GONE
            } else {
                binding.tvEnableNotification.visibility = View.VISIBLE
                binding.tvDisableNotification.visibility = View.GONE
            }

            binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    startPeriodicWork()
                    mainViewModel.saveReminderSetting(true)
                    Toast.makeText(requireContext(), "Daily Reminder Activated", Toast.LENGTH_SHORT).show()
                } else {
                    cancelPeriodicWork()
                    mainViewModel.saveReminderSetting(false)
                    Toast.makeText(requireContext(), "Daily Reminder Deactivated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startPeriodicWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag("DailyReminder")
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "DailyReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    private fun cancelPeriodicWork() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyReminderWork")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}