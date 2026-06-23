package com.homehome.app

import android.app.Application
import com.homehome.app.data.db.AppDatabase
import com.homehome.app.data.repository.AppRepository

class HomehomeApplication : Application() {
    val repository: AppRepository by lazy {
        val db = AppDatabase.getInstance(this)
        AppRepository(
            taskDao = db.taskDao(),
            habitWordDao = db.habitWordDao(),
            sessionDao = db.reflectionSessionDao(),
            planDao = db.dailyPlanDao(),
            resultDao = db.reflectionResultDao()
        )
    }
}
