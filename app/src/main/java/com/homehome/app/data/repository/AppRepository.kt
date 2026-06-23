package com.homehome.app.data.repository

import com.homehome.app.data.db.dao.*
import com.homehome.app.data.db.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

data class HomeState(
    val session: ReflectionSessionEntity?,
    val planItems: List<DailyPlanItemEntity>
)

data class SelectableItem(
    val id: Long,
    val title: String,
    val sourceType: String, // TASK or HABIT
    val sourceId: Long
)

class AppRepository(
    private val taskDao: TaskDao,
    private val habitWordDao: HabitWordDao,
    private val sessionDao: ReflectionSessionDao,
    private val planDao: DailyPlanDao,
    private val resultDao: ReflectionResultDao
) {
    // ── ホーム ──────────────────────────────────────────
    fun observeHomeState(): Flow<HomeState> =
        sessionDao.observeOpenSession().flatMapLatest { session ->
            if (session == null) {
                flowOf(HomeState(null, emptyList()))
            } else {
                planDao.observePlanItems(session.id).combine(flowOf(session)) { items, s ->
                    HomeState(s, items)
                }
            }
        }

    // ── タスク ──────────────────────────────────────────
    fun observeActiveTasks(): Flow<List<TaskEntity>> = taskDao.observeActiveTasks()

    fun observeAllTasks(): Flow<List<TaskEntity>> = taskDao.observeAllTasks()

    suspend fun addTask(title: String, memo: String? = null) {
        taskDao.insertTask(TaskEntity(title = title, memo = memo))
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun markTaskCompleted(id: Long) {
        taskDao.markTaskCompleted(id)
    }

    // ── 単語帳 ──────────────────────────────────────────
    fun observeHabitWords(): Flow<List<HabitWordEntity>> = habitWordDao.observeHabitWords()

    suspend fun addHabitWord(title: String) {
        habitWordDao.insertHabitWord(HabitWordEntity(title = title))
    }

    suspend fun updateHabitWord(word: HabitWordEntity) {
        habitWordDao.updateHabitWord(word.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteHabitWord(word: HabitWordEntity) {
        habitWordDao.deleteHabitWord(word)
    }

    // ── 今日の3つ ────────────────────────────────────────
    fun observePlanItems(sessionId: Long): Flow<List<DailyPlanItemEntity>> =
        planDao.observePlanItems(sessionId)

    suspend fun savePlanItems(sessionId: Long, items: List<DailyPlanItemEntity>) {
        planDao.replacePlanItems(sessionId, items)
    }

    suspend fun togglePlanItemChecked(itemId: Long, checked: Boolean) {
        planDao.updateChecked(itemId, checked)
    }

    // 単語帳+BOX統合候補リスト（フカツさん案：統合リストでタグで出所を示す）
    fun observeSelectableItems(): Flow<List<SelectableItem>> =
        habitWordDao.observeHabitWords().combine(taskDao.observeActiveTasks()) { habits, tasks ->
            val habitItems = habits.map {
                SelectableItem(it.id, it.title, "HABIT", it.id)
            }
            val taskItems = tasks.map {
                SelectableItem(it.id, it.title, "TASK", it.id)
            }
            habitItems + taskItems
        }

    // ── セッション ──────────────────────────────────────
    suspend fun getOrCreateOpenSession(): ReflectionSessionEntity {
        return sessionDao.getOpenSession() ?: run {
            val id = sessionDao.insertSession(ReflectionSessionEntity())
            sessionDao.getOpenSession()!!
        }
    }

    fun observeOpenSession(): Flow<ReflectionSessionEntity?> = sessionDao.observeOpenSession()

    suspend fun createNewSession(): Long =
        sessionDao.insertSession(ReflectionSessionEntity())

    // ── 振り返り ────────────────────────────────────────
    suspend fun addBonusResult(sessionId: Long, title: String, sourceType: String, sourceId: Long?) {
        val result = ReflectionResultEntity(
            sessionId = sessionId,
            titleSnapshot = title,
            sourceType = sourceType,
            sourceId = sourceId,
            isPlanned = false,
            isCompleted = true
        )
        resultDao.insertResults(listOf(result))
        if (sourceType == "TASK" && sourceId != null) {
            taskDao.markTaskCompleted(sourceId)
        }
    }

    suspend fun completeReflection(
        sessionId: Long,
        checkedPlanItems: List<DailyPlanItemEntity>,
        allPlanItems: List<DailyPlanItemEntity>
    ): String {
        val results = allPlanItems.map { item ->
            ReflectionResultEntity(
                sessionId = sessionId,
                titleSnapshot = item.titleSnapshot,
                sourceType = "PLAN",
                sourceId = item.sourceId,
                isPlanned = true,
                isCompleted = item.isChecked
            )
        }
        resultDao.insertResults(results)

        val bonusResults = resultDao.getResultsBySessionId(sessionId)
            .filter { !it.isPlanned }
        val checkedCount = checkedPlanItems.size
        val bonusCount = bonusResults.size
        val praiseText = buildPraiseText(checkedCount, allPlanItems.size, bonusCount)

        sessionDao.closeSession(sessionId, System.currentTimeMillis(), praiseText)

        // 未達成の目標はそのまま（やることBOXには戻さない、持ち越し扱い）
        return praiseText
    }

    fun observeHistory(): Flow<List<ReflectionSessionEntity>> = sessionDao.observeClosedSessions()

    fun observeHistoryDetail(sessionId: Long): Flow<List<ReflectionResultEntity>> =
        resultDao.observeResultsBySessionId(sessionId)

    suspend fun getSessionById(sessionId: Long): ReflectionSessionEntity? =
        sessionDao.getOpenSession() // ヒストリー詳細は別クエリで対応

    // ── ほめコメント生成 ──────────────────────────────────
    fun buildPraiseText(checkedCount: Int, totalPlan: Int, bonusCount: Int): String = when {
        checkedCount >= totalPlan && totalPlan > 0 && bonusCount > 0 ->
            "目標以上やるなんて天才！"
        checkedCount >= totalPlan && totalPlan > 0 ->
            "3つ達成、めちゃくちゃえらい！"
        checkedCount in 1 until totalPlan ->
            "ちゃんとできたことがある。それが今日の勝ち"
        checkedCount == 0 && bonusCount > 0 ->
            "予定外でもできたことがある。ちゃんと前に進んでる"
        else ->
            "振り返りに来られた。それだけでもえらい"
    }
}
