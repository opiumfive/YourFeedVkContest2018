package com.opiumfive.yourfeed.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.opiumfive.yourfeed.BuildConfig
import com.opiumfive.yourfeed.LEFT_ITEMS_TO_INIT_LOAD
import com.opiumfive.yourfeed.NEWS_ITEMS_PER_PAGE
import com.opiumfive.yourfeed.R
import com.opiumfive.yourfeed.domain.VkFeedViewModel
import com.opiumfive.yourfeed.utils.DateFormatter
import com.opiumfive.yourfeed.utils.ext.hideSmooth
import com.opiumfive.yourfeed.utils.ext.launchAnim
import com.opiumfive.yourfeed.utils.ext.visible
import com.opiumfive.yourfeed.utils.recyclerHelpers.*
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKScope.WALL
import com.vk.sdk.VKSdk
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKApiNews
import com.vk.sdk.api.model.VKApiNewsFeedArray
import com.vk.sdk.util.VKUtil
import kotlinx.android.synthetic.main.activity_vk_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class VkFeedActivity : AppCompatActivity() {

    private val dateFormatter: DateFormatter by inject()
    val viewModel : VkFeedViewModel by viewModel()

    private var adapter: VkNewsAdapter? = null
    private var callback: ItemTouchHelperCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        setContentView(R.layout.activity_vk_main)

        val fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName())

        if (VKAccessToken.currentToken() == null) {
            VKSdk.login(this, WALL)
        } else {
            onReady()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken?) {
                onReady()
            }

            override fun onError(error: VKError?) {
                showAlert(getString(R.string.fail_auth))
            }
        })
    }

    private fun onReady() {
        initUI()

        // ViewModel observers
        viewModel.newsFeed.observe(this, object : Observer<VKApiNewsFeedArray> {
            override fun onChanged(t: VKApiNewsFeedArray?) {
                progress.hideSmooth()
                if (t != null && !t.isEmpty()) {
                    showUI(t)
                } else {
                    showAlert(getString(R.string.failed_load_feed))
                }
            }
        })

        viewModel.likeData.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                enableButtons(true)
                if (t == false && BuildConfig.DEBUG) {
                    showAlert(getString(R.string.failed_request))
                }
            }
        })

        if (viewModel.currentList.isEmpty().not()) {
            showUI(viewModel.currentList)
        } else {
            loadFeed()
        }
    }

    private fun initUI() {
        adapter = VkNewsAdapter(dateFormatter) {
            callback?.enabled = false
            bottomShadow.visible(View.VISIBLE)
        }

        newsRecycler.adapter = adapter

        // add side taps detector
        newsRecycler.addOnItemTouchListener(RecyclerItemTouchListener(this) {
            adapter?.onTouchedSide(it)
        })

        callback = ItemTouchHelperCallback(
            adapter,
            object : OnSlideListener {
                override fun onSliding(vh: RecyclerView.ViewHolder, ratio: Float, direction: Int) =
                    when (direction) {
                        // skip and like hints appearing animation
                        //TODO wrap magic code
                        SLIDING_LEFT -> when {
                            ratio < -0.3f -> {
                                skipHint.rotation = ratio * DEFAULT_ROTATE_DEGREE + 4f
                                skipHint.alpha = if (ratio < -0.5f) 1.0f else 1.0f - Math.abs(0.5f + ratio) / 0.2f
                            }
                            else -> skipHint.alpha = 0.0f
                        }
                        SLIDING_RIGHT -> when {
                            ratio > 0.3f -> {
                                likeHint.rotation = ratio * DEFAULT_ROTATE_DEGREE - 4f
                                likeHint.alpha = if (ratio > 0.5f) 1.0f else 1.0f - Math.abs(0.5f - ratio) / 0.2f
                            }
                            else -> likeHint.alpha = 0.0f
                        }
                        else -> Unit
                    }

                override fun onSlided(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val layoutPosition = viewHolder.layoutPosition
                    val item = adapter?.getCurrentItem(layoutPosition)
                    when (direction) {
                        SLIDED_LEFT -> {
                            dislike.launchAnim(R.anim.pressing)
                            dislike(item)
                        }
                        SLIDED_RIGHT -> {
                            like.launchAnim(R.anim.pressing)
                            like(item)
                        }
                        else -> Unit
                    }
                    //remove skipped item
                    adapter?.removeAt(layoutPosition)
                    callback?.enabled = true
                    bottomShadow.visible(View.INVISIBLE)

                    if (adapter?.itemCount == LEFT_ITEMS_TO_INIT_LOAD) {
                        loadFeed()
                    }

                    likeHint.hideSmooth()
                    skipHint.hideSmooth()
                }
            })

        val itemTouchHelper = ItemTouchHelper(callback!!)
        val layoutManager = VkNewsLayoutManager(newsRecycler, itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(newsRecycler)
        newsRecycler.layoutManager = layoutManager

        like.setOnClickListener {
            like(adapter?.getCurrentItem(0))
            nextItem()
            it.launchAnim(R.anim.pressing)
        }

        dislike.setOnClickListener {
            dislike(adapter?.getCurrentItem(0))
            nextItem()
            it.launchAnim(R.anim.pressing)
        }
    }

    private fun enableButtons(enable: Boolean) {
        like.isEnabled = enable
        dislike.isEnabled = enable
    }

    private fun like(news: VKApiNews?) {
        viewModel.like(news)
    }

    private fun dislike(news: VKApiNews?) {
        viewModel.dislike(news)
    }

    private fun nextItem() {
        adapter?.removeSmooth(0)
        bottomShadow.visible(View.INVISIBLE)
        if (adapter?.itemCount == LEFT_ITEMS_TO_INIT_LOAD) {
            loadFeed()
        }
        callback?.enabled = true
        enableButtons(false)
    }

    private fun loadFeed() {
        viewModel.retrieveFeed()
    }

    fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.oops))
            .setMessage(message)
            .setNeutralButton(getString(android.R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.retry)) { dialog, _ ->
                loadFeed()
                progress.alpha = 1f
                dialog.dismiss()
            }
            .show()
    }

    private fun showUI(data: VKApiNewsFeedArray?) {
        if (data != null && !data.isEmpty()) {
            adapter?.addList(data)
        } else {
            showAlert(getString(R.string.empty_feed))
        }
    }
}
