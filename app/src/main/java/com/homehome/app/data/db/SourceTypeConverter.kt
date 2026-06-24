package com.homehome.app.data.db

import androidx.room.TypeConverter
import com.homehome.app.data.db.entity.SourceType

class SourceTypeConverter {
    @TypeConverter
    fun fromSourceType(type: SourceType): String = type.name

    @TypeConverter
    fun toSourceType(value: String): SourceType = SourceType.valueOf(value)
}
