package com.opiumfive.yourfeed.ui

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.SparseArray
import android.view.View
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.opiumfive.yourfeed.GlideApp
import com.opiumfive.yourfeed.R
import com.opiumfive.yourfeed.utils.DateFormatter
import com.opiumfive.yourfeed.utils.ext.dp2px
import com.opiumfive.yourfeed.utils.ext.screenWidthPx
import com.opiumfive.yourfeed.utils.recyclerHelpers.TOUCH_LEFT
import com.opiumfive.yourfeed.utils.recyclerHelpers.TOUCH_RIGHT
import com.vk.sdk.api.model.*
import kotlinx.android.synthetic.main.item_news.view.*

class ViewHolder(
    itemView: View,
    private val dateFormatter: DateFormatter,
    private val profilesMap: SparseArray<VKApiUserFull>,
    private val groupMap: SparseArray<VKApiCommunityFull>,
    private val listener: (VKApiNews?) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private var currentAttachment = 0
    private var attachments: VKAttachments? = null
    private var needToGoneMoreButton = true

    fun bind(newsItem: VKApiNews) {
        currentAttachment = 0
        itemView.text.text = newsItem.text
        itemView.date.text = dateFormatter.format(newsItem.date)

        val screenWidth = itemView.context.screenWidthPx

        var profileImage = ""
        var profileName = ""

        var profile = profilesMap.get(Math.abs(newsItem.signer_id).toInt())
        var group = groupMap.get(Math.abs(newsItem.source_id).toInt())

        if (profile == null) {
            profile = profilesMap.get(Math.abs(newsItem.source_id).toInt())
        }
        if (group == null) {
            group = groupMap.get(Math.abs(newsItem.signer_id).toInt())
        }

        val imageSize = itemView.context.dp2px(screenWidth)

        if (profile != null) {
            profileName = with(profile) {
                "$first_name $last_name"
            }
            profileImage = profile.photo.getImageForDimension(imageSize, imageSize)
        }
        if (group != null) {
            profileName = group.name
            profileImage = group.photo.getImageForDimension(imageSize, imageSize)
        }

        GlideApp.with(itemView).load(profileImage).centerCrop().circleCrop().into(itemView.avatar)

        itemView.name.text = profileName

        val filteredAttachments = VKAttachments()
        newsItem.attachments.forEach {
            var needThisAttachment = false
            when (it?.type) {
                VKAttachments.TYPE_PHOTO -> {
                    val link = (it as VKApiPhoto).src.getImageForDimension(screenWidth, screenWidth)
                    needThisAttachment = link.isNullOrEmpty().not()
                }
                VKAttachments.TYPE_ALBUM -> {
                    val link = (it as VKApiPhotoAlbum).photo.getImageForDimension(screenWidth, screenWidth)
                    needThisAttachment = link.isNullOrEmpty().not()
                }
                VKAttachments.TYPE_VIDEO -> {
                    val link = (it as VKApiVideo).photo.getImageForDimension(screenWidth, screenWidth)
                    needThisAttachment = link.isNullOrEmpty().not()
                }
                VKAttachments.TYPE_LINK -> {
                    val link = (it as VKApiLink).image_src
                    needThisAttachment = link.isNullOrEmpty().not()
                }
            }
            if (needThisAttachment) {
                filteredAttachments.add(it)
            }
        }
        attachments = filteredAttachments
        if (attachments?.isEmpty() == false) {
            itemView.progress.initIndicator(attachments?.size ?: 0)
            itemView.progress.visibility = View.VISIBLE

            showAttachment(attachments?.get(currentAttachment))
        } else {
            // no fit attaches
            itemView.progress.visibility = View.INVISIBLE
        }

        itemView.text.maxLines = 2
        itemView.more.visibility = if (TextUtils.isEmpty(newsItem.text)) View.INVISIBLE else View.VISIBLE

        itemView.text.post {
            needToGoneMoreButton = itemView.text.layout.getEllipsisCount(itemView.text.layout.lineCount) > 0
        }

        itemView.more.setOnClickListener {
            itemView.more.visibility = if (needToGoneMoreButton) View.GONE else View.INVISIBLE
            itemView.text.maxLines = Integer.MAX_VALUE
            listener.invoke(newsItem)
        }
    }

    fun onSideTap(side: Int) {
        when (side) {
            TOUCH_LEFT -> {
                if (currentAttachment > 0) {
                    itemView.progress.setPosition(--currentAttachment)
                    showAttachment(attachments?.get(currentAttachment))
                }
            }
            TOUCH_RIGHT -> {
                if (currentAttachment < (attachments?.size ?: 0) - 1) {
                    itemView.progress.setPosition(++currentAttachment)
                    showAttachment(attachments?.get(currentAttachment))
                }
            }
        }
    }

    private fun showAttachment(attach: VKAttachments.VKApiAttachment?) {
        val screenWidth = itemView.context.screenWidthPx

        val link = when (attach?.type) {
            VKAttachments.TYPE_PHOTO -> (attach as VKApiPhoto).src.getImageForDimension(screenWidth, screenWidth)
            VKAttachments.TYPE_ALBUM -> (attach as VKApiPhotoAlbum).photo.getImageForDimension(screenWidth, screenWidth)
            VKAttachments.TYPE_VIDEO -> (attach as VKApiVideo).photo.getImageForDimension(screenWidth, screenWidth)
            VKAttachments.TYPE_LINK -> (attach as VKApiLink).image_src
            else -> ""
        }

        GlideApp.with(itemView)
            .load(if (!TextUtils.isEmpty(link)) link else R.drawable.camera_200)
            .error(R.drawable.camera_200)
            .transition(DrawableTransitionOptions.withCrossFade(100))
            .into(itemView.image)
    }
}