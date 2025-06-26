package com.example.studybuddy

import com.example.studybuddy.data.Plan

public fun Plan.toFirebaseMap(): Map<String, Any> {
    return mapOf(
        "subject" to subject,
        "date" to date,
        "time" to time,
        "note" to note,
        "timestamp" to System.currentTimeMillis()
    )
}