package com.kaia.demo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Required [Application] class for Hilt DI
 */
@HiltAndroidApp
class KaiaApplication : Application() {
}