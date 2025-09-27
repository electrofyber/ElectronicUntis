package com.sapuseven.untis.core.ui.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

fun fullscreenDialogAnimationEnter(): EnterTransition {
	return slideInVertically(initialOffsetY = { it / 4 }) + fadeIn()
}

fun fullscreenDialogAnimationExit(): ExitTransition {
	return slideOutVertically(targetOffsetY = { it / 4 }) + fadeOut()
}
