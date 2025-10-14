package com.orbit

import android.app.Application
import com.orbit.data.database.DatabaseInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class OrbitApplication : Application() {
    
    @Inject
    lateinit var databaseInitializer: DatabaseInitializer
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize database with sample data
        applicationScope.launch {
            databaseInitializer.initializeDatabase()
        }
    }
}
