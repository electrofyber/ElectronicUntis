package com.sapuseven.untis.core.database.entity

import com.sapuseven.untis.core.model.ElementType

abstract class ElementEntity {
	abstract val id: Long
	abstract val userId: Long
	abstract val type: ElementType
	abstract val name: String
	abstract val foreColor: String?
	abstract val backColor: String?
	abstract val allowed: Boolean
	abstract val active: Boolean

	abstract fun getShortName(default: String = "?"): String
	abstract fun getLongName(default: String = "?"): String
}
