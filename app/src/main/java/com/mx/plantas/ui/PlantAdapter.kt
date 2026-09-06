package com.mx.plantas.ui

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mx.plantas.data.Plant
import com.mx.plantas.databinding.ItemPlantBinding
import java.io.File

class PlantAdapter(
    private val onPlantClick: (Plant) -> Unit
) : ListAdapter<Plant, PlantAdapter.PlantViewHolder>(PlantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = ItemPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlantViewHolder(
        private val binding: ItemPlantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
            binding.plantName.text = plant.name
            binding.plantScientificName.text = plant.scientificName
            binding.plantFamily.text = plant.family
            binding.plantTag.text = plant.careLevel
            
            // Intentar cargar imagen desde archivo (plantas del usuario)
            val fileImageLoaded = try {
                val file = File(plant.imageResName)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    binding.plantImage.setImageBitmap(bitmap)
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
                        binding.plantImage.setImageResource(resourceId)
                    }
                } catch (e: Exception) {
                    // Si falla, mantiene la imagen por defecto
                }
            }
            
            binding.root.setOnClickListener { onPlantClick(plant) }
        }
    }

    class PlantDiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean = oldItem == newItem
    }
}
