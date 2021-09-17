package com.kaia.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaia.demo.R
import com.kaia.demo.databinding.OverviewFragmentBinding
import com.kaia.demo.model.Exercise
import com.kaia.demo.viewmodel.ExercisesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class OverviewFragment : Fragment() {

    private var _binding: OverviewFragmentBinding? = null
    private val binding
        get() = _binding!!
    val viewModel: ExercisesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = OverviewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        binding.startTrainingButton.setOnClickListener {
            findNavController().navigate(R.id.action_OverviewFragment_to_TrainingSessionFragment)
        }

        viewModel.run {
            exercises.observe(viewLifecycleOwner, { showExercises(it) })
            error.observe(viewLifecycleOwner, { showError(viewModel) })
        }

        // Only load exercises if they haven't been loaded already
        if (viewModel.exercises.value == null)
            viewModel.loadExercises()
    }

    private fun showExercises(exercises: List<Exercise>)
    {
        val recyclerView = binding.root.findViewById(R.id.recycler_contents) as RecyclerView
        val adapter = ExercisesAdapter(exercises, ExerciseListener { _ -> startTrainingSession() })
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun startTrainingSession()
    {
        Toast.makeText(context, "Start training session", Toast.LENGTH_LONG).show()
    }

    private fun showError(viewModel: ExercisesViewModel) {
        Toast.makeText(context, "Error: " + viewModel.error.value, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }
}