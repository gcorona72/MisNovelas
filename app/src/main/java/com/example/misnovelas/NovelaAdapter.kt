package com.example.misnovelas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NovelaAdapter(
    private val context: Context,
    private val novelas: List<Novela>,
    private val onFavoriteChanged: (Novela) -> Unit,
    private val onItemClick: (Novela) -> Unit
) : RecyclerView.Adapter<NovelaAdapter.NovelaViewHolder>() {

    inner class NovelaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(context.resources.getIdentifier("nombreTextView", "id", context.packageName))
        val descripcionTextView: TextView = itemView.findViewById(context.resources.getIdentifier("descripcionTextView", "id", context.packageName))
        val valoracionTextView: TextView = itemView.findViewById(context.resources.getIdentifier("valoracionTextView", "id", context.packageName))
        val favoriteCheckBox: CheckBox = itemView.findViewById(context.resources.getIdentifier("favoriteCheckBox", "id", context.packageName))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NovelaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(context.resources.getIdentifier("item_novela", "layout", context.packageName), parent, false)
        return NovelaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NovelaViewHolder, position: Int) {
        val novela = novelas[position]
        holder.nombreTextView.text = "${novela.nombre} (${novela.año})"
        holder.descripcionTextView.text = novela.descripcion
        holder.valoracionTextView.text = "Valoración: ${novela.valoracion}"
        holder.favoriteCheckBox.isChecked = novela.isFavorite

        // Set text color based on dark mode
        val isDarkMode = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getBoolean("dark_mode", false)
        val textColor = if (isDarkMode) {
            0xFFFFFFFF.toInt() // White color for dark mode
        } else {
            0xFF000000.toInt() // Black color for light mode
        }

        holder.nombreTextView.setTextColor(textColor)
        holder.descripcionTextView.setTextColor(textColor)
        holder.valoracionTextView.setTextColor(textColor)

        holder.favoriteCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val updatedNovela = novela.copy(isFavorite = isChecked)
            onFavoriteChanged(updatedNovela)
        }

        holder.itemView.setOnClickListener {
            onItemClick(novela)
        }
    }

    override fun getItemCount() = novelas.size
}