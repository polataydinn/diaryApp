package com.yahya.dailyflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import com.yahya.dailyflow.presentation.theme.DimensionalFeelsTheme
import com.yahya.dailyflow.data.database.ImageToDeleteDao
import com.yahya.dailyflow.data.database.ImageToUploadDao
import com.yahya.dailyflow.data.database.entity.ImageToDelete
import com.yahya.dailyflow.data.database.entity.ImageToUpload
import com.yahya.dailyflow.navigation.NavGraph
import com.yahya.dailyflow.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var keepSplashOpened = true

    @Inject
    lateinit var imageToUploadDao: ImageToUploadDao

    @Inject
    lateinit var imageToDeleteDao: ImageToDeleteDao

    @Inject
    lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition { keepSplashOpened }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        FirebaseApp.initializeApp(applicationContext)
        setContent {
            DimensionalFeelsTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navController = rememberNavController()
                NavGraph(startDestinationRoute = getStartDestination(app = app),
                    navController = navController,
                    drawerState = drawerState,
                    onDataLoaded = { keepSplashOpened = false })
            }
        }
        /*
        cleanupCheck(
            firebaseStorage = FirebaseStorage.getInstance(),
            scope = lifecycleScope,
            imageToUploadDao = imageToUploadDao,
            imageToDeleteDao = imageToDeleteDao
        )
         */
    }
}


private fun cleanupCheck(
    firebaseStorage: FirebaseStorage,
    scope: CoroutineScope,
    imageToUploadDao: ImageToUploadDao,
    imageToDeleteDao: ImageToDeleteDao
) {
    scope.launch(Dispatchers.IO + SupervisorJob()) {
        val uploadResult = imageToUploadDao.getAllImages()
        uploadResult.forEach { imageToUpload ->
            retryUploadingImageToFirebase(
                firebaseStorage = firebaseStorage,
                imageToUpload = imageToUpload,
                onSuccess = {
                    scope.launch(Dispatchers.IO) {
                        imageToUploadDao.deleteImageToUpload(imageId = imageToUpload.id)
                    }
                })
        }
        val deleteResult = imageToDeleteDao.getAllImages()
        deleteResult.forEach { imageToDelete ->
            retryDeletingImageFromFirebase(
                firebaseStorage = firebaseStorage,
                imageToDelete = imageToDelete,
                onSuccess = {
                    scope.launch(Dispatchers.IO) {
                        imageToDeleteDao.cleanupImage(imageId = imageToDelete.id)
                    }
                })
        }
    }
}

private fun getStartDestination(app: App): String {
    val user = app.currentUser
    return if (user != null && user.loggedIn) Screen.Home.route else Screen.Authentication.route
}

fun retryUploadingImageToFirebase(
    firebaseStorage: FirebaseStorage, imageToUpload: ImageToUpload, onSuccess: () -> Unit
) {
    val storage = firebaseStorage.reference

    try {
        storage.child(imageToUpload.remoteImagePath).putFile(
            imageToUpload.imageUri.toUri(), storageMetadata { }, imageToUpload.sessionUri.toUri()
        )
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        onSuccess()
    }
}

fun retryDeletingImageFromFirebase(
    firebaseStorage: FirebaseStorage, imageToDelete: ImageToDelete, onSuccess: () -> Unit
) {
    val storage = firebaseStorage.reference
    storage.child(imageToDelete.remoteImagePath).delete().addOnSuccessListener { onSuccess() }
}