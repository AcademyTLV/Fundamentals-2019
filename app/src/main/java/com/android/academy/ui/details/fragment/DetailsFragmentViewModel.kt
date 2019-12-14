package com.android.academy.ui.details.fragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.academy.model.MovieModel
import com.android.academy.repos.TrailersRepository
import com.android.academy.ui.list.TAG


enum class State { LOADING, LOADED, ERROR }

class DetailsFragmentViewModel(application: Application) : AndroidViewModel(application) {

    init {
        Log.d(TAG, "DetailsFragmentViewModel instance created: $this")
    }

    private val trailersRepository = TrailersRepository(application)

    // State
    private val state: MutableLiveData<State> by lazy { MutableLiveData<State>() }

    fun getState(): LiveData<State> = state

    // Open trailer
    private val openTrailer: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun getOpenTrailer(): LiveData<String> = openTrailer

    // Get Trailer
    fun getTrailer(movieModel: MovieModel) {
        state.postValue(State.LOADING)
        observeTrailer()
        trailersRepository.fetchTrailer(movieModel)
    }

    private fun observeTrailer() {
        if (trailersRepository.getTrailer().hasObservers()) return

        trailersRepository.getTrailer().observeForever {
            if (it == null) {
                state.postValue(State.ERROR)
            } else {
                state.postValue(State.LOADED)
                openTrailer.postValue(it.key)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "DetailsFragmentViewModel onCleared called for: $this")
    }
}