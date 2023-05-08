package com.zhy.adapter.recyclerview

import android.content.Context
import android.view.LayoutInflater
import com.zhy.adapter.recyclerview.base.ItemViewDelegate
import com.zhy.adapter.recyclerview.base.ViewHolder

/**
 * Created by zhy on 16/4/9.
 */
abstract class RecyclerViewAdapter<T>(context: Context, layoutId: Int, datas: List<T>) :
    MultiItemTypeAdapter<T>(context, datas) {
    protected lateinit var mContext: Context
    protected var mLayoutId: Int
    protected lateinit var mDatas: List<T>
    protected var mInflater: LayoutInflater
    protected abstract fun convert(holder: ViewHolder?, t: T, position: Int)

    init {
        mContext = context
        mInflater = LayoutInflater.from(context)
        mLayoutId = layoutId
        mDatas = datas
        addItemViewDelegate(object : ItemViewDelegate<T> {
            override fun isForViewType(item: T, position: Int): Boolean {
                return true
            }
            override val itemViewLayoutId: Int
                get() = layoutId

            override fun convert(holder: ViewHolder?, t: T, position: Int) {
                this@RecyclerViewAdapter.convert(holder, t, position)
            }
        })
    }
}