package com.opiumfive.yourfeed.domain

import com.opiumfive.yourfeed.NEWS_ITEMS_PER_PAGE
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKApiNews
import com.vk.sdk.api.model.VKApiNewsFeedArray

class VkFeedRepo {

    fun loadFeed(startFrom: String?, success: (VKApiNewsFeedArray?) -> Unit) {
        val newsFeedRequest = VKApi.newsFeed().getDiscover(
            VKParameters.from(
                "count", NEWS_ITEMS_PER_PAGE,
                "start_from", startFrom,
                "extended", 1
            )
        )

        newsFeedRequest.executeWithListener(object : VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse?) = success.invoke(response?.parsedModel as VKApiNewsFeedArray)

            override fun onError(error: VKError?) = success.invoke(null)
        })
    }

    fun like(news: VKApiNews?, success: (Boolean) -> Unit) {
        val likeRequest = VKApi.likes().add(
            VKParameters.from(
                "type", news?.type,
                "owner_id", if (news?.source_id != 0L) news?.source_id else news.signer_id,
                "item_id", news?.post_id
            )
        )

        likeRequest.executeWithListener(object : VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse?) = success.invoke(true)
            override fun onError(error: VKError?) = success.invoke(false)
        })
    }

    fun dislike(news: VKApiNews?, success: (Boolean) -> Unit) {
        val dislikeRequest = VKApi.newsFeed().ignoreItem(
            VKParameters.from(
                "type", "wall",
                "owner_id", if (news?.source_id != 0L) news?.source_id else news.signer_id,
                "item_id", news?.post_id
            )
        )

        dislikeRequest.executeWithListener(object : VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse?) = success.invoke(true)
            override fun onError(error: VKError?) = success.invoke(false)
        })
    }
}