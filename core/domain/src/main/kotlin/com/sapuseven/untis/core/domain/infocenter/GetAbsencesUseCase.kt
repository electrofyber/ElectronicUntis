package com.sapuseven.untis.core.domain.infocenter

import com.sapuseven.untis.core.datastore.UserSettingsDataSource
import com.sapuseven.untis.core.datastore.model.AbsencesTimeRange
import com.sapuseven.untis.core.domain.repository.InfoCenterRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.absences.Absence
import com.sapuseven.untis.core.model.officehours.OfficeHour
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import java.time.LocalDate
import javax.inject.Inject

class GetAbsencesUseCase @Inject constructor(
	private val infoCenterRepository: InfoCenterRepository,
	private val userSettingsRepository: UserSettingsDataSource,
	private val clock: Clock = Clock.System,
	private val zone: TimeZone = TimeZone.currentSystemDefault(),
	getCurrentSchoolYear: GetCurrentSchoolYearUseCase
) {
	//private val currentSchoolYear =
		//getCurrentSchoolYear() ?: SchoolYearEntity(startDate = LocalDate.now(), endDate = LocalDate.now())

	@OptIn(ExperimentalCoroutinesApi::class)
	operator fun invoke(user: User): Flow<Result<List<Absence>>> =
		userSettingsRepository.getSettings().flatMapLatest { settings ->
			val daysAgo: Long = when (settings.infocenterAbsencesTimeRange) {
				AbsencesTimeRange.SEVEN_DAYS -> 7
				AbsencesTimeRange.FOURTEEN_DAYS -> 14
				AbsencesTimeRange.THIRTY_DAYS -> 30
				AbsencesTimeRange.NINETY_DAYS -> 90
				else -> 0
			}

			val timeRange = if (daysAgo > 0) {
				clock.todayIn(zone).minus(daysAgo, DateTimeUnit.DAY) to clock.todayIn(zone)
			} else {
				clock.todayIn(zone).minus(daysAgo, DateTimeUnit.DAY) to clock.todayIn(zone)
				//currentSchoolYear.startDate to currentSchoolYear.endDate
			}

			infoCenterRepository
				.getAbsences(
					user,
					InfoCenterRepository.AbsencesParams(
						timeRange.first,
						timeRange.second,
						includeExcused = !settings.infocenterAbsencesOnlyUnexcused,
					),
				)
				.map {
					if (settings.infocenterAbsencesSortReverse)
						it.sortedBy { absence -> absence.startDateTime } // oldest first
					else
						it.sortedByDescending { absence -> absence.startDateTime } // newest first
				}
				.map(Result.Companion::success)
				.catch { emit(Result.failure(it)) }
		}
}
