package com.yahya.dailyflow.model

import android.net.Uri

data class GalleryImage(
    val image: Uri,
    val remoteImagePath: String = ""
)
