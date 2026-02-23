package com.sapuseven.untis.core.data.mapper

import android.net.Uri
import androidx.core.net.toUri
import com.sapuseven.untis.core.api.mobile.model.untis.SchoolInfo
import com.sapuseven.untis.core.model.timetable.School
import com.sapuseven.untis.core.model.timetable.SchoolApi

internal fun SchoolInfo.toDomain(apiUrlOverride: String? = null) = School(
	name = loginName,
	displayName = displayName,
	address = address,
	api = SchoolApi(
		base = apiUrlOverride ?: baseApiUrl().toString(), // apiUrlOverride replaces base api url
		jsonRpc = apiUrlOverride ?: baseApiUrl().jsonRpc().toString(), // apiUrlOverride replaces jsonRpc api url
		rest = (apiUrlOverride?.toUri() ?: baseApiUrl()).rest().toString(), // apiUrlOverride replaces base url of rest api
		restAuth = (apiUrlOverride?.toUri() ?: baseApiUrl()).restAuth().toString() // apiUrlOverride replaces base url of rest auth api
	)
)

internal fun School.toEntity(): SchoolInfo = SchoolInfo(
	server = api.base.toUri().host ?: "webuntis.com",
	useMobileServiceUrlAndroid = false,
	useMobileServiceUrlIos = false,
	address = "",
	displayName = displayName,
	loginName = name,
	schoolId = 0L, // This is not used in the app, so we can set it to 0
	tenantId = null, // Not used in the app
	serverUrl = api.base,
	mobileServiceUrl = null // Not used in the app
)

private fun SchoolInfo.baseApiUrl(): Uri {
	return mobileServiceUrl?.takeIf { useMobileServiceUrlAndroid && it.isNotBlank() }?.let {
		Uri.Builder()
			.scheme("https")
			.authority(it.toUri().host)
			.appendPath("WebUntis")
			.appendQueryParameter("school", loginName)
			.build()
	} ?: serverUrl.toUri()
}

private fun Uri.jsonRpc(): Uri = buildUpon()
	.appendEncodedPath("jsonrpc_intern.do")
	.build()

private fun Uri.rest(): Uri = buildUpon()
	.appendEncodedPath("api/rest")
	.build()

private fun Uri.restAuth(): Uri = buildUpon()
	.appendEncodedPath("api/mobile/v2")
	.build()
