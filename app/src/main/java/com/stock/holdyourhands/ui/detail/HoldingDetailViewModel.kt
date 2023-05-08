package com.stock.holdyourhands.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.stock.holdyourhands.db.HoldingChangeModel
import com.stock.holdyourhands.ui.base.MyApplication
import com.stock.holdyourhands.db.HoldingModel
import com.stock.holdyourhands.ui.base.BaseViewModel
import com.stock.holdyourhands.util.GSLog
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

class HoldingDetailViewModel : BaseViewModel() {

    private val _holding = MutableLiveData<HoldingModel>().apply {
        value = HoldingModel(null)
    }
    val holding: LiveData<HoldingModel> = _holding

    private val _holdingChanges = MutableLiveData<MutableList<HoldingChangeModel
            >>().apply {
        value = mutableListOf()
    }
    val holdingChanges: LiveData<MutableList<HoldingChangeModel>> = _holdingChanges

    private val _newChange = MutableLiveData<HoldingChangeModel>().apply {
        value = HoldingChangeModel()
    }
    val newChange: LiveData<HoldingChangeModel> = _newChange

    fun requestHolding(id: Int) {
        workerScope?.launch(coroutineExceptionHandler) {
            _holding.postValue(MyApplication.db.holdingModelDao().findById(id))
        }
    }


    fun changeHolding(holdingModel: HoldingModel) {
        workerScope?.launch(coroutineExceptionHandler) {
            MyApplication.db.holdingModelDao().insertAll(holdingModel)
            holdingModel.id?.let {
                requestHolding(it)
            }
        }
    }

    fun addHoldingChanges(holdingChangeModel: HoldingChangeModel) {
        workerScope?.launch(coroutineExceptionHandler) {
            MyApplication.db.holdingChangesDao().insertAll(holdingChangeModel)
            _newChange.postValue(holdingChangeModel)
        }
    }

    fun requestHoldingChanges(id: Int) {
        workerScope?.launch(coroutineExceptionHandler) {
            _holdingChanges.value?.addAll(MyApplication.db.holdingChangesDao().getChanges(id))
            _holdingChanges.postValue(_holdingChanges.value)
        }
    }

    override fun onLoadDataError(throwable: Throwable) {
        super.onLoadDataError(throwable)
        GSLog.e(throwable)
    }

    fun updateMarketPrice() {

        workerScope?.launch(coroutineExceptionHandler) {

            kotlin.runCatching {
                //第二步创建OkHttpClient对象
                val client = OkHttpClient()
                val code = _holding.value?.code
                val url = if (code?.startsWith("60") == true) {
                    "http://img1.money.126.net/data/hs/time/today/0" + _holding.value?.code + ".json"
                } else {
                    "http://img1.money.126.net/data/hs/time/today/1" + _holding.value?.code + ".json"
                }
                GSLog.i("url = " + url)
                //第二步创建request对象
                val request = Request.Builder()
                    .url(url)
                    .build()
                //第三步调用OkHttpClient的newCall() 方法来创建一个Call 对象，并调用它的execute() 方法来发送请求并获取服务器返回的数据
                //其中Response 对象就是服务器返回的数据
                val response = client.newCall(request).execute()
                val responseData = JSON.parseObject(response.body?.string())
                val dataArray = responseData.getJSONArray("data")
                GSLog.i("dataArray = " + dataArray.size)
                _holding.value?.let {
                    it.marketPrice = dataArray.getJSONArray(dataArray.size - 1)[1].toString().toFloat()
                    changeHolding(it)
                }

            }.getOrThrow()
        }

    }
}