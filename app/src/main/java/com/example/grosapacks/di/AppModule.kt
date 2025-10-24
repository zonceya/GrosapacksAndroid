package com.example.grosapacks.di

import com.google.gson.Gson
import com.example.grosapacks.data.local.PreferencesHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { Gson() }

    single { PreferencesHelper(androidContext()) } // Use androidContext() instead of get()
}