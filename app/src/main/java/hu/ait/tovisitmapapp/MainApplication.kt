package hu.ait.tovisitmapapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Application class needs to be registered in the Manifest file!
// Creates stuff in the background that allows you to do dependency injection.
// Creates stuff in the java (generated) folder.
@HiltAndroidApp
class MainApplication : Application() {
}