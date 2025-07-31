package com.sapuseven.untis.feature.login.schoolsearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sapuseven.untis.core.model.School
import com.sapuseven.untis.core.ui.R
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class, ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SchoolSearchResults(
	modifier: Modifier,
	query: String,
	onSchoolSelected: (School) -> Unit,
	viewModel: SchoolSearchViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(query) {
		snapshotFlow { query }
			.debounce(300)
			.distinctUntilChanged()
			.collect { input ->
				viewModel.searchSchools(input)
			}
	}

	if (uiState.results.isNotEmpty()) LazyColumn(modifier) {
		items(uiState.results) {
			ListItem(
				headlineContent = { Text(it.displayName) },
				supportingContent = it.address?.let { address -> { Text(address) } },
				modifier = Modifier.clickable { onSchoolSelected(it) }
			)
		}
	}
	else {
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = modifier
		) {
			if (uiState.isLoading) CircularProgressIndicator()
			else if (uiState.errorResId != null) Text(text = stringResource(id = uiState.errorResId!!))
			else if (uiState.errorRaw != null) Text(text = uiState.errorRaw!!)
			else if (uiState.results.isEmpty()) Text(text = stringResource(id = R.string.login_no_results))
		}
	}
}
