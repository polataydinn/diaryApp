package com.yahya.dailyflow.alarm

import java.util.Calendar

interface AlarmScheduler {
    fun schedule(calendar: Calendar)
    fun cancelAlarm()
}