package com.yahya.dailyflow.presentation.screens.home


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.storage.FirebaseStorage
import com.yahya.dailyflow.R
import com.yahya.dailyflow.model.Diary
import com.yahya.dailyflow.presentation.components.AnonymousUserDiaryWarning
import com.yahya.dailyflow.presentation.components.DateHeader
import com.yahya.dailyflow.presentation.components.DiaryHolder
import com.yahya.dailyflow.presentation.components.EmptyPage
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor
import com.skydoves.balloon.compose.setTextColor
import kotlinx.coroutines.delay
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    diariesNotes: Map<LocalDate, List<Diary>>,
    firebaseStorage: FirebaseStorage,
    onClick: (String) -> Unit,
    navigateToWrite: () -> Unit,
    isSearchOpen: Boolean,
    isDateSelected: Boolean,
    onSearch: (String) -> Unit,
    onSearchReset: () -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf<String?>(null) }
    var debounced by rememberSaveable {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current

    val builder = rememberBalloonBuilder {
        setText("Now you can edit your profile!")
        setArrowSize(10)
        setWidthRatio(1.0f)
        setHeight(BalloonSizeSpec.WRAP)
        setArrowOrientation(ArrowOrientation.BOTTOM)
        setArrowPosition(0.5f)
        setPadding(12)
        setMarginHorizontal(12)
        setTextSize(15f)
        setCornerRadius(8f)
        setTextColor(Color.White)
        setBackgroundColor(Color.White)
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery != null) {
            if (searchQuery?.isEmpty() == true && !debounced) {
                debounced = true
                delay(300)
                onSearchReset()
            } else {
                debounced = false
            }
        }
    }

    Column(
        modifier = Modifier.padding(
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding()
        )
    ) {
        AnimatedVisibility(visible = isSearchOpen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = searchQuery?.isNotEmpty() == true) {
                    Box(contentAlignment = Alignment.Center) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                }
                OutlinedTextField(
                    value = searchQuery ?: "",
                    onValueChange = {
                        searchQuery = it
                    },
                    placeholder = { Text(text = stringResource(id = R.string.search)) },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (searchQuery.isNullOrEmpty()) {
                                onSearchReset()
                            } else {
                                onSearch(searchQuery!!)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search, contentDescription = null
                            )
                        }
                    },
                    shape = RoundedCornerShape(40),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        if (diariesNotes.isNotEmpty()) {
            LazyColumn(modifier = Modifier.padding(all = 24.dp)) {
                diariesNotes.forEach { (localDate, diaries) ->
                    stickyHeader(localDate) {
                        DateHeader(localDate = localDate)
                    }


                    items(diaries, key = { it._id.toString() }) { diary ->
                        DiaryHolder(
                            diary = diary,
                            onClick = { onClick(diary._id.toHexString()) },
                            firebaseStorage = firebaseStorage
                        )
                    }
                }
            }
        } else {
            EmptyPage(
                isFilteringByQuery = !searchQuery.isNullOrBlank(),
                isFilteringByDate = isDateSelected,
                onCreateButtonClicked = navigateToWrite
            )
        }
    }
}
