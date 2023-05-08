package com.stock.holdyourhands.ui.holding

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import com.stock.holdyourhands.R
import com.stock.holdyourhands.databinding.DialogAddChangeBinding
import com.stock.holdyourhands.databinding.DialogAddHoldingBinding
import com.stock.holdyourhands.databinding.DialogChangeMarketPriceBinding
import com.stock.holdyourhands.databinding.DialogCupCostBinding
import com.stock.holdyourhands.db.HoldingChangeModel
import com.stock.holdyourhands.db.HoldingModel
import com.stock.holdyourhands.ui.holding.direction.TradingDirection
import com.stock.holdyourhands.util.CommonUtil
import com.stock.holdyourhands.util.XMathUtil

object Dialogs {

    interface OnResult<T> {
        fun onResult(result: T)
    }

    fun addHoldingDialog(onResult: OnResult<HoldingModel?>, context: Context, originHoldingModel: HoldingModel? = null) {
        val binding = DialogAddHoldingBinding.inflate(LayoutInflater.from(context))
        val dialog = DialogPlus.newDialog(context)
            .setContentHolder(ViewHolder(binding.root))
            .setGravity(Gravity.CENTER)
            .setCancelable(false)
            .setExpanded(false) // This will enable the expand feature, (similar to android L share dialog)
            .create()
        var holdingModel: HoldingModel? = null
        originHoldingModel?.let {
            binding.etCode.setText(it.code)
            binding.etName.setText(it.name)
            binding.etPlanedAmount.setText(it.planedMoney.toString())
            binding.etPlanedQuantity.setText(it.planedQuantity.toString())
            binding.etPrice.setText(it.marketPrice.toString())
            binding.etCost.setText(it.holdingPrice.toString())
            binding.etNumber.setText(it.holdingQuantity.toString())
        }
        binding.btnAdd.setOnClickListener {
            val result = kotlin.runCatching {
                // 代码，名称，计划投资金额，计划投资数量，市价
                val code = binding.etCode.text.toString()
                val name = binding.etName.text.toString()
                val planedMoney = binding.etPlanedAmount.text.toString().toFloat()
                val planedQuantity = binding.etPlanedQuantity.text.toString().toInt()
                val marketPrice = if (CommonUtil.isBlankString(binding.etPrice.text.toString())) 0F else binding.etPrice.text.toString().toFloat()

                // 现有持仓价格和股数
                val holdingPrice =  binding.etCost.text.toString().toFloat()
                val holdingQuantity = binding.etNumber.text.toString().toInt()

                holdingModel = if (originHoldingModel == null) {
                    HoldingModel(null, code = code, name = name, marketPrice = marketPrice,
                        planedMoney = planedMoney, planedQuantity = planedQuantity, holdingPrice = holdingPrice,
                        holdingQuantity = holdingQuantity, firstHoldingPrice = holdingPrice, assertLowestPrice = binding.etLowestPrice.text.toString().toFloat())
                } else {
                    HoldingModel(originHoldingModel.id, code = code, name = name, marketPrice = marketPrice,
                        planedMoney = planedMoney, planedQuantity = planedQuantity, holdingPrice = holdingPrice,
                        holdingQuantity = holdingQuantity, firstHoldingPrice = holdingPrice, assertLowestPrice = binding.etLowestPrice.text.toString().toFloat())
                }


            }.isSuccess
            if (result) {
                onResult.onResult(holdingModel)
            } else {
                onResult.onResult(null)
            }
            dialog.dismiss()

        }
        dialog.show()
    }

