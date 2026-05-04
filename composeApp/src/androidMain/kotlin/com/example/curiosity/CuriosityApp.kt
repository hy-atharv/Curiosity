package com.example.curiosity

import android.app.Application
import com.example.curiosity.di.initKoin
import org.koin.android.ext.koin.androidContext

class CuriosityApp: Application() {

    override fun onCreate(){
        super.onCreate()
        initKoin {
            androidContext(this@CuriosityApp)
        }
    }
}
