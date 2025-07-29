package com.sapuseven.untis.core.datastore

interface UserProvider {
	fun optionalUserId(): Long?
	suspend fun requireUserId(): Long
}
