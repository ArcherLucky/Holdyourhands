package com.stock.holdyourhands.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

open class BaseViewModel: ViewModel() {

    protected var workerScope: CoroutineScope? = null

    protected val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        onLoadDataError(throwable)
    }

    protected open fun onLoadDataError(throwable: Throwable) {

    }

    init {
        workerScope = CoroutineScope(Dispatchers.IO)
    }

    fun cancelJobs() {
        workerScope?.cancel()
        workerScope = null
    }
}