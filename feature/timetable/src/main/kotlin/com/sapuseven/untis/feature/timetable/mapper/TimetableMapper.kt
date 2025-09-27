package com.sapuseven.untis.feature.timetable.mapper

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.sapuseven.untis.core.datastore.UserSettingsDataSource
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.domain.timetable.toShortString
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.timetable.Period
import com.sapuseven.untis.core.model.timetable.Period.Companion.ELEMENT_NAME_SEPARATOR
import com.sapuseven.untis.feature.timetable.weekview.EventStyle
import com.sapuseven.untis.feature.timetable.weekview.WeekViewEvent
import javax.inject.Inject

class TimetableMapper @Inject constructor(
	private val userRepository: UserRepository,
	private val userSettingsDataSource: UserSettingsDataSource
) {
	fun mapPeriodToWeekViewEvent(
		period: Period,
		contextType: ElementType? = null,
		includeReplacements: Boolean = true,
	): WeekViewEvent<Period> = WeekViewEvent(
		id = period.id,
		title = period.subjects.toShortString(),
		top = (
			if (contextType == ElementType.TEACHER)
				period.classes.toShortAnnotatedString(includeReplacements)
			else
				period.teachers.toShortAnnotatedString(includeReplacements)
			),
		bottom = (
			if (contextType == ElementType.ROOM)
				period.classes.toShortAnnotatedString(includeReplacements)
			else
				period.rooms.toShortAnnotatedString(includeReplacements)
			),
		//eventStyle = getColorScheme(userSettings, period),
		eventStyle = EventStyle.ThemePrimary,
		start = period.startDateTime,
		end = period.endDateTime,
		data = period
	)

	/**
	 * Prepares periods for further processing or displaying.
	 *
	 * This function filters out items that should be hidden and merges multi-hour periods.
	 *
	 * @param items List of items that should be prepared
	 * @param hideCancelled Whether cancelled items should be removed
	 * @return A list of prepared items
	 */
	/*fun preparePeriods(
		items: List<Period>,
		hideCancelled: Boolean
	): List<Period> = items
		.filterPeriods(hideCancelled)

	suspend fun mapTimetablePeriodsToWeekViewEvents(
		items: List<Period>,
		contextType: ElementType,
		allElements: Map<ElementType, List<Element>>,
	): List<WeekViewEvent<Period>> {
		waitForSettings().apply {
			return preparePeriods(items.mapToPeriodItems(allElements), UserSettings.getTimetableHideCancelled)
				.mapToEvents(
					userSettings = this,
					contextType = contextType,
				)
				.prepareEvents(
					UserSettings.getTimetableSubstitutionsIrregular,
					UserSettings.getTimetableBackgroundIrregular
				)
		}
	}

	private suspend fun waitForSettings() = userSettingsDataSource.getSettings().filterNotNull().first()

	private fun List<Period>.mapToPeriodItems(allElements: Map<ElementType, List<Element>>): List<Period> = map {
		Period(
			elements = it.elements.map { element ->
				PeriodElementEntity(allElements = allElements, periodElement = element)
			},
			originalPeriod = it
		)
	}

	private fun List<Period>.mapToEvents(
		userSettings: UserSettings,
		contextType: ElementType,
		includeOrgIds: Boolean = true,
	): List<WeekViewEvent<Period>> {
		return map {
			with(it) {
				WeekViewEvent(
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
		periodItem: Period,
	): EventStyle = with(userSettings) {
		val subject = periodItem.subjects.firstOrNull()?.entity

		val defaultColor = EventStyle.Custom(
			color = Color((subject?.backColor ?: periodItem.originalPeriod.backColor).toColorInt()),
			textStyle = TextStyle(
				color = androidx.compose.ui.graphics.Color(
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
		}.withTextStyle(TextStyle(textDecoration = if (periodItem.isCancelled()) TextDecoration.Companion.LineThrough else TextDecoration.Companion.None))
	}

	/**
	 * Prepares the items for the timetable.
	 *
	 * This function filters out items that should be hidden
	 *
	 * @param hideCancelled Whether cancelled items should be removed
	 * @return A list of prepared items
	 */
	private fun List<Period>.filterPeriods(
		hideCancelled: Boolean,
	): List<Period> = mapNotNull { item ->
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
	private fun List<WeekViewEvent<Period>>.prepareEvents(
		substitutionsIrregular: Boolean,
		backgroundIrregular: Boolean
	): List<WeekViewEvent<Period>> = mapNotNull { item ->
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

	private fun List<Period>.mergePeriods(days: List<Day>): List<Period> {
		val itemGrid: Array<Array<MutableList<Period>>> =
			Array(days.size) { Array(days.maxByOrNull { it.units.size }!!.units.size) { mutableListOf() } }
		val leftover: MutableList<Period> = mutableListOf()

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
	}*/
}

private fun List<Element>.toShortAnnotatedString(showReplacements: Boolean): AnnotatedString {
	return buildAnnotatedString {
		forEach {
			if (this.length > 0) append(ELEMENT_NAME_SEPARATOR)
			append(it.shortName)

			if (showReplacements && it.replaced) {
				append(ELEMENT_NAME_SEPARATOR)
				withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
					append(it.shortName)
				}
			}
		}
	}
}
