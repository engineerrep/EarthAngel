package com.earth.libbase.base

import kotlinx.coroutines.*


suspend fun <T> workOnMain(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.Main) { block() }
}

suspend fun <T> workOnIO(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.IO) { block() }
}

data class CoroutineCallback(
    var initDispatcher: CoroutineDispatcher? = null,
    var block: suspend () -> Unit = {},
    var onError: (Throwable) -> Unit = {}
)

fun CoroutineScope.safeLaunch(init: CoroutineCallback.() -> Unit): Job {
    val callback = CoroutineCallback().apply { this.init() }
    return launch(CoroutineExceptionHandler { _, throwable ->
        callback.onError(throwable)
    } + (callback.initDispatcher ?: GlobalScope.coroutineContext)) {
        callback.block()
    }
}

fun CoroutineScope.delayLaunch(timeMills: Long, init: CoroutineCallback.() -> Unit): Job {
    check(timeMills >= 0) { "timeMills must be positive" }
    val callback = CoroutineCallback().apply(init)
    return launch(CoroutineExceptionHandler { _, throwable ->
        callback.onError(throwable)
    } + (callback.initDispatcher ?: GlobalScope.coroutineContext)) {
        delay(timeMills)
        callback.block()
    }
}

@Suppress("FunctionName")
fun IOScope(): CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())