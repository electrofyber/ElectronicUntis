package com.sapuseven.untis.core.api.mobile.client.jsonrpc

import com.sapuseven.untis.core.api.mobile.model.request.RequestData
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class JsonrpcApiClient() {
	private lateinit var client: HttpClient

	constructor(
		httpClientEngine: HttpClientEngine,
		httpClientConfig: ((HttpClientConfig<*>) -> Unit)
	) : this(
		HttpClient(httpClientEngine, httpClientConfig)
	)

	constructor(
		httpClientEngineFactory: HttpClientEngineFactory<*>,
		httpClientConfig: ((HttpClientConfig<*>) -> Unit)
	) : this(httpClientEngineFactory.create(), httpClientConfig)

	constructor(
		httpClient: HttpClient
	) : this() {
		this.client = httpClient
	}

	protected suspend fun request(
		path: String, body: RequestData? = null
	): HttpResponse = withContext(Dispatchers.IO) {
		return@withContext client.post(path) {
			contentType(ContentType.Application.Json)
			setBody(body)
		}
	}

	companion object {
		const val DEFAULT_SCHOOLSEARCH_URL = "https://schoolsearch.webuntis.com/schoolquery2"

		const val METHOD_CREATE_IMMEDIATE_ABSENCE = "createImmediateAbsence2017"
		const val METHOD_DELETE_ABSENCE = "deleteAbsence2017"
		const val METHOD_GET_ABSENCES = "getStudentAbsences2017"
		const val METHOD_GET_APP_SHARED_SECRET = "getAppSharedSecret"
		const val METHOD_GET_AUTH_TOKEN = "getAuthToken"
		const val METHOD_GET_EXAMS = "getExams2017"
		const val METHOD_GET_HOMEWORK = "getHomeWork2017"
		const val METHOD_GET_MESSAGES = "getMessagesOfDay2017"
		const val METHOD_GET_OFFICEHOURS = "getOfficeHours2017"
		const val METHOD_GET_PERIOD_DATA = "getPeriodData2017"
		const val METHOD_GET_TIMETABLE = "getTimetable2017"
		const val METHOD_GET_USER_DATA = "getUserData2017"
		const val METHOD_SEARCH_SCHOOLS = "searchSchool"
		const val METHOD_SUBMIT_ABSENCES_CHECKED = "submitAbsencesChecked2017"
		const val METHOD_GET_LESSON_TOPIC = "getLessonTopic2017"
		const val METHOD_SUBMIT_LESSON_TOPIC = "submitLessonTopic"
	}
}
