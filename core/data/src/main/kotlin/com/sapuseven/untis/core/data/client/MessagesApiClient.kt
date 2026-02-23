package com.sapuseven.untis.core.data.client

import com.sapuseven.untis.core.api.rest.client.MessagesApi
import com.sapuseven.untis.core.domain.repository.UserRepository
import javax.inject.Inject

class UntisMessagesRestApiClient @Inject constructor(
	userRepository: UserRepository
) : MessagesApi(
	baseUrl = userRepository.getActiveUser().school.api.rest
) {
}
