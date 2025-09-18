package com.sapuseven.untis.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API

@Suppress("UnstableApiUsage")
class CustomLintRegistry : IssueRegistry() {
	override val issues =
		listOf(
			ScaffoldWindowInsetsDetector.ISSUE
		)

	override val api: Int = CURRENT_API

	override val minApi: Int = 6

	override val vendor: Vendor? = Vendor(
		vendorName = "SapuSeven",
		identifier = "com.sapuseven.untis",
	)
}
