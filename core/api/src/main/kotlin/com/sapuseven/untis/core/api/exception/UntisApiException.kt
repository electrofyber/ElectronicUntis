package com.sapuseven.untis.core.api.exception

import com.sapuseven.untis.core.api.model.response.Error

class UntisApiException(val error: Error?) : Throwable(error?.message) {
}
