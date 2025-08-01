package com.sapuseven.untis.core.api.serializer

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LocalDateSerializer : KSerializer<LocalDate> {
	private val format = LocalDate.Formats.ISO

	override val descriptor: SerialDescriptor =
		PrimitiveSerialDescriptor("kotlinx.datetime.LocalDate", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: LocalDate) {
		val string = value.format(format)
		encoder.encodeString(string)
	}

	override fun deserialize(decoder: Decoder): LocalDate {
		val string = decoder.decodeString()
		return try {
			LocalDate.parse(string, format)
		} catch (e: IllegalArgumentException) {
			Instant.fromEpochMilliseconds(0).toLocalDateTime(TimeZone.currentSystemDefault()).date
		}
	}
}
