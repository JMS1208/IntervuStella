package com.capstone.Capstone2Project.utils

import android.content.Context
import android.hardware.Camera
import android.media.MediaRecorder
import androidx.camera.video.internal.compat.Api23Impl.setAudioSource
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.capstone.Capstone2Project.utils.etc.sdk31AndUp
import com.capstone.Capstone2Project.utils.file.FileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.view.Surface


class VideoRecordManager private constructor(
    private val builder: Builder
): LifecycleEventObserver {

    private var recorder: MediaRecorder? = null

    private lateinit var fileManager: FileManager

//    private lateinit var camera: Camera

    private var outputFile: File? = null

    init {
        getLifecycle().addObserver(this)
//        initCamera()
        initFileManager()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event) {
            Lifecycle.Event.ON_PAUSE -> {
                recorder ?: pauseRecord()
            }
            Lifecycle.Event.ON_STOP -> {
                recorder ?: pauseRecord()
            }
            Lifecycle.Event.ON_DESTROY -> {
                recorder ?: stopRecord()
            }
            else-> Unit
        }
    }

//    private fun initCamera() {
//        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
//        camera.apply {
//            setDisplayOrientation(90)
//            unlock()
//        }
//    }

    private fun initFileManager() {
        fileManager = FileManager(getContext())
    }

    private suspend fun initRecorder() {

        outputFile = fileManager.createFile("interviewStella","mp4")

        recorder = sdk31AndUp {
            MediaRecorder(getContext())
        } ?: MediaRecorder()

        recorder?.apply {
            setVideoSource(MediaRecorder.VideoSource.CAMERA)
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setVideoFrameRate(30)
//            setCamera(camera)
//            setInputSurface(this@VideoRecordManager.getSurface())
            setOutputFile(outputFile!!.canonicalPath)
            prepare()
        }
    }

    fun startRecord() = CoroutineScope(Dispatchers.IO).launch {
        recorder ?: initRecorder()

        recorder?.start()
    }

    fun pauseRecord() {
        recorder?.pause()
    }

    fun resumeRecord() {
        recorder?.resume()
    }

    fun stopRecord(
        saveToExternalStorage: Boolean = true
    ) {
        recorder?.apply {
            pause()
            release()
//            camera.lock()
//            camera.release()

            if (saveToExternalStorage) {
                saveToExternalStorage()
            }

            recorder = null
        }
    }

    private fun saveToExternalStorage() = CoroutineScope(Dispatchers.IO).launch {
        outputFile ?: return@launch

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd-hh:mm", Locale.getDefault())

        val displayName = simpleDateFormat.format(Date(System.currentTimeMillis()))

        fileManager.saveVideoToExternalStorage(displayName, outputFile!!.toUri())
    }


    private fun getLifecycle(): Lifecycle = builder.lifecycleOwner?.lifecycle!!

    private fun getContext(): Context = builder.context

    private fun getLifeCycleOwner(): LifecycleOwner = builder.lifecycleOwner!!

//    private fun getSurface(): Surface = builder.surface!!

    class Builder(val context: Context) {
        var lifecycleOwner: LifecycleOwner? = null
            private set

//        var surface: Surface? = null
//            private set

        fun setLifecycleOwner(lifecycleOwner: LifecycleOwner): Builder {
            this.lifecycleOwner = lifecycleOwner
            return this
        }

//        fun setSurface(surface: Surface): Builder {
//            this.surface = surface
//            return this
//        }


        fun build(): VideoRecordManager {
            requireNotNull(lifecycleOwner) { "LifecycleOwner is not set" }
//            requireNotNull(surface) {"Surface should be set"}
            return VideoRecordManager(this)
        }
    }

}