package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.data.cache.DiskCache
import crocodile8.universal_cache.CachedSource
import crocodile8.universal_cache.FromCache
import crocodile8.universal_cache.time.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.serializer
import java.io.File

abstract class BaseCachedRepository(
	protected val cacheDir: File,
	protected val timeProvider: TimeProvider,
	protected val maxAge: Long = 60 * 60 * 1000, // 1 Hour
	protected val strategy: FromCache = FromCache.CACHED_THEN_LOAD
) {
	protected inline fun <reified T : Any> cached(
		segment: String, crossinline apiCall: suspend () -> T
	): (userId: Long) -> Flow<T> {
		val fetcher = cached<Unit, T>(segment) { apiCall() }
		return { userId -> fetcher(Unit, userId) }
	}

	protected inline fun <P : Any, reified T : Any> cached(
		segment: String, crossinline apiCall: suspend (P) -> T
	): (params: P, userId: Long) -> Flow<T> {
		val source = CachedSource<P, T>(
			source = { params -> apiCall(params) },
			cache = DiskCache(File(cacheDir, segment), serializer()),
			timeProvider = timeProvider
		)

		return { params, userId ->
			source.get(
				params = params,
				fromCache = strategy,
				maxAge = maxAge,
				additionalKey = userId
			)
		}
	}
}
