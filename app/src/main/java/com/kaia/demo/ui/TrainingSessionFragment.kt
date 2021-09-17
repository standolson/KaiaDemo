package com.kaia.demo.ui

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kaia.demo.R
import com.kaia.demo.databinding.TrainingSessionFragmentBinding
import com.kaia.demo.model.Exercise
import com.kaia.demo.util.ExercisePrefsDatabase
import com.kaia.demo.viewmodel.ExercisesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class TrainingSessionFragment : Fragment() {

    private var _binding: TrainingSessionFragmentBinding? = null
    private val binding
        get() = _binding!!
    private var slideIndex = -1
    private val handler = Handler()
    val viewModel: ExercisesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TrainingSessionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt(SLIDE_INDEX, slideIndex)
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null)
            slideIndex = savedInstanceState.getInt("slideIndex")

        binding.cancelTrainingButton.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.run {
            exercises.observe(viewLifecycleOwner, Observer { startSlideShow(it)})
            error.observe(viewLifecycleOwner, Observer { showError(viewModel) })
        }

        viewModel.loadExercises()
    }

    private fun startSlideShow(exercises: List<Exercise>) {
        if (slideIndex == -1)
            slideIndex = 0
        loadImage(exercises, slideIndex)
    }

    private fun loadImage(exercises: List<Exercise>, index: Int) {
        // If we've seen all exercises, return to the list
        if (index >= exercises.size) {
            findNavController().popBackStack()
            return
        }

        val exercise = exercises[index]

        // Update the favorite button text
        setFavoriteButtonText(exercise)
        binding.favoriteExerciseButton.setOnClickListener {
            updateFavorite(exercise)
        }

        // Show the current exercise
        if (exercise.cover_image_url != null)
            Glide.with(requireContext())
                .load(exercise.cover_image_url)
                .error(R.drawable.placeholder_image)
                .into(binding.overviewImage)
        else
            binding.overviewImage.setImageResource(R.drawable.placeholder_image)

        handler.postDelayed({ loadNextImage(exercises) }, NEXT_SLIDE_DELAY_MS)
    }

    private fun loadNextImage(exercises: List<Exercise>) {
        slideIndex += 1
        loadImage(exercises, slideIndex)
    }

    private fun updateFavorite(exercise: Exercise) {
        val isFavorite = ExercisePrefsDatabase.getFavoriteState(requireContext(), exercise.id)
        ExercisePrefsDatabase.setFavoriteState(requireContext(), exercise.id, !isFavorite)
        setFavoriteButtonText(exercise)
    }

    private fun setFavoriteButtonText(exercise: Exercise) {
        if (ExercisePrefsDatabase.getFavoriteState(requireContext(), exercise.id))
            binding.favoriteExerciseButton.setText(R.string.unfavorite_exercise)
        else
            binding.favoriteExerciseButton.setText(R.string.favorite_exercise)
    }

    private fun showError(viewModel: ExercisesViewModel) {
        Toast.makeText(context, "Error: " + viewModel.error.value, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }

    companion object {
        private val SLIDE_INDEX = "slideIndex"
        private val NEXT_SLIDE_DELAY_MS = 5000L
    }
}