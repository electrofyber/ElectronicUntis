plugins {
	alias(libs.plugins.betteruntis.jvm.library)
}

dependencies {
	compileOnly(libs.lint.api)
	compileOnly(libs.lint.checks)
}

tasks.jar {
	manifest.attributes["Lint-Registry-v2"] = "com.sapuseven.untis.lint.CustomLintRegistry"
}
