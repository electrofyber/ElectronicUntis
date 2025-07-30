package com.sapuseven.untis.feature.timetable

import androidx.lifecycle.ViewModel
import com.sapuseven.untis.core.model.ElementType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class TimetableViewModel @AssistedInject constructor(
	/*private val userSettingsDataSource: UserSettingsDataSource,
	private val timetableMapper: TimetableMapper,
	internal val userRepository: UserRepository,
	internal val timetableRepository: TimetableRepository,
	internal val masterDataRepository: MasterDataRepository,
	internal val debugInfoRepository: DebugInfoRepository,
	internal val clock: Clock,
	internal val weekLogicService: WeekLogicService,
	buildConfigFieldsProvider: BuildConfigFieldsProvider,*/
	@Assisted val elementId: Long?,
	@Assisted val elementType: ElementType?,
) : ViewModel() {
	@AssistedFactory
	interface Factory {
		fun create(elementId: Long?, elementType: ElementType?): TimetableViewModel
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

	// TODO: Extract to usecase
	private fun buildHourList(
		user: User, range: Pair<Int, Int>?, rangeIndexReset: Boolean
	): List<WeekViewHour> {
		val hourList = mutableListOf<WeekViewHour>()

		user.timeGrid.days.maxByOrNull { it.units.size }?.units?.forEachIndexed { index, hour ->
			// Check if outside configured range
			if (range?.let { index < it.first - 1 || index >= it.second } == true) return@forEachIndexed

			// If label is empty, fill it according to preferences
			val label = if (rangeIndexReset) {
				(index + 2 - (range?.first ?: 1)).toString()
			} else {
				hour.label.ifEmpty { (index + 1).toString() }
			}

			hourList.add(WeekViewHour(hour.startTime, hour.endTime, label))
		}

		return hourList
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
	}

	private suspend fun Flow<CachedSourceResult<List<Period>>>.collectEvents(
		startDate: LocalDate,
		updateLastRefresh: Boolean = true
	) = collect { result ->
		// Give allElements some time to be loaded if it is empty
		withTimeoutOrNull(1_000) {
			if (allElements.value.isEmpty()) {
				allElements.first { it.isNotEmpty() }
			}
		}

		val events = timetableMapper.mapTimetablePeriodsToWeekViewEvents(
			result.value,
			requestedElement?.getType() ?: ElementType.STUDENT,
			allElements = allElements.value
		)
		val refreshTimestamp = result.originTimeStamp?.let { Instant.ofEpochMilli(it) } ?: Instant.now()
		emitEvents(mapOf(startDate to events))
		if (updateLastRefresh)
			_lastRefresh.emit(refreshTimestamp)
	}*/
}
