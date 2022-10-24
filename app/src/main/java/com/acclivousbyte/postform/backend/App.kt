package com.acclivousbyte.postform.backend

import androidx.multidex.MultiDexApplication
import com.acclivousbyte.postform.koinDI.retrofitModule
import com.acclivousbyte.postform.koinDI.utilModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(retrofitModule, utilModule))
        }
    }
}