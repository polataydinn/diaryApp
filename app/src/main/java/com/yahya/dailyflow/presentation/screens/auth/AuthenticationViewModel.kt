package com.yahya.dailyflow.presentation.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val app: App) : ViewModel() {
    var googleLoadingState = mutableStateOf(false)
        private set

    var anonymousLoadingState = mutableStateOf(false)
        private set

    var authenticated = mutableStateOf(false)
        private set

    fun setGoogleLoading(isLoading: Boolean) {
        googleLoadingState.value = isLoading
    }

    fun setAnonymousLoading(isLoading: Boolean) {
        anonymousLoadingState.value = isLoading
    }

    private fun setAuthentication(isAuthenticated: Boolean) {
        authenticated.value = isAuthenticated
    }

    fun signInAnonymouslyWithMongoAtlas(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    app.login(
                        credentials = Credentials.anonymous()
                    ).loggedIn
                }
                withContext(Dispatchers.Main) {
                    if (result) {
                        onSuccess()
                        delay(600)
                        setAuthentication(true)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            } finally {
                setAnonymousLoading(false)
            }
        }
    }

    fun signInWithMongoAtlas(
        tokenId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    app.login(
                        credentials = Credentials.google(
                            token = tokenId, type = GoogleAuthType.ID_TOKEN
                        )
                    ).loggedIn
                }
                withContext(Dispatchers.Main) {
                    if (result) {
                        onSuccess()
                        delay(600)
                        setAuthentication(true)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            } finally {
                setGoogleLoading(false)
            }
        }
    }
}