    fun addChangeDialog(onResult: OnResult<Pair<HoldingModel?, HoldingChangeModel?>>, context: Context, holdingModel: HoldingModel) {
        val binding = DialogAddChangeBinding.inflate(LayoutInflater.from(context))
        val dialog = DialogPlus.newDialog(context)
            .setContentHolder(ViewHolder(binding.root))
            .setGravity(Gravity.CENTER)
            .setExpanded(false) // This will enable the expand feature, (similar to android L share dialog)
            .create()
        binding.btnConfirm.setOnClickListener {
            val holdingChangeModel = HoldingChangeModel()
            val result = kotlin.runCatching {
                holdingChangeModel.lastHoldingPrice = holdingModel.holdingPrice
                holdingChangeModel.tradingNumber = binding.etAddNumber.text.toString().toInt()
                holdingChangeModel.tradingPrice = binding.etAddPrice.text.toString().toFloat()
                if (binding.radioBuy.isChecked) {
                    holdingChangeModel.direction = TradingDirection.BUY
                    holdingChangeModel.currentHoldingPrice = XMathUtil.divide((holdingModel.getCostValue() + holdingChangeModel.tradingNumber * holdingChangeModel.tradingPrice).toString(),
                        (holdingModel.holdingQuantity + holdingChangeModel.tradingNumber).toString()).toFloat()
                    holdingChangeModel.currentNumber = holdingModel.holdingQuantity + holdingChangeModel.tradingNumber
                } else if (binding.radioSell.isChecked) {
                    holdingChangeModel.direction = TradingDirection.SELL
                    holdingChangeModel.currentHoldingPrice = XMathUtil.divide((holdingModel.getCostValue() - holdingChangeModel.tradingNumber * holdingChangeModel.tradingPrice).toString(),
                        (holdingModel.holdingQuantity - holdingChangeModel.tradingNumber).toString()).toFloat()
                    holdingChangeModel.currentNumber = holdingModel.holdingQuantity - holdingChangeModel.tradingNumber
                }
                holdingChangeModel.holdingId = holdingModel.id!!
                holdingModel.holdingQuantity = holdingChangeModel.currentNumber
                holdingModel.holdingPrice = holdingChangeModel.currentHoldingPrice
            }.isSuccess
            if (result) {
                onResult.onResult(Pair(holdingModel, holdingChangeModel))
            } else {
                onResult.onResult(Pair(null, null))
            }
            dialog.dismiss()

        }
        dialog.show()
    }


    fun changePriceDialog(onResult: OnResult<HoldingModel?>, context: Context, holdingModel: HoldingModel?) {
        val binding = DialogChangeMarketPriceBinding.inflate(LayoutInflater.from(context))
        val dialog = DialogPlus.newDialog(context)
            .setContentHolder(ViewHolder(binding.root))
            .setGravity(Gravity.BOTTOM)
            .setExpanded(false) // This will enable the expand feature, (similar to android L share dialog)
            .create()
        binding.btnConfirm.setOnClickListener {
            val result = kotlin.runCatching {
                val code = binding.etPrice.text.toString()
                holdingModel?.marketPrice = code.toFloat()
            }.isSuccess
            if (result) {
                onResult.onResult(holdingModel)
            } else {
                onResult.onResult(null)
            }
            dialog.dismiss()

        }
        dialog.show()
    }

    fun cupCost(context: Context, holdingModel: HoldingModel?) {
        val dialogBinding = DialogCupCostBinding.inflate(LayoutInflater.from(context))
        val dialog = DialogPlus.newDialog(context)
            .setContentHolder(ViewHolder(dialogBinding.root))
            .setGravity(Gravity.CENTER)
            .setExpanded(true) // This will enable the expand feature, (similar to android L share dialog)
            .create()
        dialogBinding.etAddPrice.setText(holdingModel?.marketPrice?.toString() ?: "")
        dialogBinding.btnCpu.setOnClickListener {
            val addNumber = dialogBinding.etAddNumber.text.toString().toFloat()
            val addPrice = dialogBinding.etAddPrice.text.toString().toFloat()
            holdingModel?.let {
                val cost = XMathUtil.divide((it.getCostValue() + addNumber * addPrice).toString(), (it.holdingQuantity + addNumber).toString())
                dialogBinding.tvCost.setText(context.getString(R.string.cost_change, cost))
            }

        }
        dialog.show()
    }


}