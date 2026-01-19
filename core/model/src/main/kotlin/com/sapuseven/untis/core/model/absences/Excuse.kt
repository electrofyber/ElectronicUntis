package com.sapuseven.untis.core.model.absences

import kotlinx.datetime.LocalDate

/**
 * Represents a standardized excuse or justification for an absence.
 *
 * In contrast to [Absence.absenceReason] (which contains the reason for the absence itself),
 * an [Excuse] can render the absence as excused or unexcused
 * and is typically set by the teacher or school administration.
 *
 * An excuse can exist either as a standalone entity or be associated with a specific absence.
 */
data class Excuse(
	/**
	 * The unique identifier of the excuse.
	 */
	val id: Long,

	/**
	 * The display text of the excuse.
	 */
	val text: String?,

	/**
	 * Whether or not this excuse indicates an excused absence.
	 * For example, an excuse like "Sick" would typically have this set to true,
	 * while an excuse like "Truancy" would have it set to false.
	 */
	val excused: Boolean,

	/**
	 * Whether or not this excuse is currently active and can be applied to absences.
	 * When inactive, the excuse should not be used for new absences.
	 */
	val active: Boolean,

	/**
	 * The date when this excuse was applied to an associated absence, if available.
	 * Typically exists for instances of [Absence.excuse].
	 */
	val excusedDate: LocalDate? = null
)
