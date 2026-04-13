package com.example.indonesianevents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indonesianevents.databinding.FragmentFinishingEventBinding
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ViewModelFactory

class FinishingEventFragment : Fragment() {
    private var _binding: FragmentFinishingEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(SettingPreference.getInstance(requireContext().dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishingEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())

        val adapter = EventAdapter { event ->
            val bundle = Bundle().apply {
                putInt("id", event.id)
            }
            findNavController().navigate(R.id.action_finishingEventFragment_to_detailEventFragment, bundle)
        }
        binding.rvEvent.adapter = adapter

        viewModel.eventListEntity.observe(viewLifecycleOwner) { data ->
            adapter.setData(data)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        if (savedInstanceState == null) {
            viewModel.getDataFinishEvent(requireContext())
        } else {
            viewModel.getEventEntity(requireContext(), "finished")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
