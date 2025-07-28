package com.sapuseven.untis.core.data.repository

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class DebugInfoRepository {
	@OptIn(ExperimentalSerializationApi::class)
	private val json = Json {
		encodeDefaults = true
		prettyPrint = true
		prettyPrintIndent = "  "
	}

	fun getColorSchemeDebugInfo(colorSchemeString: String): String = colorSchemeString
		.replace("(\\w+=\\w+\\([^)]*\\))".toRegex(), "$1\n")
		.replace(", sRGB IEC61966-2.1", "")
		.removePrefix("ColorScheme(")
		.removeSuffix("\n)")
}
