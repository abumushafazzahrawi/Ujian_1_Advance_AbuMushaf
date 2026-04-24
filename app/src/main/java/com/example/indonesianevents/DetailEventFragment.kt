package com.example.indonesianevents

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.database.EventEntity
import com.example.indonesianevents.databinding.FragmentDetailEventBinding
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ViewModelFactory
import androidx.core.net.toUri
import com.example.helper.DateHelper

class DetailEventFragment : Fragment() {
    private var _binding: FragmentDetailEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(SettingPreference.getInstance(requireContext().dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getInt("id") ?: -1
        
        if (savedInstanceState == null) {
            viewModel.getDetailEvent(requireContext(), eventId)
        }

        viewModel.detailEvent.observe(viewLifecycleOwner) { event ->
            if (event != null) {
                displayEventDetails(event)
                updateFavoriteIcon(event.isFavorite)
                
                binding.ivFavorite.setOnClickListener {
                    Toast.makeText(requireContext(), "Favorite status updated", Toast.LENGTH_SHORT).show()
                    viewModel.setFavoriteEvent(requireContext(), event, !event.isFavorite)
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayEventDetails(event: EventEntity) {
        binding.tvNama.text = event.name
        binding.tvSummary.text = event.summary
        binding.tvDescription.text = HtmlCompat.fromHtml(
            event.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.tvBeginTime.text = "Mulai: ${DateHelper.getCurrentDate(event.beginTime)}"
        binding.tvEndTime.text = "Berakhir: ${DateHelper.getCurrentDate(event.endTime)}"
        binding.tvQuota.text = "Quota: ${event.quota}"
        binding.tvRegistrants.text = "Registrants: ${event.registrants}"
        binding.tvCategory.text = event.category
        binding.tvOwnerName.text = event.ownerName
        binding.tvCityName.text = event.cityName

        Glide.with(requireContext())
            .load(event.mediaCover)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.ivPicture)

        binding.btnRegister.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, event.link.toUri())
            startActivity(intent)
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.ivFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_border))
        } else {
            binding.ivFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
