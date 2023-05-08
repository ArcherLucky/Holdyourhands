package com.stock.holdyourhands.ui.base

import android.widget.Toast
import androidx.fragment.app.Fragment

open class FragmentSupport: Fragment() {

    protected fun showToast(string: String) {
        Toast.makeText(MyApplication.context, string, Toast.LENGTH_SHORT).show()
    }
}