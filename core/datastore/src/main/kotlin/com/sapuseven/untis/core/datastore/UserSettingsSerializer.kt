package com.sapuseven.untis.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.sapuseven.untis.data.settings.model.Settings
import java.io.InputStream
import java.io.OutputStream


internal object UserSettingsSerializer : Serializer<Settings> {
	override val defaultValue: Settings = Settings.getDefaultInstance()

	override suspend fun readFrom(input: InputStream): Settings {
		try {
			return Settings.parseFrom(input)
		} catch (exception: InvalidProtocolBufferException) {
			throw CorruptionException("Cannot read proto", exception)
		}
	}

	override suspend fun writeTo(
		t: Settings,
		output: OutputStream
	) = t.writeTo(output)
}
