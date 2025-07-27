package com.sapuseven.untis.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.graphics.toColorInt
import com.sapuseven.untis.core.api.model.untis.enumeration.ElementType
import com.sapuseven.untis.core.api.model.untis.enumeration.PeriodState
import com.sapuseven.untis.core.api.model.untis.masterdata.timegrid.Day
import com.sapuseven.untis.core.api.model.untis.timetable.Period
import com.sapuseven.untis.data.repository.UserRepository
import com.sapuseven.untis.data.repository.UserSettingsRepository
import com.sapuseven.untis.data.repository.withDefault
import com.sapuseven.untis.data.settings.model.UserSettings
import com.sapuseven.untis.models.PeriodElementEntity
import com.sapuseven.untis.models.PeriodItem
import com.sapuseven.untis.models.toShortString
import com.sapuseven.untis.models.getShortAnnotatedString
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.ui.weekview.Event
import com.sapuseven.untis.ui.weekview.EventStyle
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class TimetableMapper @Inject constructor(
	private val userRepository: UserRepository,
	private val userSettingsRepository: UserSettingsRepository,
) {
	/**
	 * Prepares periods for further processing or displaying.
	 *
	 * This function filters out items that should be hidden and merges multi-hour periods.
	 *
	 * @param items List of items that should be prepared
	 * @param hideCancelled Whether cancelled items should be removed
	 * @return A list of prepared items
	 */
	fun preparePeriods(
		items: List<PeriodItem>,
		hideCancelled: Boolean
	): List<PeriodItem> = items
		.filterPeriods(hideCancelled)
		.mergePeriods(userRepository.currentUser!!.timeGrid.days)

	suspend fun mapTimetablePeriodsToWeekViewEvents(
		items: List<Period>,
		contextType: ElementType,
		allElements: Map<ElementType, List<ElementEntity>>,
	): List<Event<PeriodItem>> {
		waitForSettings().apply {
			return preparePeriods(items.mapToPeriodItems(allElements), timetableHideCancelled)
				.mapToEvents(
					userSettings = this,
					contextType = contextType,
				)
				.prepareEvents(
					timetableSubstitutionsIrregular,
					timetableBackgroundIrregular
				)
		}
	}

	private suspend fun waitForSettings() = userSettingsRepository.getSettings().filterNotNull().first()

	private fun List<Period>.mapToPeriodItems(allElements: Map<ElementType, List<ElementEntity>>): List<PeriodItem> = map {
		PeriodItem(
			elements = it.elements.map { element ->
				PeriodElementEntity(allElements = allElements, periodElement = element)
			},
			originalPeriod = it
		)
	}

	private fun List<PeriodItem>.mapToEvents(
		userSettings: UserSettings,
		contextType: ElementType,
		includeOrgIds: Boolean = true,
	): List<Event<PeriodItem>> {
		return map {
			with(it) {
				Event(
					title = subjects.toShortString(),
					top = (
						if (contextType == ElementType.TEACHER)
							classes.getShortAnnotatedString(includeOrgIds)
						else
							teachers.getShortAnnotatedString(includeOrgIds)
						),
					bottom = (
						if (contextType == ElementType.ROOM)
							classes.getShortAnnotatedString(includeOrgIds)
						else
							rooms.getShortAnnotatedString(includeOrgIds)
						),
					eventStyle = getColorScheme(userSettings, this),
					start = originalPeriod.startDateTime,
					end = originalPeriod.endDateTime,
					data = this
				)
			}
		}
	}

	private fun getColorScheme(
		userSettings: UserSettings,
		periodItem: PeriodItem,
	): EventStyle = with(userSettings) {
		val subject = periodItem.subjects.firstOrNull()?.entity

		val defaultColor = EventStyle.Custom(
			color = Color((subject?.backColor ?: periodItem.originalPeriod.backColor).toColorInt()),
			textStyle = TextStyle(
				color = Color(
					(subject?.foreColor ?: periodItem.originalPeriod.foreColor).toColorInt()
				)
			)
		)

		val regularColor =
			EventStyle.Custom(Color(backgroundRegular)).withDefault(hasBackgroundRegular(), EventStyle.ThemePrimary)
		val examColor = EventStyle.Custom(Color(backgroundExam)).withDefault(hasBackgroundExam(), EventStyle.ThemeError)
		val cancelledColor = EventStyle.Custom(Color(backgroundCancelled))
			.withDefault(hasBackgroundCancelled(), EventStyle.ThemeTertiary)
		val irregularColor = EventStyle.Custom(Color(backgroundIrregular))
			.withDefault(hasBackgroundIrregular(), EventStyle.ThemeSecondary)

		return when {
			periodItem.isExam() -> if (schoolBackgroundList.contains("exam")) defaultColor else examColor
			periodItem.isCancelled() -> (if (schoolBackgroundList.contains("cancelled")) defaultColor else cancelledColor)
			periodItem.isIrregular() -> if (schoolBackgroundList.contains("irregular")) defaultColor else irregularColor
			else -> if (schoolBackgroundList.contains("regular")) defaultColor else regularColor
		}.withTextStyle(TextStyle(textDecoration = if (periodItem.isCancelled()) TextDecoration.LineThrough else TextDecoration.None))
	}

	/**
	 * Prepares the items for the timetable.
	 *
	 * This function filters out items that should be hidden
	 *
	 * @param hideCancelled Whether cancelled items should be removed
	 * @return A list of prepared items
	 */
	private fun List<PeriodItem>.filterPeriods(
		hideCancelled: Boolean,
	): List<PeriodItem> = mapNotNull { item ->
		if (hideCancelled && item.originalPeriod.`is`(PeriodState.CANCELLED)) return@mapNotNull null
		item
	}

	/**
	 * Prepares the items for the timetable.
	 *
	 * This function marks items as irregular when they match certain rules
	 *
	 * @param substitutionsIrregular Whether items with substitutions should be marked as irregular
	 * @param backgroundIrregular Whether irregular items should have a different background color
	 * @return A list of prepared items
	 */
	private fun List<Event<PeriodItem>>.prepareEvents(
		substitutionsIrregular: Boolean,
		backgroundIrregular: Boolean
	): List<Event<PeriodItem>> = mapNotNull { item ->
		if (substitutionsIrregular) {
			item.data?.apply {
				forceIrregular =
					classes.any { it.replacementEntity != null }
						|| teachers.any { it.replacementEntity != null }
						|| subjects.any { it.replacementEntity != null }
						|| rooms.any { it.replacementEntity != null }
				//TODO|| backgroundIrregular.getValue() && item.data.element.backColor != UNTIS_DEFAULT_COLOR
			}
		}
		item
	}

	private fun List<PeriodItem>.mergePeriods(days: List<Day>): List<PeriodItem> {
		val itemGrid: Array<Array<MutableList<PeriodItem>>> =
			Array(days.size) { Array(days.maxByOrNull { it.units.size }!!.units.size) { mutableListOf() } }
		val leftover: MutableList<PeriodItem> = mutableListOf()

		// TODO: Check if the day from the Untis API is always an english string
		val firstDayOfWeek =
			DayOfWeek.MONDAY //DateTimeFormat.forPattern("EEE").withLocale(Locale.ENGLISH).parseDateTime(days.first().day).dayOfWeek

		// Put all items into a two dimensional array depending on day and hour
		forEach { item ->
			val startDateTime = item.originalPeriod.startDateTime
			val endDateTime = item.originalPeriod.endDateTime

			val day = endDateTime.dayOfWeek.value - firstDayOfWeek.value

			if (day < 0 || day >= days.size) return@forEach

			val thisUnitStartIndex = days[day].units.indexOfFirst {
				it.startTime.truncatedTo(ChronoUnit.MINUTES)
					.equals(startDateTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES))
			}

			val thisUnitEndIndex = days[day].units.indexOfFirst {
				it.endTime.truncatedTo(ChronoUnit.MINUTES)
					.equals(endDateTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES))
			}

			if (thisUnitStartIndex != -1 && thisUnitEndIndex != -1) itemGrid[day][thisUnitStartIndex].add(
				item
			)
			else leftover.add(item)
		}

		val newItems = itemGrid.flatMap { unitsOfDay ->
			unitsOfDay.flatMapIndexed { unitIndex, items ->
				items.onEach {
					var i = 1
					while (unitIndex + i < unitsOfDay.size && it.mergeWith(unitsOfDay[unitIndex + i])) i++
				}
			}
		}.toMutableList()

		newItems.addAll(leftover) // Add items that didn't fit inside the timegrid. These will always be single lessons.

		return newItems
	}

	private fun PeriodItem.mergeWith(items: MutableList<PeriodItem>): Boolean {
		for (i in items.indices.reversed()) {
			val candidate = items[i]

			if (candidate.originalPeriod.startDateTime.dayOfYear != originalPeriod.startDateTime.dayOfYear) continue

			if (this.equalsIgnoreTime(candidate)) {
				originalPeriod.endDateTime = candidate.originalPeriod.endDateTime
				items.removeAt(i)
				return true
			}
		}
		return false
	}
}
