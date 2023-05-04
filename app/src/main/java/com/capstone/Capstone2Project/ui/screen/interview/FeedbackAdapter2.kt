package com.capstone.Capstone2Project.ui.screen.interview

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.setLayerType
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.FeedbackItem
import com.capstone.Capstone2Project.databinding.FeedbackItemBinding
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur

class FeedbackAdapter2 : ListAdapter<FeedbackItem, FeedbackAdapter2.ViewHolder>(DiffCallback) {

    private var onItemClickListener: ((FeedbackItem) -> Unit)? = null
    private var blurRadius: Float = 5f
    private var blurOverlayColor: Int = R.color.blurOverlayColor

    private var composeView: @Composable (FeedbackItem)->Unit = {}

    fun setOnItemClickListener(listener: (FeedbackItem) -> Unit) {
        onItemClickListener = listener
    }

    fun setBlurRadius(blurRadius: Float) {
        this.blurRadius = blurRadius
    }

    fun setBlurOverlayColor(blurOverlayColor: Int) {
        this.blurOverlayColor = blurOverlayColor
    }

    fun setInnerComposeView(composeView: @Composable (FeedbackItem)->Unit ) {
        this.composeView = composeView
    }

    inner class ViewHolder(private val itemBinding: FeedbackItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(feedbackItem: FeedbackItem) {

            itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(feedbackItem)
                }
            }

            val decorView = getActivityDecorView(itemView.context)

            val windowBackground = decorView?.background

            itemBinding.blurView.setupWith(itemBinding.root, getBlurAlgorithm(itemView.context))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(blurRadius)
                .setOverlayColor(
                    ContextCompat.getColor(itemView.context, blurOverlayColor)
                )

            itemBinding.blurView.apply {
                outlineProvider = ViewOutlineProvider.BACKGROUND
                clipToOutline = true
            }

            itemBinding.composeView.apply {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                setContent {
                    composeView(feedbackItem)
                }
            }

        }

    }

    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<FeedbackItem>() {
            override fun areItemsTheSame(oldItem: FeedbackItem, newItem: FeedbackItem): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: FeedbackItem, newItem: FeedbackItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            FeedbackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedbackItem = currentList[position]
        holder.bind(feedbackItem)
    }

    private fun getBlurAlgorithm(context: Context): BlurAlgorithm {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffectBlur()
        } else {
            RenderScriptBlur(context)
        }
    }

    private fun getActivityDecorView(context: Context): View? {
        var ctx = context
        var i = 0
        while (i < 4 && ctx !is Activity && ctx is ContextWrapper) {
            ctx = ctx.baseContext
            i++
        }
        return if (ctx is Activity) {
            ctx.window.decorView
        } else {
            null
        }
    }

}