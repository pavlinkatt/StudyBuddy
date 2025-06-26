package com.example.studybuddy.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plan: Plan)

    @Update
    suspend fun update(plan: Plan)

    @Delete
    suspend fun delete(plan: Plan)

    @Query("SELECT * FROM plans ORDER BY date, time")
    fun getAllPlans(): LiveData<List<Plan>>
}