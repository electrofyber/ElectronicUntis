package com.sapuseven.untis.core.domain.bookmarks

import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.datastore.UserSettingsDataSource
import javax.inject.Inject

class RemoveBookmarkUseCase @Inject constructor(
	private val userSettingsDataSource: UserSettingsDataSource,
) {
	suspend operator fun invoke(element: ElementEntity) {
		userSettingsDataSource.updateSettings {
			removeBookmarks(bookmarksList.indexOfFirst {
				it.elementId == element.id && it.elementType == element.type.ordinal
			})
		}
	}
}
