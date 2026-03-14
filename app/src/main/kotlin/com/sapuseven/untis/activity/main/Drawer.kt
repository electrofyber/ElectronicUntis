package com.sapuseven.untis.activity.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.model.timetable.ElementType


@Composable
fun DrawerItems(
	disableTypeSelection: Boolean = false,
	displayedElement: ElementEntity? = null,
	onTimetableClick: (item: NavItemTimetable) -> Unit,
	onNavigationClick: (item: NavItemNavigation) -> Unit,
) {
	// All element type navigation (Classes, Teachers, Rooms) has been removed.

	DrawerDivider()
}

@Composable
fun DrawerDivider() {
	HorizontalDivider(
		color = MaterialTheme.colorScheme.outline,
		modifier = Modifier.padding(vertical = 8.dp)
	)
}

@Composable
fun DrawerText(text: String) {
	Text(
		text = text,
		style = MaterialTheme.typography.labelMedium,
		modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
	)
}

open class NavItem(
	open val icon: Painter,
	open val label: String
)

data class NavItemTimetable(
	override val icon: Painter,
	override val label: String,
	val elementType: ElementType
) : NavItem(icon, label)

data class NavItemNavigation(
	override val icon: Painter,
	override val label: String,
	val route: Any
) : NavItem(icon, label)
