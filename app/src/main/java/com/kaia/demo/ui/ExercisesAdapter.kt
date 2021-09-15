package com.kaia.demo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaia.demo.databinding.ExerciseOverviewBinding
import com.kaia.demo.model.Exercise

class ExercisesAdapter(val list: List<Exercise>, val clickListener: ExerciseListener) : RecyclerView.Adapter<ExercisesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ExerciseOverviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ExerciseOverviewBinding.inflate(inflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val exercise = list[position]
        viewHolder.binding.root.setOnClickListener { clickListener.onClick(exercise) }

        val image = viewHolder.binding.overviewImage
        Glide.with(image.context).load(exercise.cover_image_url).into(image)

        viewHolder.binding.overviewExerciseName.text = exercise.name

        viewHolder.binding.overviewExerciseFavorite.setOnCheckedChangeListener { button, isChecked ->
            button.isChecked = isChecked
            Toast.makeText(button.context, "isChecked " + isChecked, Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount() = list.size
}

class ExerciseListener(val clickListener: (exercise: Exercise) -> Unit) {
    fun onClick(exercise: Exercise) = clickListener(exercise)
}