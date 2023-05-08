package com.stock.holdyourhands.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stock.holdyourhands.util.XMathUtil
import kotlinx.parcelize.Parcelize


/**
 * 持仓信息
 */
@Entity
@Parcelize
class HoldingModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    // 持仓代码
    @ColumnInfo(name = "code") var code: String = "",
    // 持仓名称
    @ColumnInfo(name = "name") var name: String = "",
    // 市价
    @ColumnInfo(name = "market_price") var marketPrice: Float = 0.0F,
    // 计划投资金额
    @ColumnInfo(name = "planned_money") var planedMoney: Float = 0.0F,
    // 计划投资数量
    @ColumnInfo(name = "planned_quantity") var planedQuantity: Int = 0,

    // 成本
    @ColumnInfo(name = "holding_price") var holdingPrice: Float = 0.0F,
    // 持仓数量
    @ColumnInfo(name = "holding_quantity") var holdingQuantity: Int = 0,
    // 建仓价位
    @ColumnInfo(name = "first_holding_price") var firstHoldingPrice: Float = 0.0F,
    // 断言最低价
    @ColumnInfo(name = "assert_lowest_price") var assertLowestPrice: Float = 1F,

    ) : Parcelable {


    fun getHoldingEnableCounts(): Int {
        return holdingQuantity
    }

    /**
     * 获取盈亏
     */
    fun getResult(): String {
        if (holdingQuantity == 0) {
            return "0"
        }
        val result = XMathUtil.multiply(marketPrice  - holdingPrice, holdingQuantity)
        return result

    }

    /**
     * 获取盈亏百分比
     */
    fun getResultPercent(): String {
        val result = XMathUtil.multiply(getResult().toFloat().div(XMathUtil.multiply(holdingPrice, holdingQuantity).toFloat()), 100)
        return if (isSuccessHolding()) "$result%" else "-$result%"

    }

    /**
     * 是否盈利
     */
    fun isSuccessHolding(): Boolean {
        return holdingPrice <= marketPrice
    }

    /**
     * 获取当前价值
     */
    fun getCurrentValue(): Float {
        return (marketPrice * holdingQuantity)
    }

    /**
     * 获取成本价值
     */
    fun getCostValue(): Float {
        return (holdingPrice * holdingQuantity)
    }

    fun getPercentOfBuild(): Float {
        return (XMathUtil.multiply(XMathUtil.divide(marketPrice, firstHoldingPrice), 100).toFloat() - 100F)
    }


}