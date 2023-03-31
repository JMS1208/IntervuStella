package com.capstone.Capstone2Project.utils.service

import android.app.Activity.RESULT_OK
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*
import android.os.Environment.DIRECTORY_MOVIES
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.capstone.Capstone2Project.R
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ScreenRecordService: Service() {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var mediaRecorder: MediaRecorder? = null


    private val NOTIFICATION_ID = 23

    private val channelId = "recording_channel_id"
    private val channelName = "Screen Recording Channel"

    private val NOTHING_CODE = Integer.MAX_VALUE
    private var resultCode = NOTHING_CODE

    private var takenIntent: Intent? = null

    private lateinit var screenStateReceiver: BroadcastReceiver
    private lateinit var serviceHandler: ServiceHandler

    private var isRecording: Boolean = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        fun newIntent(context: Context, resultCode: Int, data: Intent): Intent {
            val intent = Intent(context, ScreenRecordService::class.java)
            intent.apply {
                putExtra(EXTRA_RESULT_CODE, resultCode)
                putExtra(EXTRA_INTENT_DATA, data)
            }

            return intent
        }

        private val EXTRA_INTENT_DATA = "data"
        private val EXTRA_RESULT_CODE = "resultcode"

    }

    override fun onCreate() {

        createNotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)

        val notificationIntent = Intent(this, ScreenRecordService::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification =
            NotificationCompat.Builder(this)
                .setContentTitle("InterviewStella")
                .setContentText("Your screen is being recorded and saved to your phone.")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
//                .setTicker("Tickertext")
                .setChannelId(channelId)
                .build()

        startForeground(NOTIFICATION_ID, notification)

        screenStateReceiver = ScreenRecordBroadcastReceiver()
        val screenStateFilter = IntentFilter()

        screenStateFilter.apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_CONFIGURATION_CHANGED)
        }

        registerReceiver(screenStateReceiver, screenStateFilter)

        val thread = HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND)

        thread.start()

        val serviceLooper = thread.looper

        serviceHandler = ServiceHandler(serviceLooper)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("TAG", "onStartCommand: $intent")
        intent ?: throw IllegalStateException("Intent missing")

        Toast.makeText(this, "Starting recording service", Toast.LENGTH_SHORT).show()

        resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, NOTHING_CODE)
        takenIntent = intent.getParcelableExtra(EXTRA_INTENT_DATA)

        if(resultCode == NOTHING_CODE || takenIntent == null) {
            throw IllegalStateException("Result code or data missing.")
        }

        val msg = serviceHandler.obtainMessage()
        msg.arg1 = startId
        serviceHandler.sendMessage(msg)

        return START_NOT_STICKY
    }

    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = baseContext.getSystemService(NotificationManager::class.java)

            val serviceChannel = NotificationChannel(
                channelId,
                channelName,
                importance
            )

            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun startRecording(resultCode: Int, data: Intent) {
        if (isRecording) {
            Toast.makeText(this, "이미 녹화중입니다", Toast.LENGTH_SHORT).show()
            return
        }
        val mediaProjectionManager = applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaRecorder = MediaRecorder()

        mediaRecorder ?: throw Exception()

        val metrics = DisplayMetrics()
        val windowManager = applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getRealMetrics(metrics)

        val densityDpi = metrics.densityDpi
        val displayWidth = metrics.widthPixels
        val displayHeight = metrics.heightPixels

        mediaRecorder!!.apply {
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setVideoEncodingBitRate(16 * 1000 * 1000)
            setVideoFrameRate(30)
            setVideoSize(displayWidth, displayHeight)
        }

        val videoDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES).absolutePath


        val timeStamp = System.currentTimeMillis()

        val simpleDateFormat = SimpleDateFormat("yyyy_MM_dd_hh_ss", Locale.getDefault())


        val filePathAndName = "$videoDir/interviewStella_${simpleDateFormat.format(timeStamp)}.mp4"

        mediaRecorder!!.setOutputFile(filePathAndName)

        try {
            mediaRecorder!!.prepare()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
        val surface = mediaRecorder!!.surface
        virtualDisplay = mediaProjection!!.createVirtualDisplay(
            "MainActivity",
            displayWidth,
            displayHeight,
            densityDpi,
            VIRTUAL_DISPLAY_FLAG_PRESENTATION,
            surface,
            null,
            null
        )
        mediaRecorder!!.start()
        isRecording = true
    }

    private fun stopRecording() {
        mediaRecorder?.stop()
        mediaProjection?.stop()
        mediaRecorder?.release()
        virtualDisplay?.release()
        isRecording = false
    }

    override fun onDestroy() {
        stopRecording()
        unregisterReceiver(screenStateReceiver)
        stopSelf()
        Toast.makeText(this, "Recorder service stopped", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

    inner class ServiceHandler(looper: Looper): Handler(looper) {
        override fun handleMessage(msg: Message) {
            if (resultCode == RESULT_OK && takenIntent != null) {
                startRecording(resultCode, takenIntent!!)
            }
        }
    }

    inner class ScreenRecordBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent ?: return

            when(intent.action) {
                Intent.ACTION_SCREEN_OFF-> {
                    //stopRecording()
                }
                Intent.ACTION_CONFIGURATION_CHANGED-> {
                    //stopRecording()
                }
                else-> Unit
            }
        }
    }
}