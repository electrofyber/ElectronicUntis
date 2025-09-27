package com.sapuseven.untis.feature.timetable.drawer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapuseven.untis.core.domain.bookmarks.AddBookmarkUseCase
import com.sapuseven.untis.core.domain.bookmarks.GetBookmarksUseCase
import com.sapuseven.untis.core.domain.bookmarks.RemoveBookmarkUseCase
import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
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
	masterDataRepository: MasterDataRepository,
	getBookmarks: GetBookmarksUseCase,
) : ViewModel() {
	var enableDrawerGestures: Boolean = true
		private set

	var bookmarkDeleteDialog by mutableStateOf<Element?>(null)
		private set

	val elements: StateFlow<Map<ElementType, List<Element>>> = masterDataRepository.timetableElements.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = emptyMap()
	)

	val bookmarks: StateFlow<List<Element>> = getBookmarks().stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = emptyList()
	)

	fun onBookmarkAdd(item: Element) = viewModelScope.launch {
		addBookmark(item)
	}

	fun onBookmarkRemove(bookmark: Element) = viewModelScope.launch {
		removeBookmark(bookmark)
		dismissBookmarkDeleteDialog()
	}

	fun showBookmarkDeleteDialog(bookmark: Element) {
		bookmarkDeleteDialog = bookmark
	}

	fun dismissBookmarkDeleteDialog() {
		bookmarkDeleteDialog = null
	}

	fun getBookmarkDisplayName(bookmark: Element): String {
		return bookmark.longName
	}
}
