package ua.afanasiev.android_coroutines_mvi.library

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.completable(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    builder: CompletableBuilder.() -> Unit
): Job {
    val completable = CompletableImpl().apply(builder)
    checkNotNull(completable.source) { "Source must be specified" }
    return launch(context, start) {
        completable.onLaunch?.invoke()
        try {
            completable.source!!()
            completable.onComplete?.invoke()
        } catch (error: Throwable) {
            completable.onFailure?.invoke(error)
        }
    }
}

interface CompletableBuilder {
    fun source(supplier: suspend () -> Unit)
    fun onLaunch(action: () -> Unit)
    fun onComplete(consumer: () -> Unit)
    fun onFailure(consumer: (Throwable) -> Unit)
}

private class CompletableImpl : CompletableBuilder {

    var source: (suspend () -> Unit)? = null
    var onLaunch: (() -> Unit)? = null
    var onComplete: (() -> Unit)? = null
    var onFailure: ((Throwable) -> Unit)? = null

    override fun source(supplier: suspend () -> Unit) {
        source = supplier
    }

    override fun onLaunch(action: () -> Unit) {
        onLaunch = action
    }

    override fun onComplete(consumer: () -> Unit) {
        onComplete = consumer
    }

    override fun onFailure(consumer: (Throwable) -> Unit) {
        onFailure = consumer
    }
}