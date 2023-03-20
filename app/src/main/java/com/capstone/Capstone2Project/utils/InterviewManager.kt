package com.capstone.Capstone2Project.utils

import android.content.Context
import android.media.FaceDetector
import android.media.Image
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.runtime.compositionLocalOf
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.nio.ByteBuffer
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@androidx.annotation.OptIn(androidx.camera.camera2.interop.ExperimentalCamera2Interop::class)
class InterviewManager private constructor(private val builder: Builder) :
    LifecycleEventObserver {

    private var cameraExecutor: ExecutorService? = null


    init {
        getLifecycle().addObserver(this)
    }

    fun updatePreview(
        previewView: PreviewView,
        showPreview: Boolean
    ) {
        if(showPreview) {
            showPreview(previewView)
        } else {
            notShowPreview()
        }

    }

    fun showPreview(
        cameraPreview: PreviewView = getCameraPreview()
    ): PreviewView {

        startCamera(cameraPreview)
        return cameraPreview
    }

    private fun notShowPreview() {
        startWithOutCamera()
    }

    private fun getCameraPreview(): PreviewView = PreviewView(getContext()).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        keepScreenOn = true

    }


    fun shutDownCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(getContext())

        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        cameraProvider.unbindAll()
    }
    private fun startWithOutCamera() {
        val cameraExecutor = cameraExecutor ?: return

        val cameraProviderFuture = ProcessCameraProvider.getInstance(getContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()


            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        getImageAnalyzer()
                    )
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    getLifeCycleOwner(),
                    cameraSelector,
                    //preview,
                    imageAnalysis
                )

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "Use case binding failed", e)
            }


        }, ContextCompat.getMainExecutor(getContext()))
    }
    private fun startCamera(previewView: PreviewView) {

        val cameraExecutor = cameraExecutor ?: return

        val cameraProviderFuture = ProcessCameraProvider.getInstance(getContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }


            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        getImageAnalyzer()
                    )
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    getLifeCycleOwner(),
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "Use case binding failed", e)
            }


        }, ContextCompat.getMainExecutor(getContext()))



    }



    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                cameraExecutor = Executors.newSingleThreadExecutor()
            }
//            Lifecycle.Event.ON_START-> {
//                if (cameraExecutor == null) {
//                    cameraExecutor = Executors.newSingleThreadExecutor()
//                }
//            }
//
//            Lifecycle.Event.ON_STOP-> {
//                Log.e(TAG, "onStateChanged: 테스트!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", )
////                cameraExecutor?.shutdown()
////                cameraExecutor = null
//            }
//            Lifecycle.Event.ON_DESTROY -> {
//
//                cameraExecutor?.shutdown()
//                cameraExecutor = null
//            }
            else -> Unit
        }
    }


    private fun getLifecycle(): Lifecycle = builder.lifecycleOwner?.lifecycle!!

    private fun getContext(): Context = builder.context

    private fun getLifeCycleOwner(): LifecycleOwner = builder.lifecycleOwner!!

    private fun getImageAnalyzer(): ImageAnalyzer = builder.imageAnalyzer!!


    class Builder(val context: Context) {
        var lifecycleOwner: LifecycleOwner? = null
            private set

        var imageAnalyzer: ImageAnalyzer? = null
            private set


        fun setLifecycleOwner(lifecycleOwner: LifecycleOwner): Builder {
            this.lifecycleOwner = lifecycleOwner
            return this
        }

        fun setImageAnalyzer(imageAnalyzer: ImageAnalyzer): Builder {
            this.imageAnalyzer = imageAnalyzer
            return this
        }


        fun build(): InterviewManager {
            requireNotNull(lifecycleOwner) { "LifecycleOwner is not set" }
            requireNotNull(imageAnalyzer) { "ImageAnalyzer is not set" }
            return InterviewManager(this)
        }
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    class ImageAnalyzer(private val listener: ImageListener) : ImageAnalysis.Analyzer {
        override fun  analyze(imageProxy: ImageProxy) {

            listener(imageProxy)

        }


    }

    companion object {
        private const val TAG = "InterviewManager"
    }

}

val LocalInterviewManager =
    compositionLocalOf<InterviewManager> { error("No capture manager found!") }

typealias ImageListener = (image: ImageProxy) -> Unit

fun ByteBuffer.toByteArray(): ByteArray {
    rewind()
    val data = ByteArray(remaining())
    get(data)
    return data
}