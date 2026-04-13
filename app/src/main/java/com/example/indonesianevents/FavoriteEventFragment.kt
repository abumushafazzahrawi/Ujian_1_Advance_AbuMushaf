package com.example.indonesianevents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indonesianevents.databinding.FragmentFavoriteEventBinding
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ViewModelFactory

class FavoriteEventFragment : Fragment() {
    private var _binding: FragmentFavoriteEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(SettingPreference.getInstance(requireContext().dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventAdapter { event ->
            val bundle = Bundle().apply {
                putInt("id", event.id)
            }
            findNavController().navigate(R.id.action_favoriteEventFragment_to_detailEventFragment, bundle)
        }

        binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorite.adapter = adapter

        viewModel.favoriteEvents.observe(viewLifecycleOwner) { favorites ->
            adapter.setData(favorites)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoriteEvents(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
