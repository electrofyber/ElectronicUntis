package com.sapuseven.untis.core.domain.bookmarks

import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.datastore.UserSettingsDataSource
import com.sapuseven.untis.core.datastore.model.TimetableElement
import javax.inject.Inject

class AddBookmarkUseCase @Inject constructor(
	private val userSettingsDataSource: UserSettingsDataSource,
) {
	suspend operator fun invoke(element: ElementEntity) {
		userSettingsDataSource.updateSettings {
			if (!bookmarksList.any { it.equals(element) }) {
				addBookmarks(TimetableElement.newBuilder().apply {
					elementId = element.id
					elementType = element.type.ordinal
				}.build())
			}
		}
	}
}
