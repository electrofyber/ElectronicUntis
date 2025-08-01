package com.sapuseven.untis.core.api.serializer

import kotlinx.datetime.LocalTime
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test

class LocalTimeSerializerTest {
	@Test
	fun serialize() {
		Assert.assertEquals("\"T16:05\"", Json.encodeToString(LocalTimeSerializer, LocalTime(16, 5)))
	}

	@Test
	fun deserialize() {
		Assert.assertEquals(LocalTime(16, 5), Json.decodeFromString(LocalTimeSerializer, "\"T16:05\""))
	}

	@Test
	fun deserializeEmpty_returnsDefaultValue() {
		Assert.assertEquals(LocalTime(0, 0), Json.decodeFromString(LocalTimeSerializer, "\"\""))
	}
}
