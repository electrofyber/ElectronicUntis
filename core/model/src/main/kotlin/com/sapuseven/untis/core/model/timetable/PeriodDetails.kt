package com.sapuseven.untis.core.model.timetable

import com.sapuseven.untis.core.model.absences.Absence

data class PeriodDetails(
	val absenceChecked: Boolean,
	val studentIds: List<Long> = emptyList(),
	val absences: List<Absence> = emptyList(),
	//val classRegEvents: List<ClassRegEvent>?,
	//val exemptions: List<StudentExemption>?,
	//val prioritizedAttendances: List<PrioritizedAttendance>?,
	//val text: PeriodText?,
	//val topic: LessonTopic?,
	//val homeWorks: List<HomeWork>?,
	//val seatingPlan: SeatingPlan?,
	//val classRoles: List<ClassRole>?,
	//val channel: MessengerChannel? = null,
	//val can: List<com.sapuseven.untis.core.api.model.untis.enumeration.PeriodRight>
)
