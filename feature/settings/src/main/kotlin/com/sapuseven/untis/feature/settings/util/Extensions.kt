package com.sapuseven.untis.feature.settings.util

internal fun <T> T.withDefault(isPresent: Boolean, defaultValue: T): T = if (isPresent) this else defaultValue
