package com.stock.holdyourhands.ui.holding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.stock.holdyourhands.R
import com.stock.holdyourhands.databinding.FragmentHoldingBinding
import com.stock.holdyourhands.db.HoldingChangeModel
import com.stock.holdyourhands.ui.detail.HoldingDetailActivity
import com.stock.holdyourhands.ui.detail.KEY_HOLDING
import com.stock.holdyourhands.db.HoldingModel
import com.stock.holdyourhands.ui.base.FragmentSupport
import com.stock.holdyourhands.ui.holding.direction.TradingDirection
import com.stock.holdyourhands.util.CommonUtil
import com.stock.holdyourhands.util.GSLog
import com.stock.holdyourhands.util.XMathUtil
import com.zhy.adapter.recyclerview.RecyclerViewAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

class HoldingFragment : FragmentSupport() {

    private var _binding: FragmentHoldingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val holdingAdapter by lazy {
        object : RecyclerViewAdapter<HoldingModel>(
            requireContext(),
            R.layout.item_position, mutableListOf()
        ) {
            override fun convert(holder: ViewHolder?, holdingModel: HoldingModel, position: Int) {
                holder?.let {
                    it.setTextColor(R.id.tv_total, CommonUtil.getColor(holdingModel))
                    it.setTextColor(R.id.tv_name, CommonUtil.getColor(holdingModel))
                    it.setTextColor(R.id.tv_holding, CommonUtil.getColor(holdingModel))
                    it.setTextColor(R.id.tv_holding_enable, CommonUtil.getColor(holdingModel))
                    it.setTextColor(R.id.tv_cost, CommonUtil.getColor(holdingModel))
                    it.setTextColor(R.id.tv_market_price, CommonUtil.getColor(holdingModel))
                    it.setTextColor(R.id.tv_result, CommonUtil.getColor(holdingModel))
                    it.setTextColor(R.id.tv_result_percent, CommonUtil.getColor(holdingModel))

                    it.setText(R.id.tv_total, holdingModel.getCurrentValue().toString())
                    it.setText(R.id.tv_name, holdingModel.name)
                    it.setText(R.id.tv_holding, holdingModel.holdingQuantity.toString())
                    it.setText(R.id.tv_holding_enable, holdingModel.getHoldingEnableCounts().toString())
                    it.setText(R.id.tv_cost, holdingModel.holdingPrice.toString())
                    it.setText(R.id.tv_market_price, holdingModel.marketPrice.toString())
                    it.setText(R.id.tv_result, holdingModel.getResult())
                    it.setText(R.id.tv_result_percent, holdingModel.getResultPercent())

                    it.setOnClickListener(R.id.content, object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            val intent = Intent(context, HoldingDetailActivity::class.java)
                            intent.putExtra(KEY_HOLDING, holdingModel.id)
                            startActivity(intent)
                        }

                    })

                }
            }


        }
    }

    private val holdingViewModel by lazy {
        ViewModelProvider(this)[HoldingViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentHoldingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.rvPosition.layoutManager = LinearLayoutManager(context)
        binding.rvPosition.adapter = holdingAdapter

        holdingViewModel.holding.observe(viewLifecycleOwner) {
            holdingAdapter.datas.clear()
            holdingAdapter.datas.addAll(it)
            holdingAdapter.notifyDataSetChanged()
        }
        holdingViewModel.currentHolding.observe(viewLifecycleOwner) {
            holdingAdapter.datas.add(it)
            holdingAdapter.notifyItemInserted(holdingAdapter.datas.size - 1)
            // 添加建仓操作
            if (null != it.id) {
                val holdingChangeModel = HoldingChangeModel()
                val result = kotlin.runCatching {
                    holdingChangeModel.lastHoldingPrice = it.holdingPrice
                    holdingChangeModel.tradingNumber = it.holdingQuantity
                    holdingChangeModel.tradingPrice = it.holdingPrice
                    holdingChangeModel.direction = TradingDirection.CREATE
                    holdingChangeModel.currentHoldingPrice =  it.holdingPrice
                    holdingChangeModel.currentNumber = it.holdingQuantity
                    holdingChangeModel.holdingId = it.id!!
                }.isSuccess
                if (result) {
                    holdingViewModel.addHoldingChanges(holdingChangeModel)
                }
            }
        }

        binding.flAddHolding.setOnClickListener {
            Dialogs.addHoldingDialog(object : Dialogs.OnResult<HoldingModel?> {
                override fun onResult(result: HoldingModel?) {
                    if (result == null) {
                        showToast("添加失败，所有选项为必填项")
//val holdingModel =
//HoldingModel(null, "000651", "格力电器TEST", 100F, 500000F,
//10000, 100F, 4700, 100.00F)
//holdingViewModel.addHolding(holdingModel)
                    } else {
                        holdingViewModel.addHolding(result)
                    }
                }

            }, requireActivity())
        }

        GSLog.d(XMathUtil.divide("100", "1000"))

        return root
    }

    override fun onStart() {
        super.onStart()
        holdingViewModel.requestHolding()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}