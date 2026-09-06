package com.mx.plantas.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mx.plantas.data.Plant
import com.mx.plantas.databinding.FragmentAddPlantBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class EditPlantFragment : Fragment() {
    private var _binding: FragmentAddPlantBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by viewModels()
    
    private var selectedPlant: Plant? = null
    private var selectedImageUri: Uri? = null
    private var imageBitmap: Bitmap? = null
    private var imageChanged = false

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imageChanged = true
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                imageBitmap = BitmapFactory.decodeStream(inputStream)
                binding.plantImage.setImageBitmap(imageBitmap)
            } catch (e: Exception) {
                Toast.makeText(context, "Error cargando imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cambiar título
        binding.titleAddPlant.text = "Editar Planta"
        binding.buttonSave.text = "Guardar Cambios"

        // Cargar datos de la planta
        val plantId = arguments?.getLong("plantId", 0L) ?: 0L
        lifecycleScope.launch {
            selectedPlant = viewModel.loadPlantForEdit(plantId)
            selectedPlant?.let { plant ->
                binding.editName.setText(plant.name)
                binding.editScientificName.setText(plant.scientificName)
                binding.editFamily.setText(plant.family)
                binding.editDescription.setText(plant.description)
                binding.editCareLevel.setText(plant.careLevel)
                binding.editWaterNeed.setText(plant.waterNeed)
                binding.editSunlight.setText(plant.sunlight)
                binding.editDifficulty.setText(plant.difficulty)
                
                // Cargar imagen actual
                try {
                    val file = File(plant.imageResName)
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        binding.plantImage.setImageBitmap(bitmap)
                        imageBitmap = bitmap
                    }
                } catch (e: Exception) {
                    // No hacer nada si la imagen no existe
                }
            }
        }

        binding.buttonChooseImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.buttonSave.setOnClickListener {
            savePlant()
        }
    }

    private fun savePlant() {
        val plant = selectedPlant ?: return
        
        val name = binding.editName.text.toString().trim()
        val scientificName = binding.editScientificName.text.toString().trim()
        val family = binding.editFamily.text.toString().trim()
        val description = binding.editDescription.text.toString().trim()
        val careLevel = binding.editCareLevel.text.toString().trim()
        val waterNeed = binding.editWaterNeed.text.toString().trim()
        val sunlight = binding.editSunlight.text.toString().trim()
        val difficulty = binding.editDifficulty.text.toString().trim()

        if (name.isEmpty() || scientificName.isEmpty() || family.isEmpty()) {
            Toast.makeText(context, "Completa los campos requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        var imageResName = plant.imageResName
        
        // Si la imagen cambió, guardarla
        if (imageChanged && imageBitmap != null) {
            val newImagePath = saveImageToCache(imageBitmap!!)
            if (newImagePath == null) {
                Toast.makeText(context, "Error guardando imagen", Toast.LENGTH_SHORT).show()
                return
            }
            imageResName = newImagePath
        }

        val updatedPlant = plant.copy(
            name = name,
            scientificName = scientificName,
            family = family,
            description = description,
            careLevel = careLevel,
            waterNeed = waterNeed,
            sunlight = sunlight,
            difficulty = difficulty,
            imageResName = imageResName
        )

        lifecycleScope.launch {
            try {
                viewModel.updatePlant(updatedPlant)
                Toast.makeText(context, "Planta actualizada exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } catch (e: Exception) {
                Toast.makeText(context, "Error actualizando planta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToCache(bitmap: Bitmap): String? {
        return try {
            val fileName = "plant_${System.currentTimeMillis()}.jpg"
            val cacheDir = requireContext().cacheDir
            val imagesDir = File(cacheDir, "plant_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            val file = File(imagesDir, fileName)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
