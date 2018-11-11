package com.opiumfive.yourfeed.utils.views

import android.content.Context
import android.graphics.Rect
import android.support.annotation.AttrRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.opiumfive.yourfeed.R
import com.opiumfive.yourfeed.utils.ext.dp2px
import com.opiumfive.yourfeed.utils.ext.postIfNoWidth
import kotlinx.android.synthetic.main.view_indicator.view.*


class ProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var recycler: RecyclerView? = null
    private var adapter: DotsAdapter? = null
    private var count: Int = 1
    private var initPos: Int = 0

    init {
        recycler = RecyclerView(context)
        recycler?.overScrollMode = View.OVER_SCROLL_NEVER
        recycler?.addItemDecoration(RecyclerItemDecorator(leftOffset = context.dp2px(2)))
        recycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        addView(recycler)
    }

    fun initIndicator(count: Int, initPos: Int = 0) {
        this.count = count
        this.initPos = initPos
        val margins = context.dp2px(2) * (count - 1)
        // if width is 0 then post code else launch without posting
        postIfNoWidth {
            adapter = DotsAdapter(count, initPos, (width - margins) / count)
            recycler?.adapter = adapter
        }
    }

    fun setPosition(pos: Int) {
        adapter?.currentPos = pos
    }
}

class DotsAdapter(private val count: Int, initPos: Int, private val itemWidth: Int) :
    RecyclerView.Adapter<DotsAdapter.ViewHolderImpl>() {

    var currentPos = initPos
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderImpl {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_indicator, parent, false)
        view.layoutParams.width = itemWidth
        return ViewHolderImpl(view)
    }

    override fun onBindViewHolder(holder: ViewHolderImpl, position: Int) = holder.bind(position <= currentPos)

    override fun getItemCount() = count

    inner class ViewHolderImpl(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(select: Boolean) {
            itemView.progressItem.isSelected = select
        }
    }
}

class RecyclerItemDecorator(private val leftOffset: Int = 0) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)
        outRect.set(if (position == 0) 0 else leftOffset, 0, 0, 0)
    }
}