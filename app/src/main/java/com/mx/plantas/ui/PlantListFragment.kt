package com.mx.plantas.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mx.plantas.R
import com.mx.plantas.data.Plant
import com.mx.plantas.databinding.FragmentPlantListBinding
import kotlinx.coroutines.launch

class PlantListFragment : Fragment() {
    private var _binding: FragmentPlantListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by viewModels()

    private lateinit var plantAdapter: PlantAdapter

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { handleScannedBitmap(it) }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                handleScannedBitmap(bitmap)
            } catch (e: Exception) {
                showScanFeedback("Error al cargar la imagen: ${e.message}")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        plantAdapter = PlantAdapter { plant ->
            val bundle = Bundle().apply { putLong("plant_id", plant.id) }
            findNavController().navigate(R.id.action_plantListFragment_to_plantDetailFragment, bundle)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = plantAdapter

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.onSearchChanged(s?.toString().orEmpty())
            }
        })

        // Botón para escanear con cámara
        binding.scanPlantButton.setOnClickListener {
            takePictureLauncher.launch(null)
        }

        // Botón para buscar en galería
        binding.galleryPlantButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Botón para agregar nueva planta
        binding.addPlantButton.setOnClickListener {
            findNavController().navigate(R.id.action_plantListFragment_to_addPlantFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredPlants.collect { plants ->
                    plantAdapter.submitList(plants)
                    binding.emptyState.isVisible = plants.isEmpty()
                }
            }
        }
    }

    private fun handleScannedBitmap(bitmap: Bitmap) {
        showScanFeedback("Analizando imagen de la planta...")

        viewLifecycleOwner.lifecycleScope.launch {
            val (matchedPlant, debugInfo) = viewModel.findPlantByImageAsync(bitmap)
            if (matchedPlant != null) {
                showPlantMatchDialog(matchedPlant)
            } else {
                showDebugDialog(debugInfo)
            }
        }
    }

    private fun showDebugDialog(debugInfo: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("No se identificó la planta")
            .setMessage("$debugInfo\n\n💡 Intenta con otra imagen más clara o agrega la planta manualmente.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showPlantMatchDialog(plant: Plant) {
        val details = buildString {
            append("Nombre científico: ${plant.scientificName}\n")
            append("Familia: ${plant.family}\n")
            append("Nivel de cuidado: ${plant.careLevel}\n")
            append("Riego: ${plant.waterNeed}\n")
            append("Luz: ${plant.sunlight}\n")
            append("Dificultad: ${plant.difficulty}\n\n")
            append(plant.description)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Planta identificada: ${plant.name}")
            .setMessage(details)
            .setPositiveButton("Ver detalles completos") { _, _ ->
                val bundle = Bundle().apply { putLong("plant_id", plant.id) }
                findNavController().navigate(R.id.action_plantListFragment_to_plantDetailFragment, bundle)
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun showScanFeedback(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

