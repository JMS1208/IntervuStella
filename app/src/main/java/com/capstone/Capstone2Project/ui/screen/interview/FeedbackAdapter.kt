package com.capstone.Capstone2Project.ui.screen.interview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.setLayerType
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.FeedbackItem
import com.capstone.Capstone2Project.databinding.FeedbackItemBinding
import com.capstone.Capstone2Project.databinding.ItemFeedbackMotionBinding
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur

class FeedbackAdapter : ListAdapter<FeedbackItem, FeedbackAdapter.ViewHolder>(DiffCallback) {

    private var onItemClickListener: ((FeedbackItem) -> Unit)? = null

    private var composeView: @Composable (Int, FeedbackItem) -> Unit = {_,_->}
    private var pagerComposeView: @Composable () -> Unit = {}

    private val VIEW_TYPE_NORMAL = 0
    private val VIEW_TYPE_FOOTER = 1

    fun setOnItemClickListener(listener: (FeedbackItem) -> Unit) {
        onItemClickListener = listener
    }

    private var text = "텍스트"

    fun setInnerComposeView(composeView: @Composable (Int, FeedbackItem) -> Unit) {
        this.composeView = composeView
    }

    fun setPagerComposeView(composeView: @Composable ()->Unit) {
        this.pagerComposeView = composeView
    }

    inner class ViewHolder(
        private val itemBinding: ItemFeedbackMotionBinding,
        private val viewType: Int
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(index: Int, feedbackItem: FeedbackItem) {
            if(viewType == VIEW_TYPE_NORMAL) {
                itemView.setOnClickListener {
                    onItemClickListener?.let {
                        it(feedbackItem)
                    }
                }

                itemBinding.cvItemFeedback.apply {
                    setContent {
                        composeView(index, feedbackItem)
                    }
                }

            } else {
                itemBinding.cvItemFeedback.apply {
                    setContent {
                        pagerComposeView()
                    }
                }
                itemView.setOnTouchListener { v, event ->
                    return@setOnTouchListener false
                }
            }

        }

    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<FeedbackItem>() {
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
            ItemFeedbackMotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding, viewType)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(position < itemCount-1) {
            val feedbackItem = currentList[position]
            holder.bind(position, feedbackItem)
        } else {
            holder.bind(position, FeedbackItem.createTestFeedbackItem())
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            VIEW_TYPE_FOOTER
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

}