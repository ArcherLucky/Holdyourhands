package com.stock.holdyourhands.util

import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorRes
import com.stock.holdyourhands.R
import com.stock.holdyourhands.ui.base.MyApplication
import com.stock.holdyourhands.db.HoldingModel
import com.stock.holdyourhands.ui.holding.direction.TradingDirection


const val KEY_1 = "："
const val KEY_2 = "还有"
object CommonUtil {

    fun getColor(model: HoldingModel): Int {
        return if (!model.isSuccessHolding()) {
            MyApplication.context.resources.getColor(R.color.teal_700)
        } else {
            MyApplication.context.resources.getColor(R.color.red_ff4a57)
        }
    }

    fun getColor(float: Float): Int {
        return if (float <= 0) {
            MyApplication.context.resources.getColor(R.color.teal_700)
        } else {
            MyApplication.context.resources.getColor(R.color.red_ff4a57)
        }
    }

    fun getDirectionColor(direction: TradingDirection): Int {
        return if (direction == TradingDirection.SELL) {
            MyApplication.context.resources.getColor(R.color.teal_700)
        } else {
            MyApplication.context.resources.getColor(R.color.red_ff4a57)
        }
    }

    fun isBlankString(string: String): Boolean {
        return null == string || TextUtils.isEmpty(string) || TextUtils.isEmpty(string.trim()) || "null".equals(
            string
        )
    }

//    fun getSomeSpannableString(string: String, @ColorRes colorRes: Int?, vararg keyStr: String): SpannableString {
//        val spanString = SpannableString(string);
//
//        keyStr.forEach {
//            val span = ForegroundColorSpan(
//                colorRes ?: MyApplication.context.resources.getColor(R.color.red_ff4a57)
//            );
//            spanString.setSpan(span, spanString.indexOf(it) + 1, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        return spanString
//    }

    fun getSomeSpannableString(string: String, @ColorRes colorRes: Int?): SpannableString {
        val spanString = SpannableString(string);
        val span = ForegroundColorSpan(
            colorRes ?: MyApplication.context.resources.getColor(R.color.red_ff4a57)
        );
        spanString.setSpan(span, spanString.indexOf("：") + 1, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString
    }
}