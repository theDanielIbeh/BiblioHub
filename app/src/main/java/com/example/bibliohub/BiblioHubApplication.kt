package com.example.bibliohub

import android.app.Application
import com.example.bibliohub.data.AppContainer
import com.example.bibliohub.data.DefaultAppContainer

class BiblioHubApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}