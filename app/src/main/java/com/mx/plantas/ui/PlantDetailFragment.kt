package com.mx.plantas.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.mx.plantas.data.Plant
import com.mx.plantas.databinding.FragmentPlantDetailBinding
import kotlinx.coroutines.launch
import java.io.File

class PlantDetailFragment : Fragment() {
    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    private var currentPlant: Plant? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plantId = arguments?.getLong("plant_id", 0L) ?: 0L
        viewModel.loadPlant(plantId)

        binding.editButton.setOnClickListener {
            currentPlant?.let { plant ->
                val bundle = Bundle().apply { putLong("plantId", plant.id) }
                findNavController().navigate(
                    com.mx.plantas.R.id.action_plantDetailFragment_to_editPlantFragment,
                    bundle
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedPlant.collect { plant ->
                    plant?.let { 
                        currentPlant = it
                        bindPlant(it) 
                    }
                }
            }
        }
    }

    private fun bindPlant(plant: Plant) {
        binding.plantName.text = plant.name
        binding.scientificName.text = plant.scientificName
        binding.plantFamily.text = plant.family
        binding.careLevel.text = plant.careLevel
        binding.waterNeed.text = plant.waterNeed
        binding.sunlight.text = plant.sunlight
        binding.difficulty.text = plant.difficulty
        binding.description.text = plant.description

        // Cargar imagen desde archivo primero (plantas del usuario)
        val fileImageLoaded = try {
            val file = File(plant.imageResName)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.plantImageDetail.setImageBitmap(bitmap)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }

        // Si no se cargó desde archivo, intentar desde recursos
        if (!fileImageLoaded) {
            try {
                val resourceId = binding.root.context.resources.getIdentifier(
                    plant.imageResName,
                    "drawable",
                    binding.root.context.packageName
                )
                if (resourceId != 0) {
                    binding.plantImageDetail.setImageResource(resourceId)
                }
            } catch (e: Exception) {
                // Si falla, mantiene la imagen por defecto
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
