package com.example.studybuddy.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.data.FirebasePlanRepository
import com.example.studybuddy.notifications.NotificationHelper
import com.example.studybuddy.data.AppDatabase
import com.example.studybuddy.data.Plan
import kotlinx.coroutines.launch

class PlanViewModel(application: Application) : AndroidViewModel(application) {
    private val localPlanDao = AppDatabase.getDatabase(application).planDao()
    private val firebaseRepository = FirebasePlanRepository()
    private val notificationHelper = NotificationHelper(application)

    val plans: LiveData<List<Plan>> = firebaseRepository.getAllPlans().asLiveData()

    fun addPlan(plan: Plan) = viewModelScope.launch {
        try {
            val firebaseId = firebaseRepository.addPlan(plan)

            val planWithFirebaseId = plan.copy(firebaseId = firebaseId)
            localPlanDao.insert(planWithFirebaseId)

            notificationHelper.scheduleNotification(planWithFirebaseId)
        } catch (e: Exception) {
            localPlanDao.insert(plan)
            notificationHelper.scheduleNotification(plan)
        }
    }

    fun updatePlan(plan: Plan) = viewModelScope.launch {
        try {

            firebaseRepository.updatePlan(plan)

            localPlanDao.update(plan)

            notificationHelper.cancelNotification(plan.id)
            notificationHelper.scheduleNotification(plan)
        } catch (e: Exception) {
            localPlanDao.update(plan)
            notificationHelper.cancelNotification(plan.id)
            notificationHelper.scheduleNotification(plan)
        }
    }

    fun deletePlan(plan: Plan) = viewModelScope.launch {
        try {
            if (plan.firebaseId.isNotEmpty()) {
                firebaseRepository.deletePlan(plan.firebaseId)
            }

            localPlanDao.delete(plan)

            notificationHelper.cancelNotification(plan.id)
        } catch (e: Exception) {
            // Fallback на локална база
            localPlanDao.delete(plan)
            notificationHelper.cancelNotification(plan.id)
        }
    }
}