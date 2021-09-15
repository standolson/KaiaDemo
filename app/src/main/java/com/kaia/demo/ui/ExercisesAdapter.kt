package com.kaia.demo.ui

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaia.demo.databinding.ExerciseOverviewBinding
import com.kaia.demo.model.Exercise
import com.kaia.demo.R

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
        viewHolder.binding.overviewExerciseFavorite.isChecked = getFavoriteState(context, exercise.id)

        // Put a click handler on the favorite button so we can update the
        // list of favorites
        viewHolder.binding.overviewExerciseFavorite.setOnCheckedChangeListener { button, isChecked ->
            Toast.makeText(button.context, "isChecked " + button.isChecked, Toast.LENGTH_LONG).show()
            setFavoriteState(context, exercise.id, isChecked)
        }
    }

    override fun getItemCount() = list.size

    private fun getFavoriteState(context: Context, id: Int?): Boolean
    {
        if (id == null)
            return false

        val prefs =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE) ?: return false
        val favoriteList = getFavoriteList(prefs)
        if (favoriteList.size == 0)
            return false

        val state = favoriteList.contains(id.toString())
        return state
    }

    private fun setFavoriteState(context: Context, id: Int?, state: Boolean)
    {
        if (id == null)
            return

        val prefs =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE) ?: return
        val favoriteList = getFavoriteList(prefs)

        // Adding
        if (state) {
            if (favoriteList.contains(id.toString()))
                return
            favoriteList.add(id.toString())
            writeFavoriteList(prefs, favoriteList)
            return
        }

        // Removing
        if (!favoriteList.contains(id.toString()))
            return
        favoriteList.remove(id.toString())
        writeFavoriteList(prefs, favoriteList)
    }

    private fun getFavoriteList(prefs: SharedPreferences): MutableList<String>
    {
        val favoriteList = prefs.getString(FAVORITES_KEY, null)
        if (favoriteList == null || favoriteList.length == 0)
            return mutableListOf()
        val list = favoriteList.split(",")
        return list.toMutableList()
    }

    private fun writeFavoriteList(prefs: SharedPreferences, favorites: MutableList<String>)
    {
        val builder: StringBuilder = StringBuilder()

        for (item in favorites) {
            if (builder.length == 0)
                builder.append(item)
            else
                builder.append("," + item)
        }

        with (prefs.edit()) {
            putString(FAVORITES_KEY, builder.toString())
            apply()
        }
    }

    companion object {
        val PREFERENCES_NAME = "favorites_pref"
        val FAVORITES_KEY = "favorites_list"
    }
}

class ExerciseListener(val clickListener: (exercise: Exercise) -> Unit) {
    fun onClick(exercise: Exercise) = clickListener(exercise)
}