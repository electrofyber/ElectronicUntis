package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.database.entity.UserDao
import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMasterDataRepository : MasterDataRepository {
	override val classes: Flow<List<Element>> = flowOf(emptyList())
	override val teachers: Flow<List<Element>> = flowOf(emptyList())
	override val subjects: Flow<List<Element>> = flowOf(emptyList())
	override val rooms: Flow<List<Element>> = flowOf(emptyList())

	override val timetableElements: Flow<Map<ElementType, List<Element>>> = flowOf(emptyMap())

	override fun getElement(id: Long, type: ElementType): Element? = null
}

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class UntisMasterDataRepository @Inject constructor(
	private val userDao: UserDao,
	private val userRepository: UserRepository
) : MasterDataRepository {
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

	override fun getElement(id: Long, type: ElementType): Element? {
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

	private fun Flow<List<ElementEntity>>.mapElements() = map { it.map(ElementEntity::toDomain) }
}

//val LocalMasterDataRepository = compositionLocalOf<MasterDataRepository> { DefaultMasterDataRepository() }
