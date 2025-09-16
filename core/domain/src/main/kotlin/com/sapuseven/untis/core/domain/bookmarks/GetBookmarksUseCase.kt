package com.sapuseven.untis.core.domain.bookmarks

import com.sapuseven.untis.core.datastore.UserSettingsDataSource
import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
	private val userRepository: UserRepository,
	private val masterDataRepository: MasterDataRepository,
	private val userSettingsDataSource: UserSettingsDataSource,
) {
	operator fun invoke(): Flow<List<Element>> {
		return userSettingsDataSource.getSettings(userRepository.getActiveUser().id)
			.map { it.bookmarksList }
			.map {
				it.mapNotNull { bookmark ->
					// TODO: Maybe not store the ElementType as Int?
					ElementType.entries.getOrNull(bookmark.elementType)?.let { type ->
						masterDataRepository.getElement(bookmark.elementId, type)
					}
				}
			}
	}
}
