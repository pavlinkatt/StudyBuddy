package com.example.studybuddy.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val planId = intent.getIntExtra("planId", -1)
        val subject = intent.getStringExtra("subject") ?: "Study Time"
        val note = intent.getStringExtra("note") ?: ""

        if (planId != -1) {
            val notificationHelper = NotificationHelper(context)


            CoroutineScope(Dispatchers.IO).launch {
                val motivationalMessage = notificationHelper.getMotivationalQuote()
                val noteMessage = if (note.isNotEmpty()) {
                    "📚 $subject\n📝 $note"
                } else {
                    "📚 $subject"
                }

                notificationHelper.showNotification(
                    title = "$motivationalMessage\n\n",
                    message = noteMessage,
                    planId = planId
                )
            }
        }
    }
}