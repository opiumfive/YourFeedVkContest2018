package com.opiumfive.yourfeed

import android.app.Application
import com.opiumfive.yourfeed.domain.VkFeedRepo
import com.opiumfive.yourfeed.domain.VkFeedViewModel
import com.opiumfive.yourfeed.utils.DateFormatter
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

class VkContestApp : Application() {

    val appModule: Module = module {
        // need formatter as a singleton
        single { DateFormatter(androidContext()) }
        viewModel { VkFeedViewModel(get()) }
        single { VkFeedRepo() }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))

        val tokenTracker = object : VKAccessTokenTracker() {
            override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
                // stub
            }
        }
        tokenTracker.startTracking()

        VKSdk.initialize(this)
    }
}