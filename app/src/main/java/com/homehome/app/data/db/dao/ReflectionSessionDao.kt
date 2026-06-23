package com.homehome.app.data.db.dao

import androidx.room.*
import com.homehome.app.data.db.entity.ReflectionSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReflectionSessionDao {
    @Query("SELECT * FROM reflection_sessions WHERE closedAt IS NULL ORDER BY startedAt DESC LIMIT 1")
    suspend fun getOpenSession(): ReflectionSessionEntity?

    @Query("SELECT * FROM reflection_sessions WHERE closedAt IS NULL ORDER BY startedAt DESC LIMIT 1")
    fun observeOpenSession(): Flow<ReflectionSessionEntity?>

    @Insert
    suspend fun insertSession(session: ReflectionSessionEntity): Long

    @Query("UPDATE reflection_sessions SET closedAt = :closedAt, praiseText = :praiseText WHERE id = :id")
    suspend fun closeSession(id: Long, closedAt: Long, praiseText: String)

    @Query("SELECT * FROM reflection_sessions WHERE closedAt IS NOT NULL ORDER BY closedAt DESC")
    fun observeClosedSessions(): Flow<List<ReflectionSessionEntity>>
}
