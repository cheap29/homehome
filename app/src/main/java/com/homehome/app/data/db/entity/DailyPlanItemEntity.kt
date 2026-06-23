package com.homehome.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_plan_items")
data class DailyPlanItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val sourceType: String, // TASK, HABIT, FREE
    val sourceId: Long? = null,
    val titleSnapshot: String,
    val orderIndex: Int,
    val isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
