package com.sapuseven.untis.data.repository

import androidx.compose.runtime.compositionLocalOf
import com.sapuseven.untis.api.model.untis.enumeration.ElementType
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.database.entity.User
import com.sapuseven.untis.core.database.entity.UserDao
import com.sapuseven.untis.core.database.entity.UserWithData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface MasterDataRepository {
	val user: User?

	@Deprecated("Use the specific flows for user data.")
	val userData: UserWithData?

	/**
	 * This flow provides all active classes for the active user, sorted by name.
	 *
	 * Typical usage in a ViewModel:
	 *
	 * ```kotlin
	 * val classes = repository.classes.stateIn(
	 *     scope = viewModelScope,
	 *     started = SharingStarted.WhileSubscribed(5_000),
	 *     initialValue = emptyList()
	 * )
	 * ```
	 *
	 * And in a Composable:
	 *
	 * ```kotlin
	 * val classes by viewModel.classes.collectAsStateWithLifecycle()
	 * ```
	 *
	 * @see [timetableElements]
	 */
	val classes: Flow<List<ElementEntity>>

	/**
	 * This flow provides all active teachers for the active user, sorted by name.
	 *
	 * Typical usage in a ViewModel:
	 *
	 * ```kotlin
	 * val teachers = repository.teachers.stateIn(
	 *     scope = viewModelScope,
	 *     started = SharingStarted.WhileSubscribed(5_000),
	 *     initialValue = emptyList()
	 * )
	 * ```
	 *
	 * And in a Composable:
	 *
	 * ```kotlin
	 * val teachers by viewModel.teachers.collectAsStateWithLifecycle()
	 * ```
	 *
	 * @see [timetableElements]
	 */
	val teachers: Flow<List<ElementEntity>>

	/**
	 * This flow provides all active subjects for the active user, sorted by name.
	 *
	 * Typical usage in a ViewModel:
	 *
	 * ```kotlin
	 * val subjects = repository.subjects.stateIn(
	 *     scope = viewModelScope,
	 *     started = SharingStarted.WhileSubscribed(5_000),
	 *     initialValue = emptyList()
	 * )
	 * ```
	 *
	 * And in a Composable:
	 *
	 * ```kotlin
	 * val subjects by viewModel.subjects.collectAsStateWithLifecycle()
	 * ```
	 *
	 * @see [timetableElements]
	 */
	val subjects: Flow<List<ElementEntity>>

	/**
	 * This flow provides all active rooms for the active user, sorted by name.
	 *
	 * Typical usage in a ViewModel:
	 *
	 * ```kotlin
	 * val rooms = repository.rooms.stateIn(
	 *     scope = viewModelScope,
	 *     started = SharingStarted.WhileSubscribed(5_000),
	 *     initialValue = emptyList()
	 * )
	 * ```
	 *
	 * And in a Composable:
	 *
	 * ```kotlin
	 * val rooms by viewModel.rooms.collectAsStateWithLifecycle()
	 * ```
	 *
	 * @see [timetableElements]
	 */
	val rooms: Flow<List<ElementEntity>>

	/**
	 * This flow combines [classes], [teachers], [subjects] and [rooms] in a single flow.
	 *
	 * It will emit an empty map if all lists are empty or no user is active.
	 *
	 * Typical usage in a ViewModel:
	 *
	 * ```kotlin
	 * val elements = repository.timetableElements.stateIn(
	 *     scope = viewModelScope,
	 *     started = SharingStarted.WhileSubscribed(5_000),
	 *     initialValue = emptyMap()
	 * )
	 * ```
	 *
	 * And in a Composable:
	 *
	 * ```kotlin
	 * val elements by viewModel.elements.collectAsStateWithLifecycle()
	 * ```
	 *
	 * @see [classes]
	 * @see [teachers]
	 * @see [subjects]
	 * @see [rooms]
	 */
	val timetableElements: Flow<Map<ElementType, List<ElementEntity>>>

	fun getElement(id: Long, type: ElementType): ElementEntity?
}

@Singleton
class DefaultMasterDataRepository : MasterDataRepository {
	override val user: User? = null
	override val userData: UserWithData? = null

	override val classes: Flow<List<ElementEntity>> = flowOf(emptyList())
	override val teachers: Flow<List<ElementEntity>> = flowOf(emptyList())
	override val subjects: Flow<List<ElementEntity>> = flowOf(emptyList())
	override val rooms: Flow<List<ElementEntity>> = flowOf(emptyList())

	override val timetableElements: Flow<Map<ElementType, List<ElementEntity>>> = flowOf(emptyMap())

	override fun getElement(id: Long, type: ElementType): ElementEntity? = null
}

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class UntisMasterDataRepository @Inject constructor(
	private val userDao: UserDao,
	private val userRepository: UserRepository
) : MasterDataRepository {
	override val user: User?
		get() = (userRepository.userState.value as? UserRepository.UserState.User)?.user

	private val _userData = MutableStateFlow<UserWithData?>(null)
	override val userData: UserWithData?
		get() = _userData.value

	// TODO Delete userData and UserWithData and add flows for all required attributes

	private val userIdFlow = userRepository.userState
		.map { (it as? UserRepository.UserState.User)?.user?.id }
		.distinctUntilChanged()

	override val classes = userIdFlow
		.flatMapLatest { id ->
			id?.let { userDao.getActiveClassesFlow(it) } ?: flowOf(emptyList())
		}

	override val teachers = userIdFlow
		.flatMapLatest { id ->
			id?.let { userDao.getActiveTeachersFlow(it) } ?: flowOf(emptyList())
		}

	override val subjects = userIdFlow
		.flatMapLatest { id ->
			id?.let { userDao.getActiveSubjectsFlow(it) } ?: flowOf(emptyList())
		}

	override val rooms = userIdFlow
		.flatMapLatest { id ->
			id?.let { userDao.getActiveRoomsFlow(it) } ?: flowOf(emptyList())
		}

	override val timetableElements: Flow<Map<ElementType, List<ElementEntity>>> = combine(
		classes,
		teachers,
		subjects,
		rooms
	) { classes, teachers, subjects, rooms ->
		if (classes.isEmpty() && teachers.isEmpty() && subjects.isEmpty() && rooms.isEmpty())
			emptyMap()
		else
			mapOf(
				ElementType.CLASS to classes,
				ElementType.TEACHER to teachers,
				ElementType.SUBJECT to subjects,
				ElementType.ROOM to rooms
			)
	}

	override fun getElement(id: Long, type: ElementType): ElementEntity? {
		// TODO well, now this can only be called from a location where we have this state.
		//  This needs to be redesigned
		return when (type) {
			//ElementType.CLASS -> classes.value.firstOrNull { it.id == id }
			//ElementType.TEACHER -> teachers.value.firstOrNull { it.id == id }
			//ElementType.SUBJECT -> subjects.value.firstOrNull { it.id == id }
			//ElementType.ROOM -> rooms.value.firstOrNull { it.id == id }
			else -> null
		}
	}
}

val LocalMasterDataRepository = compositionLocalOf<MasterDataRepository> { DefaultMasterDataRepository() }
