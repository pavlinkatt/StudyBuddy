package com.example.studybuddy.data

import com.example.studybuddy.toFirebaseMap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebasePlanRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val plansCollection = firestore.collection("plans")


    suspend fun addPlan(plan: Plan): String {
        val docRef = plansCollection.add(plan.toFirebaseMap()).await()
        return docRef.id
    }

    suspend fun updatePlan(plan: Plan) {
        if (plan.firebaseId.isNotEmpty()) {
            plansCollection.document(plan.firebaseId)
                .set(plan.toFirebaseMap())
                .await()
        }
    }


    suspend fun deletePlan(firebaseId: String) {
        plansCollection.document(firebaseId).delete().await()
    }


    fun getAllPlans(): Flow<List<Plan>> = callbackFlow {
        val listener = plansCollection
            .orderBy("date", Query.Direction.ASCENDING)
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val plans = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Plan::class.java)?.copy(firebaseId = doc.id)
                } ?: emptyList()

                trySend(plans)
            }

        awaitClose { listener.remove() }
    }
}