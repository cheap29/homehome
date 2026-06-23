package com.homehome.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reflection_sessions")
data class ReflectionSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAt: Long = System.currentTimeMillis(),
    val closedAt: Long? = null,
    val praiseText: String? = null
)
