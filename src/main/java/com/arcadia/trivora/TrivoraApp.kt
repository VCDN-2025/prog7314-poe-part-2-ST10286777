package com.arcadia.trivora

import android.app.Application

class TrivoraApp: Application()
{
    override fun onCreate() {
        super.onCreate()
        SharedPrefs.init(this)
    }
}