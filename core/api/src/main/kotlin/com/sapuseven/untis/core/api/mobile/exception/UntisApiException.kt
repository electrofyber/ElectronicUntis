package com.sapuseven.untis.core.api.mobile.exception

import com.sapuseven.untis.core.api.mobile.model.response.Error

class UntisApiException(val error: Error?) : Throwable(error?.message) {
}
