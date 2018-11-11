package com.opiumfive.yourfeed.ui

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import com.opiumfive.yourfeed.R
import com.opiumfive.yourfeed.utils.DateFormatter
import com.vk.sdk.api.model.VKApiCommunityFull
import com.vk.sdk.api.model.VKApiNews
import com.vk.sdk.api.model.VKApiNewsFeedArray
import com.vk.sdk.api.model.VKApiUserFull


class VkNewsAdapter(private val dateFormatter: DateFormatter, private val itemClick: (VKApiNews?) -> Unit) :
    RecyclerView.Adapter<ViewHolder>() {

    private var newsList = mutableListOf<VKApiNews>()
    // SparseArray is for a bit faster search, needed to move it to viewModel
    private var groupMap = SparseArray<VKApiCommunityFull>()
    private var profilesMap = SparseArray<VKApiUserFull>()
    private var currentViewHolder: ViewHolder? = null

    fun addList(data: VKApiNewsFeedArray) {
        val wasEmpty = newsList.size == 0
        newsList.addAll(data)
        data.profiles.forEach { profilesMap.put(it.id, it) }
        data.groups.forEach { groupMap.put(it.id, it) }

        if (wasEmpty) {
            notifyDataSetChanged()
        }
    }

    fun removeAt(pos: Int) {
        if (itemCount >= pos + 1) {
            newsList.removeAt(pos)
        }
    }

    fun getCurrentItem(pos: Int) = if (newsList.size > pos) newsList[pos] else null

    fun removeSmooth(pos: Int) {
        if (itemCount >= pos + 1) {
            newsList.removeAt(pos)
            notifyItemChanged(0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false),
            dateFormatter,
            profilesMap,
            groupMap,
            itemClick
        )

    private fun getItem(position: Int) = newsList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            currentViewHolder = holder
        }
        holder.bind(getItem(position))
    }

    override fun getItemCount() = newsList.size

    fun onTouchedSide(side: Int) = currentViewHolder?.onSideTap(side)
}