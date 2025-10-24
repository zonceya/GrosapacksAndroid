package com.example.grosapacks

import android.app.Application
import com.example.grosapacks.di.appModule
import com.example.grosapacks.di.networkModule
import com.example.grosapacks.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GrosaPacksApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@GrosaPacksApplication)
            modules(
                listOf(
                    appModule,
                    viewModelModule,
                    networkModule
                )
            )
        }
    }
}
