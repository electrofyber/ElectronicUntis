package com.sapuseven.untis.core.domain.infocenter

import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import java.time.LocalDate
import javax.inject.Inject

class GetCurrentSchoolYearUseCase @Inject constructor(
	private val masterDataRepository: MasterDataRepository,
	private val userRepository: UserRepository
) {
	operator fun invoke(currentDate: LocalDate = LocalDate.now()): Long? {
		return null;

		/*return masterDataRepository.userData?.schoolYears?.find {
			currentDate.isAfter(it.startDate) && currentDate.isBefore(it.endDate)
		}*/
	}
}
