package com.sapuseven.untis.core.api.serializer

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
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

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
	private val format = LocalDateTime.Format {
		dateTime(LocalDateTime.Formats.ISO)
		char('Z')
	}

	override val descriptor: SerialDescriptor =
		PrimitiveSerialDescriptor("kotlinx.datetime.LocalDateTime", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: LocalDateTime) {
		val string = value.format(format)
		encoder.encodeString(string)
	}

	override fun deserialize(decoder: Decoder): LocalDateTime {
		val string = decoder.decodeString()
		return try {
			LocalDateTime.parse(string, format)
		} catch (e: IllegalArgumentException) {
			Instant.fromEpochMilliseconds(0).toLocalDateTime(TimeZone.currentSystemDefault())
		}
	}
}
