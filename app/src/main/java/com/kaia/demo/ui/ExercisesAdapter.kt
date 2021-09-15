package com.kaia.demo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaia.demo.R
import com.kaia.demo.databinding.ExerciseOverviewBinding
import com.kaia.demo.model.Exercise
import com.kaia.demo.util.ExercisePrefsDatabase

class ExercisesAdapter(val list: List<Exercise>, val clickListener: ExerciseListener) : RecyclerView.Adapter<ExercisesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ExerciseOverviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ExerciseOverviewBinding.inflate(inflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val exercise = list[position]
        val context = viewHolder.binding.root.context

        // Put a click handler on the entire view so the user can tell
        // us to start the training session
        viewHolder.binding.root.setOnClickListener { clickListener.onClick(exercise) }

        // Load the remote image
        val image = viewHolder.binding.overviewImage
        if (exercise.cover_image_url != null)
            Glide.with(context)
                .load(exercise.cover_image_url)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fallback(R.drawable.placeholder_image)
                .into(image)
        else
            image.setImageResource(R.drawable.placeholder_image)

        // Set the exercise name
        viewHolder.binding.overviewExerciseName.text = exercise.name

        // Set the initial state of the favorite checkbox from our stored list
        viewHolder.binding.overviewExerciseFavorite.isChecked =
            ExercisePrefsDatabase.getFavoriteState(context, exercise.id)

        // Put a click handler on the favorite button so we can update the
        // list of favorites
        viewHolder.binding.overviewExerciseFavorite.setOnCheckedChangeListener { button, isChecked ->
            ExercisePrefsDatabase.setFavoriteState(context, exercise.id, isChecked)
        }
    }

    override fun getItemCount() = list.size
}

class ExerciseListener(val clickListener: (exercise: Exercise) -> Unit) {
    fun onClick(exercise: Exercise) = clickListener(exercise)
}