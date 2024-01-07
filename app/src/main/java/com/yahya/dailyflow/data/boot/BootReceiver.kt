package com.yahya.dailyflow.data.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yahya.dailyflow.alarm.AlarmScheduler
import com.yahya.dailyflow.util.Constants
import com.yahya.dailyflow.util.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var alarmScheduler: AlarmScheduler


    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (preferencesManager.getBoolean(Constants.IS_DAILY_REMINDER_ENABLED_KEY, false)) {
                val scheduledAlarmHour =
                    preferencesManager.getInt(Constants.DAILY_REMINDER_HOUR_KEY, 20)
                val scheduledAlarmMinute =
                    preferencesManager.getInt(Constants.DAILY_REMINDER_MINUTE_KEY, 0)

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, scheduledAlarmHour)
                    set(Calendar.MINUTE, scheduledAlarmMinute)
                    set(Calendar.SECOND, 0)
                }
                alarmScheduler.schedule(calendar = calendar)
            }
        }
    }
}