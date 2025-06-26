package com.example.studybuddy.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.studybuddy.activities.MainActivity
import com.example.studybuddy.data.Plan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "study_reminders"
        const val CHANNEL_NAME = "Study Reminders"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for study reminders"
                enableVibration(true)
                setShowBadge(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(plan: Plan) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("planId", plan.id)
            putExtra("subject", plan.subject)
            putExtra("note", plan.note)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plan.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        try {
            val date = dateFormat.parse(plan.date)
            val time = timeFormat.parse(plan.time)

            if (date != null && time != null) {
                calendar.time = date
                val timeCalendar = Calendar.getInstance()
                timeCalendar.time = time

                calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                calendar.set(Calendar.SECOND, 0)

                if (calendar.timeInMillis > System.currentTimeMillis()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelNotification(planId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            planId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    suspend fun getMotivationalQuote(): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://zenquotes.io/api/random")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val response = connection.inputStream.bufferedReader().readText()
                val jsonArray = JSONArray(response)
                val jsonObject = jsonArray.getJSONObject(0)

                val quote = jsonObject.getString("q")
                val author = jsonObject.getString("a")

//                val jsonArray = JSONArray(response)
//                val jsonObject = jsonArray.getJSONObject(0)
//                val quote = jsonObject.getString("q")
//                val author = jsonObject.getString("a")


                "💡 \"$quote\" - $author"

            } catch (e: Exception) {
                val fallbackQuotes = listOf(
                    "💪 Секој ден е нова можност за учење!",
                    "🌟 Знаењето е сила - продолжи да учиш!",
                    "🚀 Твојата иднина зависи од она што правиш денес!",
                    "📚 Читањето е патувањето на умот!",
                    "🎯 Успехот е сума од мали напори повторувани ден за ден!",
                    "✨ Верувај во себе и во својата способност да учиш!"
                )
                fallbackQuotes.random()
            }
        }
    }

    fun showNotification(title: String, message: String, planId: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            planId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(planId, notification)
        }
    }
}