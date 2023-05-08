package com.stock.holdyourhands.ui.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

open class ActivitySupport: AppCompatActivity() {
    protected fun showToast(string: String) {
        Toast.makeText(MyApplication.context, string, Toast.LENGTH_SHORT).show()
    }
}