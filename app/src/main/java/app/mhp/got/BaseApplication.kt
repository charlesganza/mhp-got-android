package app.mhp.got

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * this important class instantiates the class that lives as long as the application is alive makes sure there will be only one instance of it
 * throughout the whole application lifetime
 */
@HiltAndroidApp
class BaseApplication : Application()
