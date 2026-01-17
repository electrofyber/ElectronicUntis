package com.sapuseven.untis.core.domain.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class FeatureRouteItem(
	@field:DrawableRes val icon: Int,
	@field:StringRes val label: Int,
	val route: Any
)
