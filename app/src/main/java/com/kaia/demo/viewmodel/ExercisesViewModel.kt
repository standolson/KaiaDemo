package com.kaia.demo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kaia.demo.model.Exercise
import com.kaia.demo.util.ResponseData
import com.kaia.demo.util.observeOnce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val repository: ExercisesRepository
) : BaseViewModel()
{
    var exercises = MutableLiveData<List<Exercise>>()

    fun loadExercises() : ExercisesViewModel {
        viewModelScope.launch {
            repository.getExerciseList().observeOnce { onRepositoryUpdated(it) }
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