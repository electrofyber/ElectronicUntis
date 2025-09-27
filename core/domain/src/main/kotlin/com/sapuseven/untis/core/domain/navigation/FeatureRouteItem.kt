package com.sapuseven.untis.core.domain.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class FeatureRouteItem(
	@DrawableRes val icon: Int,
	@StringRes val label: Int,
	val route: Any
)
