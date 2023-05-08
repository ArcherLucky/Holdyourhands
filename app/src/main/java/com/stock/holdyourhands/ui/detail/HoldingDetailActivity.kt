package com.stock.holdyourhands.ui.detail

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.Hold
import com.stock.holdyourhands.R
import com.stock.holdyourhands.databinding.ActivityHoldingDetailBinding
import com.stock.holdyourhands.db.HoldingChangeModel
import com.stock.holdyourhands.ui.base.ActivitySupport
import com.stock.holdyourhands.db.HoldingModel
import com.stock.holdyourhands.ui.holding.Dialogs
import com.stock.holdyourhands.util.*
import com.zhy.adapter.recyclerview.RecyclerViewAdapter
import okhttp3.OkHttpClient
import okhttp3.Request


const val KEY_HOLDING = "KEY_HOLDING"

class HoldingDetailActivity : ActivitySupport() {

    private lateinit var binding: ActivityHoldingDetailBinding

    private val holdingDetailViewModel by lazy {
        ViewModelProvider(this)[HoldingDetailViewModel::class.java]
    }

    private val holdingAdapter by lazy {
        object : RecyclerViewAdapter<HoldingChangeModel>(
            this,
            R.layout.item_holding, mutableListOf()
        ) {
            override fun convert(holder: com.zhy.adapter.recyclerview.base.ViewHolder?, t: HoldingChangeModel, position: Int) {
                holder?.let {
                    it.setText(R.id.tv_direction, t.getDirectionStr())
                    it.setTextColor(R.id.tv_direction, CommonUtil.getDirectionColor(t.direction))
                    it.setText(R.id.tv_name, holdingDetailViewModel.holding.value?.name)
                    it.setText(R.id.tv_code, holdingDetailViewModel.holding.value?.code)
                    it.setText(R.id.tv_price, getString(R.string.rmb_s, t.tradingPrice.toString()))
                    it.setText(R.id.tv_numbers, getString(R.string.stock_s, t.tradingNumber.toString()))
                    it.setText(R.id.tv_money, getString(R.string.rmb_s, XMathUtil.multiply(t.tradingPrice, t.tradingNumber)))
                    val percentOfBuild = XMathUtil.multiply(
                        XMathUtil.divide(t.tradingPrice, holdingDetailViewModel.holding.value?.firstHoldingPrice), 100).toFloat() - 100F
                    it.setTextColor(R.id.tv_percent_of_build, CommonUtil.getColor(percentOfBuild))
                    it.setText(R.id.tv_percent_of_build, ("$percentOfBuild%"))

                    it.setText(R.id.tv_current_result, getString(R.string.current_result,
                        (t.lastHoldingPrice - t.currentHoldingPrice).toString()))

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHoldingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "持仓详情"
        holdingDetailViewModel.holding.observe(this) {
            binding.tvTotal.setText(it.getCurrentValue().toString())
            binding.tvName.setText(it.name)
            binding.tvHolding.setText(it.holdingQuantity.toString())
            binding.tvHoldingEnable.setText(it.getHoldingEnableCounts().toString())
            binding.tvCost.setText(it.holdingPrice.toString())
            binding.tvMarketPrice.setText(it.marketPrice.toString())
            binding.tvResult.setText(it.getResult())
            binding.tvResultPercent.setText(it.getResultPercent())

            binding.tvTotal.setTextColor(CommonUtil.getColor(it))
            binding.tvName.setTextColor(CommonUtil.getColor(it))
            binding.tvHolding.setTextColor(CommonUtil.getColor(it))
            binding.tvHoldingEnable.setTextColor(CommonUtil.getColor(it))
            binding.tvCost.setTextColor(CommonUtil.getColor(it))
            binding.tvMarketPrice.setTextColor(CommonUtil.getColor(it))
            binding.tvResult.setTextColor(CommonUtil.getColor(it))
            binding.tvResultPercent.setTextColor(CommonUtil.getColor(it))

            binding.tvPlanedAmount.setText(getString(R.string.planed_amount_s, it.planedMoney.toString()))
            binding.tvPlanQuantity.setText(getString(R.string.planed_quantity_s, it.planedQuantity.toString()))
            binding.tvHoldingAmount.setText(getString(R.string.holding_amount_s, it.getCostValue().toString()))
            binding.tvHoldingQuantity.setText(getString(R.string.holding_quantity_s, (it.holdingQuantity.toString())))
            binding.tvSurplusMoney.setText(CommonUtil.getSomeSpannableString(
                getString(R.string.surplus_amount_s, (it.planedMoney?.minus(
                    it.getCostValue()
                )).toString()), null
            ))
            binding.tvSurplusQuantity.setText(CommonUtil.getSomeSpannableString(
                getString(R.string.surplus_quantity_s, (it.planedQuantity?.minus(
                    it.holdingQuantity
                )).toString()), null
            ))

            binding.tvPercentOfBuild.setText(CommonUtil.getSomeSpannableString(
                getString(R.string.percent_of_build,
                    (it.getPercentOfBuild().toString() + "%")), CommonUtil.getColor(it)
            ))

            val tempHolding = holdingDetailViewModel.holding.value
            GSLog.d("empHolding.marketPrice = " + tempHolding!!.marketPrice)
            tempHolding?.let {
                val string = getString(R.string.assert_lowest_price, tempHolding.firstHoldingPrice.toString(), tempHolding.assertLowestPrice.toString(),
                    (XMathUtil.multiply(XMathUtil.divide(tempHolding.marketPrice - tempHolding.assertLowestPrice, tempHolding.firstHoldingPrice).toFloat(), 100).toString() + "%"
                            ))
                binding.tvLowestPrice.setText(CommonUtil.getSomeSpannableString(string, null))
            }


            GSLog.d("it.marketPrice = " + it.marketPrice + ", it.firstHoldingPrice = " + it.firstHoldingPrice)

        }
        holdingDetailViewModel.holdingChanges.observe(this) {
            holdingAdapter.datas.clear()
            holdingAdapter.datas.addAll(it)
            holdingAdapter.notifyDataSetChanged()
        }

        holdingDetailViewModel.newChange.observe(this) {
            holdingAdapter.datas.add(it)
            holdingAdapter.notifyItemInserted(holdingAdapter.datas.size - 1)
        }

        holdingDetailViewModel.requestHolding(intent.getIntExtra(KEY_HOLDING, -1))
        holdingDetailViewModel.requestHoldingChanges(intent.getIntExtra(KEY_HOLDING, -1))

        binding.rvPosition.layoutManager = LinearLayoutManager(this)
        binding.rvPosition.adapter = holdingAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.holding_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.navigation_add_test -> {
                Dialogs.cupCost(this, holdingModel = holdingDetailViewModel.holding.value)
                true
            }
            R.id.navigation_add_change -> {
                holdingDetailViewModel.holding.value?.let {
                    Dialogs.addChangeDialog(object : Dialogs.OnResult<Pair<HoldingModel?, HoldingChangeModel?>> {
                        override fun onResult(result: Pair<HoldingModel?, HoldingChangeModel?>) {
                            if (result.first != null) {
                                holdingDetailViewModel.changeHolding(result.first!!)
                            }

                            if (result.second != null) {
                                holdingDetailViewModel.addHoldingChanges(result.second!!)
                            }


                        }

                    }, this, it)
                }
                true
            }

            R.id.navigation_update_price -> {

                holdingDetailViewModel.updateMarketPrice()
//                Dialogs.changePriceDialog(object : Dialogs.OnResult<HoldingModel?> {
//                    override fun onResult(result: HoldingModel?) {
//                        if (null == result) {
//                            showToast("修改市价失败")
//                            return
//                        }
//                        result.let {
//                            holdingDetailViewModel.changeHolding(result)
//                            showToast("修改市价成功")
//                        }
//                    }
//
//                }, this, holdingDetailViewModel.holding.value)
                true
            }

            R.id.navigation_update_holding -> {
                Dialogs.addHoldingDialog(
                    onResult = object : Dialogs.OnResult<HoldingModel?> {
                        override fun onResult(result: HoldingModel?) {
                            result?.let {
                                holdingDetailViewModel.changeHolding(result)
                            }
                        }
                    }, context = this, originHoldingModel = holdingDetailViewModel.holding.value)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}