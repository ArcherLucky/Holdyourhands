package com.stock.holdyourhands.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stock.holdyourhands.ui.holding.direction.TradingDirection
import kotlinx.parcelize.Parcelize


/**
 * 操作记录
 */
@Entity
@Parcelize
data class HoldingChangeModel(
    @ColumnInfo(name = "holding_id") var holdingId: Int = 0,
    // 交易方向
    @ColumnInfo(name = "trading_direction") var direction: TradingDirection = TradingDirection.CREATE,
    // 交易数量
    @ColumnInfo(name = "trading_number") var tradingNumber: Int = 0,
    // 交易价格
    @ColumnInfo(name = "trading_price") var tradingPrice: Float = 0.0F,
    // 持仓价格变动
    @ColumnInfo(name = "current_holding_price") var currentHoldingPrice: Float = 0.0F,
    // 持仓价格变动
    @ColumnInfo(name = "current_number") var currentNumber: Int = 0,
    // 操作前的持仓价
    @ColumnInfo(name = "last_holding_price") var lastHoldingPrice: Float = 0.0F,

    @PrimaryKey(autoGenerate = true) var id: Long = 0,

    ) : Parcelable {
        fun getDirectionStr(): String {
            if (direction == TradingDirection.BUY) {
                return "买入"
            }
            if (direction == TradingDirection.SELL) {
                return "卖出"
            }
            if (direction == TradingDirection.CREATE) {
                return "建仓"
            }
            return ""
        }
    }