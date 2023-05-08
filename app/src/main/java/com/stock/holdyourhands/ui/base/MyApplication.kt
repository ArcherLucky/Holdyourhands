package com.stock.holdyourhands.ui.base

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.stock.holdyourhands.db.AppDatabase

class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        context = this
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "holding_db"
        ).build()
    }


    companion object {

        lateinit var context: Context

        lateinit var db: AppDatabase
    }
}