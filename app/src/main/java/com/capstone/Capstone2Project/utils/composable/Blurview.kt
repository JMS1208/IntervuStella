package com.capstone.Capstone2Project.utils.composable

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat.setLayerType
import com.capstone.Capstone2Project.PreviewActivity
import com.capstone.Capstone2Project.R
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur

@Preview(showBackground = true)
@Composable
fun BlurPreview() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        BlurLayout(blurRadius = 125f,
            content = {
                Text("텍스트", color = Black)
            },
            backgroundContent = {
                Image(
                    painter = painterResource(id = R.drawable.scene),
                    contentDescription = null
                )
            }
        )
    }

}

@Composable
fun BlurLayout(
    blurRadius: Float,
    content: @Composable () -> Unit,
    backgroundContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    AndroidView(
        factory = {
            View.inflate(it, R.layout.compose_blur, null)
        },
        modifier = modifier,
        update = {
            val blurView = it.findViewById<BlurView>(R.id.blur_view)

            val root = it.findViewById<ViewGroup>(R.id.root)

            val decorView = getActivityDecorView(context)

            val windowBackground = decorView?.background

            blurView.setupWith(root, getBlurAlgorithm(context)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(blurRadius)

            val composeView = it.findViewById<ComposeView>(R.id.compose_view)

            val backgroundComposeView = it.findViewById<ComposeView>(R.id.background_compose_view)

            composeView.apply {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                setContent {
                    content()
                }
            }

            backgroundComposeView.apply {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                setContent {
                    backgroundContent()
                }
            }

        }
    )


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

