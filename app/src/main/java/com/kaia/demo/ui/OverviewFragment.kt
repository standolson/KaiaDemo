package com.kaia.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kaia.demo.R
import com.kaia.demo.databinding.OverviewFragmentBinding
import com.kaia.demo.viewmodel.ExercisesViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaia.demo.model.Exercise
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OverviewFragment : Fragment() {

    private var _binding: OverviewFragmentBinding? = null
    private val binding
        get() = _binding!!
    val viewModel: ExercisesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        _binding = OverviewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        binding.startTrainingButton.setOnClickListener {
            findNavController().navigate(R.id.action_OverviewFragment_to_TrainingSessionFragment)
        }

        viewModel.run {
            exercises.observe(viewLifecycleOwner, Observer { showExercises(it) })
            error.observe(viewLifecycleOwner, Observer { showError(viewModel) })
        }

        viewModel.loadExercises()
    }

    private fun showExercises(exercises: List<Exercise>)
    {
        val recyclerView = binding.root.findViewById(R.id.recycler_contents) as RecyclerView
        val adapter = ExercisesAdapter(exercises, ExerciseListener { exercise -> startTrainingSession() })
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun startTrainingSession()
    {
        Toast.makeText(context, "Start training session", Toast.LENGTH_LONG).show()
    }

    protected fun showError(viewModel: ExercisesViewModel) {
        Toast.makeText(context, "Error: " + viewModel.error.value, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }
}