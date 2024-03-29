package com.yahya.dailyflow.data.repository

import com.yahya.dailyflow.model.Diary
import com.yahya.dailyflow.util.RequestState
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.ZonedDateTime

typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoRepository {
    fun getAllDiaries(): Flow<Diaries>
    fun getTextFilteredDiaries(searchText: String): Flow<Diaries>
    fun getDateFilteredDiaries(zonedDateTime: ZonedDateTime): Flow<Diaries>
    fun getSelectedDiary(diaryId: ObjectId): Flow<RequestState<Diary>>
    suspend fun insertDiary(diary: Diary): RequestState<Diary>
    suspend fun updateDiary(diary: Diary): RequestState<Diary>
    suspend fun deleteDiary(diaryId: ObjectId): RequestState<Boolean>
    suspend fun deleteAllDiaries(): RequestState<Boolean>
    suspend fun transferAllDiariesToGoogleAccount(anonymousId: String): RequestState<Boolean>

}