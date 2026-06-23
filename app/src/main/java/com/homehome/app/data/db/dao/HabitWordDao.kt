package com.homehome.app.data.db.dao

import androidx.room.*
import com.homehome.app.data.db.entity.HabitWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitWordDao {
    @Query("SELECT * FROM habit_words ORDER BY createdAt ASC")
    fun observeHabitWords(): Flow<List<HabitWordEntity>>

    @Insert
    suspend fun insertHabitWord(word: HabitWordEntity): Long

    @Update
    suspend fun updateHabitWord(word: HabitWordEntity)

    @Delete
    suspend fun deleteHabitWord(word: HabitWordEntity)

    @Query("SELECT COUNT(*) FROM habit_words")
    suspend fun count(): Int
}
