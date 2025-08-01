package com.sapuseven.untis.core.api.serializer

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

internal typealias Date = @Serializable(LocalDateSerializer::class) LocalDate
internal typealias DateTime = @Serializable(LocalDateTimeSerializer::class) LocalDateTime
internal typealias Time = @Serializable(LocalTimeSerializer::class) LocalTime
