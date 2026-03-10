package com.sapuseven.untis.core.domain.repository

import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.ElementType
import kotlinx.coroutines.flow.Flow

interface ElementRepository {
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
	val classes: Flow<List<Element>>

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
	val teachers: Flow<List<Element>>

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
	val subjects: Flow<List<Element>>

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
	val rooms: Flow<List<Element>>

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
	val timetableElements: Flow<Map<ElementType, List<Element>>>

	/**
	 * This function retrieves a specific element by its ID and type for the active user.
	 *
	 * If the element does not exist, it will return `null`.
	 *
	 * @param key The key of the element to retrieve, containing its ID and type.
	 * @throws IllegalStateException If no user is active.
	 * @return The element with the specified ID and type, or `null` if not found or no user is active.
	 */
	suspend fun getElement(key: ElementKey): Element?

	/**
	 * This function retrieves all elements (classes, teachers, subjects, rooms) for the active user as a single list.
	 *
	 * This is a suspend function that fetches the latest values from the database and combines them into a single list.
	 * If no user is active, it will return an empty list.
	 *
	 * @return A list of all elements for the active user, or an empty list if no user is active.
	 */
	suspend fun getAllElements(): List<Element>
}
