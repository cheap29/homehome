package com.homehome.app.data.db.dao

import androidx.room.*
import com.homehome.app.data.db.entity.ReflectionResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReflectionResultDao {
    @Insert
    suspend fun insertResults(results: List<ReflectionResultEntity>)

    @Query("SELECT * FROM reflection_results WHERE sessionId = :sessionId ORDER BY createdAt ASC")
    fun observeResultsBySessionId(sessionId: Long): Flow<List<ReflectionResultEntity>>

    @Query("SELECT * FROM reflection_results WHERE sessionId = :sessionId ORDER BY createdAt ASC")
    suspend fun getResultsBySessionId(sessionId: Long): List<ReflectionResultEntity>
}
