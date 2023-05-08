package com.stock.holdyourhands.ui.holding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stock.holdyourhands.db.HoldingChangeModel
import com.stock.holdyourhands.ui.base.MyApplication
import com.stock.holdyourhands.db.HoldingModel
import com.stock.holdyourhands.ui.base.BaseViewModel
import com.stock.holdyourhands.util.GSLog
import kotlinx.coroutines.*

class HoldingViewModel : BaseViewModel() {

    private val _holding = MutableLiveData<MutableList<HoldingModel>>().apply {
        value = mutableListOf()
    }
    val holding: LiveData<MutableList<HoldingModel>> = _holding

    private val _currentHolding = MutableLiveData<HoldingModel>().apply {
        value = HoldingModel()
    }
    val currentHolding: LiveData<HoldingModel> = _currentHolding

    fun requestHolding() {
        workerScope?.launch(coroutineExceptionHandler) {
            _holding.value?.clear()
            _holding.value?.addAll(MyApplication.db.holdingModelDao().getAll())
            _holding.postValue(_holding.value)
        }
    }

    fun requestLastHolding() {
        workerScope?.launch(coroutineExceptionHandler) {
            _currentHolding.postValue(MyApplication.db.holdingModelDao().getLastOne())
        }
    }

    fun addHolding(holdingModel: HoldingModel) {
        workerScope?.launch(coroutineExceptionHandler) {
            if (holdingModel.id == null && holdingModel.firstHoldingPrice == 0F) {
                holdingModel.firstHoldingPrice = holdingModel.holdingPrice
            }
            MyApplication.db.holdingModelDao().insertAll(holdingModel)
            requestLastHolding()
        }
    }

    fun addHoldingChanges(holdingChangeModel: HoldingChangeModel) {
        workerScope?.launch(coroutineExceptionHandler) {
            MyApplication.db.holdingChangesDao().insertAll(holdingChangeModel)
        }
    }

    override fun onLoadDataError(throwable: Throwable) {
        super.onLoadDataError(throwable)
        GSLog.e(throwable)
    }
}