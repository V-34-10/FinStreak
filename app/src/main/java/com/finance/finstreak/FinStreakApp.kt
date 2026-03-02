package com.finance.finstreak

import android.app.Application
import com.finance.finstreak.di.appModules
import com.finance.finstreak.util.createNotificationChannels
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class FinStreakApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@FinStreakApp)
            modules(appModules)
        }

        createNotificationChannels(this)
    }
}
