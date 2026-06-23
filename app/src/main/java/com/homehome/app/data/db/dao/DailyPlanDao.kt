package com.homehome.app.data.db.dao

import androidx.room.*
import com.homehome.app.data.db.entity.DailyPlanItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyPlanDao {
    @Query("SELECT * FROM daily_plan_items WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    fun observePlanItems(sessionId: Long): Flow<List<DailyPlanItemEntity>>

    @Query("SELECT * FROM daily_plan_items WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    suspend fun getPlanItems(sessionId: Long): List<DailyPlanItemEntity>

    @Insert
    suspend fun insertItems(items: List<DailyPlanItemEntity>)

    @Query("DELETE FROM daily_plan_items WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: Long)

    @Query("UPDATE daily_plan_items SET isChecked = :isChecked, updatedAt = :now WHERE id = :id")
    suspend fun updateChecked(id: Long, isChecked: Boolean, now: Long = System.currentTimeMillis())

    @Transaction
    suspend fun replacePlanItems(sessionId: Long, items: List<DailyPlanItemEntity>) {
        deleteBySessionId(sessionId)
        insertItems(items)
    }
}
