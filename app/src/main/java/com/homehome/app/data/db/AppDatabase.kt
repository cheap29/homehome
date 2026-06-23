package com.homehome.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.homehome.app.data.db.dao.*
import com.homehome.app.data.db.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        HabitWordEntity::class,
        ReflectionSessionEntity::class,
        DailyPlanItemEntity::class,
        ReflectionResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun habitWordDao(): HabitWordDao
    abstract fun reflectionSessionDao(): ReflectionSessionDao
    abstract fun dailyPlanDao(): DailyPlanDao
    abstract fun reflectionResultDao(): ReflectionResultDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "homehome.db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                seedInitialData(database)
                            }
                        }
                    }
                })
                .build()

        private suspend fun seedInitialData(db: AppDatabase) {
            val habitWords = listOf(
                "起きる", "外に出る", "薬を飲む", "仕事する",
                "お風呂に入る", "勉強する", "散歩する",
                "洗濯する", "ごはんを食べる", "寝る準備をする"
            )
            habitWords.forEach { title ->
                db.habitWordDao().insertHabitWord(HabitWordEntity(title = title))
            }
            db.reflectionSessionDao().insertSession(ReflectionSessionEntity())
        }
    }
}
