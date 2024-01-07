package com.yahya.dailyflow.presentation.screens.write

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.yahya.dailyflow.R
import com.yahya.dailyflow.model.Diary
import com.yahya.dailyflow.model.GalleryImage
import com.yahya.dailyflow.model.GalleryState
import com.yahya.dailyflow.presentation.components.EmptyPage
import com.yahya.dailyflow.presentation.components.ZoomableImage
import java.time.ZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WriteScreen(
    isLoading: Boolean,
    uiState: WriteUiState,
    galleryState: GalleryState,
    pagerState: PagerState,
    onBackPressed: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSavedClicked: (Diary) -> Unit,
    onDateTimeUpdated: (ZonedDateTime?) -> Unit,
    onNavigateToDraw: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageDeleteClicked: (GalleryImage) -> Unit,
) {

    var selectedImageIndex by remember { mutableIntStateOf(0) }
    val imagePagerState = rememberPagerState(
        pageCount = { galleryState.images.size }
    )

    var selectedGalleryImage by remember { mutableStateOf<GalleryImage?>(null) }



    Scaffold(topBar = {
        if (selectedGalleryImage == null) {
            WriteTopBar(
                selectedDiary = uiState.selectedDiary,
                onBackPressed = onBackPressed,
                onDeleteConfirmed = onDeleteConfirmed,
                onDateTimeUpdated = onDateTimeUpdated,
                onNavigateToDraw = onNavigateToDraw
            )
        } else {
            ImageTopBar(title = "${stringResource(id = R.string.image)} ${imagePagerState.currentPage + 1}",
                onBackClicked = { selectedGalleryImage = null },
                onDeleteClicked = {
                    val index = galleryState.images.indexOf(selectedGalleryImage!!)
                    onImageDeleteClicked(selectedGalleryImage!!)
                    selectedGalleryImage = if (galleryState.images.isNotEmpty()) {
                        if (index >= galleryState.images.size) {
                            galleryState.images[index - 1]
                        } else {
                            galleryState.images[index]
                        }
                    } else {
                        null
                    }
                })
        }
    }, content = { padding ->

        if (isLoading) {
            EmptyPage(
                showLoading = true
            )
        } else {
            AnimatedVisibility(
                visible = selectedGalleryImage == null, enter = fadeIn(), exit = fadeOut()
            ) {
                WriteContent(
                    uiState = uiState,
                    galleryState = galleryState,
                    paddingValues = padding,
                    pagerState = pagerState,
                    onTitleChanged = onTitleChanged,
                    onDescriptionChanged = onDescriptionChanged,
                    onSavedClicked = onSavedClicked,
                    onImageSelected = onImageSelected,
                    onImageClicked = { galleryImage, index ->
                        selectedGalleryImage = galleryImage
                        selectedImageIndex = index
                    },
                )
            }

            AnimatedVisibility(
                visible = selectedGalleryImage != null, enter = fadeIn(), exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ZoomableImage(pagerState = imagePagerState,
                        selectedGalleryImageId = selectedImageIndex,
                        galleryImages = galleryState.images.toList(),
                        onPageChange = {
                            selectedGalleryImage = galleryState.images[imagePagerState.currentPage]
                        })
                }
            }
        }
    })
}