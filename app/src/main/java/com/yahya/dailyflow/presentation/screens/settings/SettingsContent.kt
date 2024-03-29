package com.yahya.dailyflow.presentation.screens.settings

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.yahya.dailyflow.R
import com.yahya.dailyflow.presentation.components.DailyReminderAlarmCard
import com.yahya.dailyflow.presentation.components.SettingsCardItem
import com.yahya.dailyflow.presentation.components.SignInButton
import com.yahya.dailyflow.presentation.components.TimePickerDialog
import com.yahya.dailyflow.util.Constants
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    onSwitchToGoogleClicked: () -> Unit,
    onSignOutClicked: () -> Unit,
    onClearDiaryClicked: () -> Unit,
    onDeleteAccountClicked: () -> Unit,
    onAlarmCanceled: () -> Unit,
    onAlarmScheduled: (Calendar) -> Unit,
    onUpdateReminderStatusPrefs: (Boolean) -> Unit,
    onUpdateReminderTimePrefs: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    isDailyReminderEnabled: Boolean,
    dailyReminderTime: LocalTime,
    isAnonymous: Boolean,
    isGoogleLoading: Boolean
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val calendar = remember(dailyReminderTime) {
        Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.SECOND, 0)
        }
    }


    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            mutableStateOf(true)
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermission = isGranted
            if (isGranted) {
                onUpdateReminderStatusPrefs(true)
            }
        }

    val formattedTime = remember(dailyReminderTime) {
        val localDateTime = LocalTime.of(dailyReminderTime.hour, dailyReminderTime.minute)
        DateTimeFormatter.ofPattern(Constants.TIME_PATTERN).format(localDateTime).uppercase()
    }

    Column(modifier = modifier) {

        Text(
            text = stringResource(id = R.string.reminder_settings),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        DailyReminderAlarmCard(alarmTime = formattedTime,
            onDailyReminderSwitchChange = { isEnabled ->
                if (isEnabled) {
                    if (hasNotificationPermission) {
                        onUpdateReminderStatusPrefs(true)
                        onUpdateReminderTimePrefs(dailyReminderTime)
                        onAlarmScheduled(calendar.apply {
                            set(Calendar.HOUR_OF_DAY, dailyReminderTime.hour)
                            set(Calendar.MINUTE, dailyReminderTime.minute)
                        })
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                } else {
                    onUpdateReminderStatusPrefs(false)
                    onAlarmCanceled()
                }
            },
            isDailyReminderEnabled = isDailyReminderEnabled,
            onClick = { if (isDailyReminderEnabled) showTimePickerDialog = true })

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.account_settings),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )


        Spacer(modifier = Modifier.height(8.dp))

        SettingsCardItem(
            optionText = stringResource(id = R.string.google_sign_out),
            optionIcon = Icons.Outlined.ExitToApp,
            onClick = onSignOutClicked
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsCardItem(
            optionText = stringResource(id = R.string.clear_diary),
            optionIcon = Icons.Outlined.DeleteOutline,
            onClick = onClearDiaryClicked
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsCardItem(
            optionText = stringResource(id = R.string.delete_account),
            optionIcon = Icons.Outlined.Close,
            onClick = onDeleteAccountClicked
        )

        AnimatedVisibility(visible = isAnonymous) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                SignInButton(
                    primaryText = stringResource(id = R.string.switch_to_google),
                    iconRes = R.drawable.google_logo,
                    isLoading = isGoogleLoading,
                    onClick = onSwitchToGoogleClicked
                )
            }
        }
    }


    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = dailyReminderTime.hour, initialMinute = dailyReminderTime.minute
        )
        TimePickerDialog(title = stringResource(id = R.string.set_reminder_time),
            onCancel = { showTimePickerDialog = false },
            onConfirm = {
                val updatedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                onUpdateReminderTimePrefs(updatedTime)
                onAlarmScheduled(calendar.apply {
                    set(Calendar.HOUR_OF_DAY, updatedTime.hour)
                    set(Calendar.MINUTE, updatedTime.minute)
                })
                showTimePickerDialog = false
            }) {
            TimePicker(
                state = timePickerState,
                layoutType = TimePickerLayoutType.Vertical,
                colors = TimePickerDefaults.colors(
                    clockDialColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                    clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    clockDialUnselectedContentColor = MaterialTheme.colorScheme.outline,
                    selectorColor = MaterialTheme.colorScheme.primary,
                    periodSelectorBorderColor = MaterialTheme.colorScheme.inverseOnSurface,
                    periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        1.dp
                    ),
                    periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        1.dp
                    ),
                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}