package app.mhp.got.ui.utils

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

/**
 * Jetpack navigation component is great for routing to different destinations.
 * However, it does have some strange behavior that, when not handled, end up crashing the app. ex: user taps on a button that routes to different screen quickly, this can crash the app.
 * These utility functions make sure that those issues don't affect the app by only navigating to valid destinations.
 * */

fun NavController.navigateSafely(destination: NavDirections) {
    currentDestination?.getAction(destination.actionId)?.run {
        navigate(destination)
    }
}

fun Fragment.safeNavigation(destination: NavDirections) {
    if(isVisible){
        findNavController().navigateSafely(destination)
    }
}
