package com.sapuseven.untis.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

class ScaffoldWindowInsetsDetector : Detector(), Detector.UastScanner {
	override fun getApplicableMethodNames() = listOf("Scaffold")

	override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
		val param = method.parameterList.parameters.find { it.name == "contentWindowInsets" } ?: return
		val hasInsets = node.getArgumentForParameter(method.parameterList.getParameterIndex(param)) != null
		if (!hasInsets) {
			context.report(
				ISSUE,
				node,
				context.getLocation(node),
				"Scaffold must explicitly specify contentWindowInsets for edge-to-edge correctness"
			)
		}
	}

	companion object {
		val ISSUE = Issue.create(
			id = "ScaffoldWindowInsets",
			briefDescription = "Missing contentWindowInsets",
			explanation = "Explicitly set `contentWindowInsets = WindowInsets.None` when you handle insets manually, or `WindowInsets.safeDrawing` if you want safe drawing insets (the default).",
			category = Category.CORRECTNESS,
			priority = 6,
			severity = Severity.ERROR,
			implementation = Implementation(
				ScaffoldWindowInsetsDetector::class.java, Scope.JAVA_FILE_SCOPE
			)
		)
	}
}
