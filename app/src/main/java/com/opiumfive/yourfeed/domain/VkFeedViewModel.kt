package com.opiumfive.yourfeed.domain

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.vk.sdk.api.model.VKApiNews
import com.vk.sdk.api.model.VKApiNewsFeedArray

class VkFeedViewModel(private val repo: VkFeedRepo) : ViewModel() {

    private var currentNextFrom = ""

    val currentList = VKApiNewsFeedArray()
    val newsFeed = MutableLiveData<VKApiNewsFeedArray>()
    val likeData = MutableLiveData<Boolean>()

    fun retrieveFeed() {
        repo.loadFeed(currentNextFrom) {
            currentNextFrom = it?.nextFrom ?: ""
            if (it != null) {
                currentList.addAll(it)
                newsFeed.value = it
            } else {
                newsFeed.value = VKApiNewsFeedArray()
            }
        }
    }

    fun like(item: VKApiNews?) = repo.like(item) {
        likeData.value = it
    }

    fun dislike(item: VKApiNews?) = repo.dislike(item) {
        likeData.value = it
    }
}