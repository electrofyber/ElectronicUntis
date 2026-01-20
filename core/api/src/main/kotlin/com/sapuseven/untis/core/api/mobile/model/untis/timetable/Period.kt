package com.sapuseven.untis.core.api.mobile.model.untis.timetable

import com.sapuseven.untis.core.api.mobile.model.untis.classreg.HomeWork
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.PeriodRight
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.PeriodState
import com.sapuseven.untis.core.api.mobile.model.untis.messenger.MessengerChannel
import com.sapuseven.untis.core.api.serializer.DateTime
import kotlinx.serialization.Serializable

@Serializable
data class Period(
	val id: Long,
	val lessonId: Long,
	var startDateTime: DateTime,
	var endDateTime: DateTime,
	val foreColor: String,
	val backColor: String,
	val innerForeColor: String,
	val innerBackColor: String,
	val text: PeriodText,
	val elements: List<PeriodElement> = emptyList(),
	val can: List<PeriodRight> = emptyList(),
	val `is`: List<PeriodState> = emptyList(),
	val homeWorks: List<HomeWork>?,
	val exam: PeriodExam? = null,
	val isOnlinePeriod: Boolean? = null,
	val messengerChannel: MessengerChannel? = null,
	val onlinePeriodLink: String? = null, // Note: This value still seems to be present in the response, but missing in the Untis Mobile sources
	val blockHash: Int? = null // Note: This value still seems to be present in the response, but missing in the Untis Mobile sources
) {
	fun can(periodRight: PeriodRight): Boolean {
		return can.contains(periodRight)
	}

	fun `is`(periodState: PeriodState): Boolean {
		return `is`.contains(periodState)
	}
}
