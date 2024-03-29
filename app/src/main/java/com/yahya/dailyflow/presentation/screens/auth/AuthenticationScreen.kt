package com.yahya.dailyflow.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yahya.dailyflow.presentation.theme.PortalPurple
import com.yahya.dailyflow.util.Constants.CLIENT_ID
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle

@Composable
fun AuthenticationScreen(
    firebaseAuth: FirebaseAuth,
    oneTapSignInState: OneTapSignInState,
    messageBarState: MessageBarState,
    isGoogleLoading: Boolean,
    isAnonymousLoading: Boolean,
    authenticated: Boolean,
    onGoogleSignInClicked: () -> Unit,
    onAnonymousSignIn: () -> Unit,
    onSuccessfulFirebaseSignIn: (String) -> Unit,
    onFailedFirebaseSignIn: (Exception) -> Unit,
    onDialogDismissed: (String) -> Unit,
    navigateHome: () -> Unit
) {

    LaunchedEffect(key1 = authenticated) {
        if (authenticated) {
            navigateHome()
        }
    }

    Scaffold(
        content = { padding ->
            ContentWithMessageBar(
                messageBarState = messageBarState,
                successContainerColor = PortalPurple
            ) {
                AuthenticationContent(
                    isGoogleLoading = isGoogleLoading,
                    onSignInWithGoogleButtonClicked = onGoogleSignInClicked,
                    isAnonymousLoading = isAnonymousLoading,
                    onAnonymousSignIn = onAnonymousSignIn,
                    modifier = Modifier.padding(padding)
                )
            }
        },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
    )

    OneTapSignInWithGoogle(state = oneTapSignInState,
        clientId = CLIENT_ID,
        onTokenIdReceived = { tokenId ->
            val credential = GoogleAuthProvider.getCredential(tokenId, null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    onSuccessfulFirebaseSignIn(tokenId)
                } else {
                    result.exception?.let(onFailedFirebaseSignIn)
                }
            }
        },
        onDialogDismissed = { message ->
            onDialogDismissed(message)
        })
}
