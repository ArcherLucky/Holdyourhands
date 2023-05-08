package com.stock.holdyourhands.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface HoldingModelDao {

    @Query("SELECT * FROM HoldingModel")
    fun getAll():List<HoldingModel>

    @Query("SELECT * FROM HoldingModel WHERE id is (:id)")
    fun findById(id: Int): HoldingModel

    @Query("SELECT * FROM HoldingModel ORDER BY id DESC LIMIT 1")
    fun getLastOne(): HoldingModel

    @Query("SELECT * FROM HoldingModel WHERE name is (:name)")
    fun findByName(name: String): HoldingModel

    @Insert(onConflict = REPLACE)
    fun insertAll(vararg users: HoldingModel)
    @Delete
    fun delete(user: HoldingModel)

}