package ua.afanasiev.android_coroutines_mvi.presentation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ua.afanasiev.android_coroutines_mvi.R
import ua.afanasiev.android_coroutines_mvi.databinding.ActivityMainBinding
import ua.afanasiev.android_coroutines_mvi.presentation.MainActivity.ViewState
import ua.afanasiev.android_coroutines_mvi.presentation.MainViewModel.Action
import ua.afanasiev.android_coroutines_mvi.presentation.MainViewModel.State

class MainActivity : AppCompatActivity() {

    private val viewModel = MainViewModel()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bindIntents()
        viewModel.collectState()
    }

    private fun MainViewModel.collectState() {
        stateFlow
            .map { state -> mapToViewState(state, applicationContext) }
            .flowOn(Dispatchers.Default)
            .onEach { state -> binding.render(state) }
            .launchIn(lifecycleScope)
    }

    private fun ActivityMainBinding.bindIntents() {
        changeSyncButton.setOnClickListener { viewModel.handleAction(Action.ChangeSync) }
        changeAsyncButton.setOnClickListener { viewModel.handleAction(Action.ChangeAsync) }
    }

    private fun ActivityMainBinding.render(state: ViewState) {
        counterValueTextView.text = state.text
    }

    data class ViewState(
        val text: String
    )
}

private fun mapToViewState(state: State, context: Context): ViewState {
    val text = context.getString(R.string.counter_text, state.counter)
    return ViewState(text = text)
}