package com.sapuseven.untis.core.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.sapuseven.untis.core.model.user.User
import com.sapuseven.untis.core.ui.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UserSelectorAction(
	users: List<User>,
	currentSelection: User? = null, // TODO: remove default and remove id below once not used anymore
	showProfileActions: Boolean = false,
	hideIfSingleProfile: Boolean = false,
	onSelectionChange: (User) -> Unit,
	onActionEdit: () -> Unit = {}
) {
	var expanded by remember { mutableStateOf(false) }

	if (!showProfileActions && hideIfSingleProfile && users.size <= 1) return

	IconButton(onClick = { expanded = true }, modifier = Modifier.testTag("action_users")) {
		Icon(
			imageVector = Icons.Outlined.AccountCircle,
			contentDescription = stringResource(id = R.string.core_ui_profiles_show)
		)
	}

	DropdownMenuPopup(expanded = expanded, onDismissRequest = { expanded = false }) {
		val groupInteractionSource = remember { MutableInteractionSource() }
		val extraActionCount = if (showProfileActions) 1 else 0
		val groupCount = 1 + extraActionCount.coerceAtMost(1)
		val itemCount = users.size + extraActionCount

		DropdownMenuGroup(
			shapes = MenuDefaults.groupShape(0, groupCount),
			interactionSource = groupInteractionSource,
			containerColor = MenuDefaults.groupVibrantContainerColor
		) {
			users.forEachIndexed { index, user ->
				DropdownMenuItem(
					text = { Text(user.displayName) },
					colors = MenuDefaults.selectableItemVibrantColors(),
					shapes = MenuDefaults.itemShape(index, itemCount),
					leadingIcon = null,
					checkedLeadingIcon = {
						Icon(
							Icons.Outlined.Check,
							contentDescription = null
						)
					},
					checked = currentSelection?.id == user.id,
					onCheckedChange = {
						expanded = false
						onSelectionChange(user)
					}
				)
			}
		}

		if (showProfileActions) {
			DropdownMenuSpacer()
			DropdownMenuGroup(
				shapes = MenuDefaults.groupShape(1, groupCount),
				interactionSource = groupInteractionSource,
				containerColor = MenuDefaults.groupVibrantContainerColor
			) {
				DropdownMenuItem(
					text = { Text(stringResource(id = R.string.core_ui_userlist)) },
					colors = MenuDefaults.selectableItemVibrantColors(),
					shapes = MenuDefaults.itemShape(users.size + 0, itemCount),
					selected = false,
					leadingIcon = {
						Icon(
							Icons.Outlined.Edit,
							contentDescription = null
						)
					},
					onClick = {
						expanded = false
						onActionEdit()
					}
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DropdownMenuSpacer() {
	Spacer(Modifier.height(MenuDefaults.GroupSpacing))
}
