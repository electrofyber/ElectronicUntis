package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.database.dao.UserDao
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.domain.repository.ElementRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.ElementType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultElementRepository : ElementRepository {
	override val classes: Flow<List<Element>> = flowOf(emptyList())
	override val teachers: Flow<List<Element>> = flowOf(emptyList())
	override val subjects: Flow<List<Element>> = flowOf(emptyList())
	override val rooms: Flow<List<Element>> = flowOf(emptyList())

	override val timetableElements: Flow<Map<ElementType, List<Element>>> = flowOf(emptyMap())

	override suspend fun getElement(key: ElementKey): Element? = null

	override suspend fun getAllElements(): List<Element> = emptyList()
}

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class UntisElementRepository @Inject constructor(
	private val userDao: UserDao,
	private val userRepository: UserRepository
) : ElementRepository {
	private val userIdFlow = userRepository.observeActiveUser()
		.map { it?.id }
		.distinctUntilChanged()

	override val classes = userIdFlow
		.flatMapLatest { id ->
			id?.let { userDao.getActiveClassesFlow(it).mapElements() } ?: emptyFlow()
		}

	override val teachers = userIdFlow
		.flatMapLatest { id ->
			id?.let { userDao.getActiveTeachersFlow(it).mapElements() } ?: flowOf(emptyList())
		}

	override val subjects = userIdFlow
		.flatMapLatest { id ->
			id?.let { userDao.getActiveSubjectsFlow(it).mapElements() } ?: flowOf(emptyList())
		}

	override val rooms = userIdFlow
		.flatMapLatest { id ->
			id?.let { userDao.getActiveRoomsFlow(it).mapElements() } ?: flowOf(emptyList())
		}

	override val timetableElements: Flow<Map<ElementType, List<Element>>> = combine(
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

	override suspend fun getElement(key: ElementKey): Element? {
		val userId = userRepository.getActiveUser().id
		return when (key.type) {
			ElementType.CLASS -> userDao.getClassById(userId, key.id)
			ElementType.TEACHER -> userDao.getTeacherById(userId, key.id)
			ElementType.SUBJECT -> userDao.getSubjectById(userId, key.id)
			ElementType.ROOM -> userDao.getRoomById(userId, key.id)
			ElementType.STUDENT -> null
		}?.toDomain()
	}

	override suspend fun getAllElements(): List<Element> =
		classes.first() + teachers.first() + subjects.first() + rooms.first()

	private fun Flow<List<ElementEntity>>.mapElements() = map { it.map(ElementEntity::toDomain) }
}
