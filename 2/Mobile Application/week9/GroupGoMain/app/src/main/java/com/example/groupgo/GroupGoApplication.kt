package com.example.groupgo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class GroupGoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Configure osmdroid cache directory and user agent
        val config = Configuration.getInstance()
        config.userAgentValue = "GroupGo/1.0 (Android; com.example.groupgo)"
        config.osmdroidTileCache = cacheDir
    }
}
