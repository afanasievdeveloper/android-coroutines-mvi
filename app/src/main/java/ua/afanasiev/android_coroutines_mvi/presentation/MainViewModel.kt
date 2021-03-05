package ua.afanasiev.android_coroutines_mvi.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ua.afanasiev.android_coroutines_mvi.data.CounterRepository
import ua.afanasiev.android_coroutines_mvi.library.single

class MainViewModel(private val initialState: State) : ViewModel() {

    constructor() : this(initialState = State(counter = 0))

    private val repository = CounterRepository()
    private val state = MutableStateFlow(initialState)

    val stateFlow: Flow<State> get() = state

    fun handleAction(action: Action) {
        when (action) {
            Action.ChangeSync -> getRandomNumberSync()
            Action.ChangeAsync -> getRandomNumberAsync()
        }
    }

    private fun getRandomNumberSync() {
        val newValue = repository.getRandomValue()
        dispatch { state -> state.copy(counter = newValue) }
    }

    private fun getRandomNumberAsync() {
        viewModelScope.single<Int>(Dispatchers.Default) {
            source { repository.getRandomValueDelayed() }
            onSuccess { value -> dispatch { state -> state.copy(counter = value) } }
            onFailure { error -> Log.e("tag", error.message, error) }
        }
    }

    private fun dispatch(block: (State) -> State) {
        state.value = block(state.value)
    }

    data class State(
        val counter: Int
    )

    enum class Action { ChangeSync, ChangeAsync }
}