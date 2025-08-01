package com.sapuseven.untis.core.api.serializer

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LocalTimeSerializer : KSerializer<LocalTime> {
	private val format = LocalTime.Format {
		char('T')
		hour()
		char(':')
		minute()
	}

	override val descriptor: SerialDescriptor =
		PrimitiveSerialDescriptor("kotlinx.datetime.LocalTime", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: LocalTime) {
		val string = value.format(format)
		encoder.encodeString(string)
	}

	override fun deserialize(decoder: Decoder): LocalTime {
		val string = decoder.decodeString()
		return try {
			LocalTime.parse(string, format)
		} catch (e: IllegalArgumentException) {
			Instant.fromEpochMilliseconds(0).toLocalDateTime(TimeZone.currentSystemDefault()).time
		}
	}
}
