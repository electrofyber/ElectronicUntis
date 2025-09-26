package com.sapuseven.untis.feature.timetable

import android.util.Log
import com.sapuseven.untis.core.domain.cache.FromCache
import com.sapuseven.untis.core.domain.timetable.GetTimetableUseCase
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.Period
import com.sapuseven.untis.core.model.user.User
import com.sapuseven.untis.feature.timetable.mapper.TimetableMapper
import com.sapuseven.untis.feature.timetable.weekview.WeekViewEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant

class TimetablePageManager(
	private val getTimetable: GetTimetableUseCase,
	private val timetableMapper: TimetableMapper,
	private val user: User,
) {
	val pagerState = MutableStateFlow(
		PagerState(
			events = emptyMap(),
			lastRefresh = emptyMap(),
			isLoading = true,
			error = null
		)
	)

	private suspend fun loadPage(page: Int, element: Element, fromCache: FromCache) {
		getTimetable(user, element, page, fromCache)
			.catch { e ->
				val message = "Error"//if (throwable is UntisApiException) "API error" else "other error"
				pagerState.update { it.copy(error = message) }
				Log.e("TimetablePageManager", "Page load failed", e)
				// TODO: Show in UI
			}
			.collect { timetable ->
				val events = timetable.periods.map {
					timetableMapper.mapPeriodToWeekViewEvent(it, element.type)
				}

				pagerState.update {
					it.copy(
						lastRefresh = it.lastRefresh + (page to timetable.timestamp),
						events = it.events + (page to events),
					)
				}
			}
	}

	suspend fun loadPage(page: Int, element: Element, skipCache: Boolean = false) {
		pagerState.update { it.copy(isLoading = true, error = null) }

		loadPage(page, element, if (skipCache) FromCache.NEVER else FromCache.CACHED_THEN_LOAD)

		pagerState.update { it.copy(isLoading = false) }
	}

	suspend fun preloadPages(page: Int, element: Element) {
		pagerState.update { it.copy(isLoading = true, error = null) }

		coroutineScope {
			((page - 1)..(page + 1))
				.filter { it !in pagerState.value.events.keys }
				.map { targetPage ->
					async {
						loadPage(targetPage, element, FromCache.CACHED_THEN_LOAD)
					}
				}
				.awaitAll()
		}

		pagerState.update { it.copy(isLoading = false) }
	}

	data class PagerState(
		val events: Map<Int, List<WeekViewEvent<Period>>> = emptyMap(),
		val lastRefresh: Map<Int, Instant> = emptyMap(),
		val isLoading: Boolean = false,
		val error: String? = null
	)
}
