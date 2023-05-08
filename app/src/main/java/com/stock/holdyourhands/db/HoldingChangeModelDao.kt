package com.stock.holdyourhands.db

import androidx.room.*

@Dao
interface HoldingChangeModelDao {
	@Query("SELECT * FROM HoldingChangeModel WHERE holding_id is (:id) ORDER BY id DESC")
	fun getChanges(id: Int): List<HoldingChangeModel>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(vararg users: HoldingChangeModel)
}
