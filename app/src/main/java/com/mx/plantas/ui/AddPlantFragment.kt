package com.mx.plantas.ui

import android.content.Context
import android.graphics.Bitmap
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

class AddPlantFragment : Fragment() {
    private var _binding: FragmentAddPlantBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var imageBitmap: Bitmap? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                imageBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
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

        binding.buttonChooseImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.buttonSave.setOnClickListener {
            savePlant()
        }
    }

    private fun savePlant() {
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

        if (imageBitmap == null) {
            Toast.makeText(context, "Selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val imageResName = saveImageToDrawable(imageBitmap!!)
        if (imageResName == null) {
            Toast.makeText(context, "Error guardando imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val newPlant = Plant(
            name = name,
            scientificName = scientificName,
            family = family,
            description = description,
            careLevel = careLevel,
            waterNeed = waterNeed,
            sunlight = sunlight,
            difficulty = difficulty,
            imageResName = imageResName,
            isFavorite = false
        )

        lifecycleScope.launch {
            try {
                viewModel.addPlant(newPlant)
                Toast.makeText(context, "Planta guardada exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } catch (e: Exception) {
                Toast.makeText(context, "Error guardando planta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToDrawable(bitmap: Bitmap): String? {
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
