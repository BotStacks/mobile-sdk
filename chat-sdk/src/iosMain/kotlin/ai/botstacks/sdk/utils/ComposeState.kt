package ai.botstacks.sdk.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow

interface ComposeState<T> {
    @get:Composable
    val state: State<T>
}

@Suppress("unused")
class ComposeStateImplementation<T>(value: T) : ComposeState<T> {
    private val _state = MutableStateFlow(value)

    override val state: State<T>
        @Composable
        get() = _state.collectAsState()

    fun update(value: T) {
        _state.value = value
    }
}