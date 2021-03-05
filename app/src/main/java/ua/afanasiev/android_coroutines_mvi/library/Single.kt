package ua.afanasiev.android_coroutines_mvi.library

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> CoroutineScope.single(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    builder: SingleBuilder<T>.() -> Unit
): Job {
    val single = SingleImpl<T>().apply(builder)
    checkNotNull(single.source) { "Source must be specified" }
    return launch(context, start) {
        single.onLaunch?.invoke()
        try {
            val result = single.source!!()
            single.onSuccess?.invoke(result)
        } catch (error: Throwable) {
            single.onFailure?.invoke(error)
        }
    }
}

interface SingleBuilder<T> {
    fun source(supplier: suspend () -> T)
    fun onLaunch(action: () -> Unit)
    fun onSuccess(consumer: (T) -> Unit)
    fun onFailure(consumer: (Throwable) -> Unit)
}

private class SingleImpl<T> : SingleBuilder<T> {

    var source: (suspend () -> T)? = null
    var onLaunch: (() -> Unit)? = null
    var onSuccess: ((T) -> Unit)? = null
    var onFailure: ((Throwable) -> Unit)? = null

    override fun source(supplier: suspend () -> T) {
        source = supplier
    }

    override fun onLaunch(action: () -> Unit) {
        onLaunch = action
    }

    override fun onSuccess(consumer: (T) -> Unit) {
        onSuccess = consumer
    }

    override fun onFailure(consumer: (Throwable) -> Unit) {
        onFailure = consumer
    }
}