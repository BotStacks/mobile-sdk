/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sample

import android.app.Application
import ai.botstacks.sdk.BotStacksChat

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        BotStacksChat.shared.setup(
            context = this,
            apiKey = getString(R.string.botstacks_api_key),
            giphyApiKey = getString(R.string.giphy_api_key),
            googleMapsApiKey = getString(R.string.google_maps_api_key)
        )
    }

    companion object {
        lateinit var app: App
    }

}