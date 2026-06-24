package com.homehome.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val praise: Int = 0,
    val praiseMedium: Int = 0,
    val praiseLarge: Int = 0,
    val praiseSuper: Int = 0
)
