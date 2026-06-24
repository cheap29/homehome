package com.homehome.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reflection_results")
data class ReflectionResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val titleSnapshot: String,
    val sourceType: SourceType,
    val sourceId: Long? = null,
    val isPlanned: Boolean,
    val isCompleted: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
