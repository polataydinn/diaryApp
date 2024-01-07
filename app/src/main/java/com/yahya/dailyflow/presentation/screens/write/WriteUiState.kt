package com.yahya.dailyflow.presentation.screens.write

import com.yahya.dailyflow.model.Diary
import io.realm.kotlin.types.RealmInstant

data class WriteUiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val updatedDateTime: RealmInstant? = null
)
