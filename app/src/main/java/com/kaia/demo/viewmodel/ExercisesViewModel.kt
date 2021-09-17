package com.kaia.demo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kaia.demo.model.Exercise
import com.kaia.demo.util.ResponseData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val repository: ExercisesRepository
) : BaseViewModel()
{
    val exercises = MutableLiveData<List<Exercise>>()

    @InternalCoroutinesApi
    fun loadExercises() : ExercisesViewModel {
        viewModelScope.launch {
            repository.getExerciseList().collect { value -> onRepositoryUpdated(value) }
        }
        return this
    }

    private fun onRepositoryUpdated(response: ResponseData<List<Exercise>>) {
        if (response.hasStatusMessage())
            error.value = response.errorStatus
        else
            exercises.value = response.data
    }
}