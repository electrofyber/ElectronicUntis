package com.sapuseven.untis

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.sapuseven.untis.core.datastore.GlobalSettingsDataSource
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {
	@Inject
	lateinit var globalSettings: GlobalSettingsDataSource;

	@Inject
	lateinit var workerFactory: HiltWorkerFactory;

	private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())

	@OptIn(DelicateCoroutinesApi::class)
	override fun onCreate() {
		super.onCreate()

		/*TODO ioScope.launch {
			val settings = globalSettings.getSettings().first()
			initSentry(
				settings.errorReportingEnable,
				settings.errorReportingEnableBreadcrumbs
			)
		}

		GlobalScope.launch {
			WorkManager.getInstance(applicationContext).apply {
				cancelAllWorkByTag(TAG_DAILY_WORK)
				enqueue(OneTimeWorkRequestBuilder<DailyWorker>().build())
			}
		}*/
	}

	override val workManagerConfiguration: Configuration
		get() = Configuration.Builder()
			.setMinimumLoggingLevel(Log.VERBOSE)
			.setWorkerFactory(workerFactory)
			.build()
	/**
	 * Return true if the application is debuggable.
	 */
	private fun isDebuggable(): Boolean {
		return 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
	}

	/**
	 * Set a thread policy that detects all potential problems on the main thread, such as network
	 * and disk access.
	 *
	 * If a problem is found, the offending call will be logged and the application will be killed.
	 */
	private fun setStrictModePolicy() {
		if (isDebuggable()) {
			StrictMode.setThreadPolicy(
				Builder().detectAll().penaltyLog().build(),
			)
		}
	}
}
