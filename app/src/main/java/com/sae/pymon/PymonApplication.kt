package com.sae.pymon

import android.app.Application
import com.sae.pymon.data.AppContainer
import com.sae.pymon.data.DefaultAppContainer

class PymonApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}