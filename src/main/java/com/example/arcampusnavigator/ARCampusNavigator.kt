// ARCampusNavigatorApp.kt
package com.example.arcampusnavigator

import android.app.Application
import com.example.arcampusnavigator.di.appModule
import com.example.arcampusnavigator.di.repositoryModule
import com.example.arcampusnavigator.di.viewModelModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ARCampusNavigatorApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Koin for dependency injection
        startKoin {
            androidContext(this@ARCampusNavigatorApp)
            modules(listOf(appModule, repositoryModule, viewModelModule))
        }
    }
}

