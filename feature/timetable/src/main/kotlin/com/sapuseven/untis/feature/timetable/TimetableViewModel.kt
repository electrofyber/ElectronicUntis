package com.sapuseven.untis.feature.timetable

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sapuseven.untis.core.datastore.UserSettingsDataSource
import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.domain.timetable.GetHourListUseCase
import com.sapuseven.untis.core.domain.timetable.GetTimetableUseCase
import com.sapuseven.untis.core.domain.timetable.WeekLogicService
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.user.User
import com.sapuseven.untis.feature.timetable.mapper.TimetableMapper
import com.sapuseven.untis.feature.timetable.navigation.TimetableRoute
import com.sapuseven.untis.feature.timetable.weekview.startDateForPageIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toKotlinLocalDate
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
	/*private val userSettingsDataSource: UserSettingsDataSource,
	private val timetableMapper: TimetableMapper,
	internal val userRepository: UserRepository,
	internal val timetableRepository: TimetableRepository,
	internal val masterDataRepository: MasterDataRepository,
	internal val debugInfoRepository: DebugInfoRepository,
	internal val clock: Clock,
	buildConfigFieldsProvider: BuildConfigFieldsProvider,*/
	private val clock: Clock,
	private val userRepository: UserRepository,
	private val masterDataRepository: MasterDataRepository,
	private val timetableMapper: TimetableMapper,
	internal val weekLogicService: WeekLogicService,
	savedStateHandle: SavedStateHandle,
	userSettingsDataSource: UserSettingsDataSource,
	getHourList: GetHourListUseCase,
	private val getTimetable: GetTimetableUseCase,
) : ViewModel() {
	private val args = savedStateHandle.toRoute<TimetableRoute>()
	private val user = userRepository.getActiveUser();
	private val requestedElement = /*if (args.id != null && args.type != null)
		masterDataRepository.getElement(ElementKey(args.id, args.type)) else TODO*/ Element.personal(1, ElementType.STUDENT, "Test")

	private val _uiState = MutableStateFlow(
		TimetableUiState(
			user = user,
			title = user.displayName,
			currentTime = clock.now(),
		)
	)
	val uiState: StateFlow<TimetableUiState> = _uiState

	private val loadingExceptionHandler: suspend FlowCollector<*>.(Throwable) -> Unit = { throwable ->
		val message = "Error"//if (throwable is UntisApiException) "API error" else "other error"
		Log.e("TimetableViewModel", "Failed to load timetable due to $message", throwable)
		// TODO: Show in UI
	}

	init {
		viewModelScope.launch {
			while (true) {
				_uiState.update {
					it.copy(
						currentTime = clock.now(),
					)
				}
				delay(10_000)
			}
		}

		userRepository.observeAllUsers()
			.onEach { users -> _uiState.update { it.copy(userList = users) } }
			.launchIn(viewModelScope)

		getHourList()
			.onEach { hourList -> _uiState.update { it.copy(hourList = hourList) } }
			.launchIn(viewModelScope)
	}

	fun switchUser(user: User) = viewModelScope.launch {
		userRepository.switchUser(user.id)
	}

	fun editUsers() {
		// TODO
	}

	fun onPageChanged(page: Int) = viewModelScope.launch {
		_uiState.update { it.copy(currentPage = page) }

		((page - 1)..(page + 1))
			.filter { it !in _uiState.value.events }
			.map { targetPage ->
				async {
					_uiState.update { it.copy(loading = true) }
					loadPage(targetPage, true)
				}
			}
			.awaitAll()

		_uiState.update { it.copy(loading = false) }
	}

	fun onPageReload(page: Int) = viewModelScope.launch {
		_uiState.update { it.copy(loading = true) }

		async { loadPage(page, false) }.await()

		_uiState.update { it.copy(loading = false) }
	}

	fun lastRefreshPeriod(lastRefresh: Instant?): DateTimePeriod? {
		return lastRefresh?.periodUntil(clock.now(), TimeZone.currentSystemDefault())
	}

	private suspend fun loadPage(page: Int, fromCache: Boolean) {
		val startDate = startDateForPageIndex(page.toLong())
		getTimetable(user, requestedElement, startDate.toKotlinLocalDate(), fromCache = fromCache)
			.catch(loadingExceptionHandler)
			.collect { timetable ->
				val events = timetable.periods.map {
					timetableMapper.mapPeriodToWeekViewEvent(it, requestedElement.type)
				}

				_uiState.update { old ->
					old.copy(
						lastRefresh = old.lastRefresh + (page to timetable.timestamp),
						events = old.events + (page to events)
					)
				}
			}
	}

	/*private val allElements = masterDataRepository.timetableElements
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = emptyMap()
		)
	val requestedElement = elementId?.let { masterDataRepository.getElement(elementId, elementType!! /* TODO not optimal...*/) }

	var profileManagementDialog by mutableStateOf(false)
	var timetableItemDetailsDialog by mutableStateOf<Pair<List<Event<PeriodItem>>, Int>?>(null)
	var feedbackDialog by mutableStateOf(false)
	var loading by mutableStateOf(true)

	val currentUser: User = userRepository.currentUser!!
	val allUsersState: StateFlow<List<User>> = userRepository.allUsersState

	val isDebug = buildConfigFieldsProvider.get().isDebug

	private val _personalElement = MutableStateFlow<PeriodElement?>(null)

	private val _needsPersonalTimetable = MutableStateFlow(false)
	val needsPersonalTimetable: StateFlow<Boolean> = _needsPersonalTimetable

	private val _hourList = MutableStateFlow<List<WeekViewHour>>(emptyList())
	val hourList: StateFlow<List<WeekViewHour>> = _hourList

	private val _events = MutableStateFlow<Map<LocalDate, List<Event<PeriodItem>>>>(emptyMap())
	val events: StateFlow<Map<LocalDate, List<Event<PeriodItem>>>> = _events

	private val _holidays = MutableStateFlow<List<Holiday>>(emptyList())
	val holidays: StateFlow<List<Holiday>> = _holidays

	private val _lastRefresh = MutableStateFlow<Instant?>(null)
	val lastRefresh: StateFlow<Instant?> = _lastRefresh

	private val _weekViewColorScheme = MutableStateFlow(WeekViewColorScheme.default())
	val weekViewColorScheme: StateFlow<WeekViewColorScheme> = _weekViewColorScheme

	private val _weekViewScale = MutableStateFlow(1f)
	val weekViewScale: StateFlow<Float> = _weekViewScale

	private val _weekViewZoomEnabled = MutableStateFlow(true)
	val weekViewZoomEnabled: StateFlow<Boolean> = _weekViewZoomEnabled

	private val _weekViewEventStyle = MutableStateFlow(WeekViewEventStyle.default())
	val weekViewEventStyle: StateFlow<WeekViewEventStyle> = _weekViewEventStyle

	// TODO: Instead of using the datasource directly, move this to a repository
	private val _userSettings = userSettingsDataSource.getSettings()

	private val _ready = combine(
		_hourList,
		_userSettings
	) { hourList, userSettings ->
		hourList.isNotEmpty()
	}
	val ready: StateFlow<Boolean> = _ready.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = false
	)

	private var currentPage = 0

	private val loadingExceptionHandler: suspend FlowCollector<*>.(Throwable) -> Unit = { throwable ->
		val message = if (throwable is UntisApiException) "API error" else "other error"
		Log.e("TimetableViewModel", "Failed to load timetable due to $message", throwable)
	}

	init {
		viewModelScope.launch {
			_userSettings.collect { userSettings ->
				// All properties that are based on preferences are set here
				userSettings.timetablePersonalTimetable?.toPeriodElement()?.let {
					_personalElement.value = it
					_needsPersonalTimetable.emit(false)
					//_currentElement.update { prev -> prev ?: it } // Update only if null
				} ?: run {
					_personalElement.value = currentUser.userData.elemType?.let {
						PeriodElement(it, currentUser.userData.elemId)
					}
					_needsPersonalTimetable.emit(requestedElement == null && _personalElement.value == null)
				}
				_hourList.value = buildHourList(
					user = currentUser,
					range = userSettings.timetableRange.convertRangeToPair(),
					rangeIndexReset = userSettings.timetableRangeIndexReset
				)
				_weekViewColorScheme.value = WeekViewColorScheme(
					pastBackgroundColor = Color(userSettings.backgroundPast),
					futureBackgroundColor = Color(userSettings.backgroundFuture),
					indicatorColor = Color(userSettings.marker),
				)
				_weekViewEventStyle.value = WeekViewEventStyle(
					padding = userSettings.timetableItemPadding,
					cornerRadius = userSettings.timetableItemCornerRadius,
					lessonNameStyle = TextStyle(
						fontSize = userSettings.timetableLessonNameFontSize.sp,
						fontWeight = if (userSettings.timetableBoldLessonName) FontWeight.Bold else FontWeight.Normal
					),
					lessonInfoStyle = TextStyle(
						fontSize = userSettings.timetableLessonInfoFontSize.sp
					),
					lessonInfoCentered = userSettings.timetableCenteredLessonInfo,
				)
				_weekViewScale.value = userSettings.timetableZoomLevel
				_weekViewZoomEnabled.value = userSettings.timetableZoomEnabled
			}
		}

		viewModelScope.launch {
			val holidays = (masterDataRepository.userData?.holidays ?: emptyList())
				.map { holiday ->
					Holiday(
						title = holiday.name,
						colorScheme = EventStyle.Transparent,
						start = holiday.startDate!!,
						end = holiday.endDate!!,
					)
				}
			_holidays.emit(holidays)
		}
	}

	fun switchUser(user: User) {
		userRepository.switchUser(user)
	}

	fun editUser(user: User?) {
		navigator.(AppRoutes.LoginDataInput(userId = user?.id ?: -1))
	}

	fun editUsers() {
		profileManagementDialog = true
	}

	fun onPageChange(pageOffset: Int = currentPage) {
		currentPage = pageOffset
		viewModelScope.launch {
			loading = true
			((pageOffset - 1)..(pageOffset + 1)).map { targetPage ->
				async {
					val startDate = startDateForPageIndex(targetPage.toLong())
					loadEvents(
						startDate,
						// Note: Right now this is a workaround to show the "last refresh" text when changing pages,
						//  since it isn't stored after emitting the events.
						//  For that reason the cache is queried more often than necessary.
						if (_events.value.contains(startDate)) FromCache.ONLY else FromCache.CACHED_THEN_LOAD
					)
						.catch(loadingExceptionHandler)
						.collectEvents(startDate, targetPage == pageOffset)
				}
			}.awaitAll()
			loading = false
		}
	}

	suspend fun onPageReload(pageOffset: Int) {
		val startDate = startDateForPageIndex(pageOffset.toLong())
		loadEvents(startDate, FromCache.NEVER)
			.catch(loadingExceptionHandler)
			.collectEvents(startDate)
	}

	fun onItemClick(itemsWithIndex: Pair<List<Event<PeriodItem>>, Int>) {
		timetableItemDetailsDialog = itemsWithIndex
	}

	suspend fun onZoom(zoomLevel: Float) {
		userSettingsRepository.updateSettings { timetableZoomLevel = zoomLevel }
	}

	private suspend fun loadEvents(startDate: LocalDate, fromCache: FromCache): Flow<Timetable> {
		// Load the requested element (nav args) or the personal element
		val elementToLoad = requestedElement?.let { PeriodElement(it.getType(), it.id) } ?: _personalElement
			.combine(_needsPersonalTimetable) { element, _ -> element } // Emit a value if _personalElement or _needsPersonalTimetable changes
			.first { it != null || _needsPersonalTimetable.value } // Take the first non-null _personalElement or null if _needsPersonalTimetable

		return elementToLoad?.let {
			timetableRepository.getTimetable(
				TimetableRepository.TimetableParams(
					elementId = it.id,
					elementType = it.type,
					startDate = startDate,
				),
				fromCache
			)
		} ?: emptyFlow()
	}

	private fun emitEvents(events: Map<LocalDate, List<Event<PeriodItem>>>) {
		_events.update {
			val newEvents = it.toMutableMap()
			events.forEach { (date, events) ->
				newEvents[date] = events
			}
			newEvents.toMap()
		}
	}

	private fun groupEventsByDate(events: List<Event<PeriodItem>>): Map<LocalDate, List<Event<PeriodItem>>> {
		val groupedEvents = mutableMapOf<LocalDate, MutableList<Event<PeriodItem>>>()

		for (event in events) {
			val eventDate = event.start.toLocalDate()
			groupedEvents.computeIfAbsent(eventDate) { mutableListOf() }.add(event)
		}

		// Convert the map to an immutable version if needed
		return groupedEvents.mapValues { it.value.toList() }
	}

	val onAnonymousSettingsClick = {
		navigator.navigate(AppRoutes.Settings.Timetable(highlightTitle = R.string.preference_timetable_personal_timetable))
	}

	fun showFeedback() {
		feedbackDialog = true
	}

	fun getTitle(context: Context) = requestedElement?.let {
		if (it == _personalElement.value) null // Use Profile name for personal timetable
		else it.getLongName()
	}
		?: (currentUser.getDisplayedName(context) + (if (isDebug) " (${currentUser.id})" else ""))

	fun showElement(element: ElementEntity?) {
		if (requestedElement != element)
			navigator.navigate(AppRoutes.Timetable(element?.let { PeriodElement(it.getType(), it.id) })) {
				if (element == null) {
					NavOptionsBuilder.popUpTo(0)
				}
			}
	}*/
}
