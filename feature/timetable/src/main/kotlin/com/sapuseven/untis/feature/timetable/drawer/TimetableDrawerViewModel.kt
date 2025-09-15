package com.sapuseven.untis.feature.timetable.drawer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.domain.GetTimetableElementTypesUseCase
import com.sapuseven.untis.core.domain.bookmarks.AddBookmarkUseCase
import com.sapuseven.untis.core.domain.bookmarks.GetBookmarksUseCase
import com.sapuseven.untis.core.domain.bookmarks.RemoveBookmarkUseCase
import com.sapuseven.untis.core.model.ElementType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableDrawerViewModel @Inject constructor(
	private val addBookmark: AddBookmarkUseCase,
	private val removeBookmark: RemoveBookmarkUseCase,
	getTimetableElementTypes: GetTimetableElementTypesUseCase,
	getBookmarks: GetBookmarksUseCase,
) : ViewModel() {
	var enableDrawerGestures: Boolean = true
		private set

	var bookmarkDeleteDialog by mutableStateOf<ElementEntity?>(null)
		private set

	val elements: StateFlow<Set<ElementType>> = getTimetableElementTypes().stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = emptySet()
	)

	val bookmarks: StateFlow<List<ElementEntity>> = getBookmarks().stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = emptyList()
	)

	fun onBookmarkAdd(item: ElementEntity) = viewModelScope.launch {
		addBookmark(item)
	}

	fun onBookmarkRemove(bookmark: ElementEntity) = viewModelScope.launch {
		removeBookmark(bookmark)
		dismissBookmarkDeleteDialog()
	}

	fun showBookmarkDeleteDialog(bookmark: ElementEntity) {
		bookmarkDeleteDialog = bookmark
	}

	fun dismissBookmarkDeleteDialog() {
		bookmarkDeleteDialog = null
	}

	fun getBookmarkDisplayName(bookmark: ElementEntity): String {
		return bookmark.getLongName()
	}
}
