package com.yummizi.app

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import dagger.hilt.android.HiltAndroidApp
import com.amplifyframework.kotlin.core.Amplify

@HiltAndroidApp
class YummiziApp : Application(){
    // Global initializations like analytics trackers, crash reporting (Timber/Firebase),
    // or dark mode configurations go here later.

    override fun onCreate() {
        super.onCreate()
        initAmplify()
    }

    private fun initAmplify() {
        try {
            // 1. Add the Cognito Auth Plugin to Amplify's plugin registry
            Amplify.addPlugin(AWSCognitoAuthPlugin())

            // 2. Reads 'amplifyconfiguration.json' from res/raw automatically
            Amplify.configure(applicationContext)

            Log.i("MainApplication", "🗝️✅  Amplify successfully initialized")
        } catch (error: AmplifyException) {
            Log.e("MainApplication", "🗝️🚩  Could not initialize Amplify", error)
        }
    }
}
