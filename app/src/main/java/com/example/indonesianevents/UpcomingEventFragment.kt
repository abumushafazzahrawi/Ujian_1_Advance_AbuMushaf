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
import com.example.indonesianevents.databinding.FragmentUpcomingEventBinding
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ViewModelFactory

class UpcomingEventFragment : Fragment() {
    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(SettingPreference.getInstance(requireContext().dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventAdapter { event ->
            val bundle = Bundle().apply {
                putInt("id", event.id)
            }
            findNavController().navigate(R.id.action_upcomingEventFragment_to_detailEventFragment, bundle)
        }

        val searchAdapter = EventAdapter { event ->
            val bundle = Bundle().apply {
                putInt("id", event.id)
            }
            findNavController().navigate(R.id.action_upcomingEventFragment_to_detailEventFragment, bundle)
        }

        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = adapter

        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = searchAdapter

        viewModel.eventListEntity.observe(viewLifecycleOwner) { data ->
            adapter.setData(data)
        }

        viewModel.listEvent.observe(viewLifecycleOwner) { items ->
            val entities = items.map {
                com.example.database.EventEntity(
                    id = it.id,
                    name = it.name,
                    summary = it.summary,
                    description = it.description,
                    imageLogo = it.imageLogo,
                    mediaCover = it.mediaCover,
                    category = it.category,
                    ownerName = it.ownerName,
                    cityName = it.cityName,
                    quota = it.quota,
                    registrants = it.registrants,
                    beginTime = it.beginTime,
                    endTime = it.endTime,
                    link = it.link,
                    type = "search"
                )
            }
            searchAdapter.setData(entities)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                searchBar.setText(searchView.text)
                viewModel.searchEvents(requireContext(), searchView.text.toString())
                false
            }
        }

        if (savedInstanceState == null) {
            viewModel.getDataEvent(requireContext())
        } else {
            viewModel.getEventEntity(requireContext(), "upcoming")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
