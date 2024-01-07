package com.yahya.dailyflow.navigation

import com.yahya.dailyflow.navigation.NavigationArguments.WRITE_SCREEN_ARGUMENT_KEY_DIARY_ID

object Routes {
    const val AUTHENTICATION = "authentication_screen"
    const val HOME = "home_screen"
    const val WRITE = "write_screen"
    const val WRITE_WITH_ARGS =
        "write_screen?$WRITE_SCREEN_ARGUMENT_KEY_DIARY_ID={$WRITE_SCREEN_ARGUMENT_KEY_DIARY_ID}"
    const val DRAW = "draw_screen"
    const val SETTINGS = "settings_screen"
}