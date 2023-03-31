package com.capstone.Capstone2Project.utils

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ExpressionAnalyzer (
    private val context: Context,
    private val modelName: String = EXPRESSION_ANALYZER
)  {

    private lateinit var interpreter: Interpreter

    private lateinit var inputTensorImage: TensorImage

    private lateinit var outputBuffer: TensorBuffer

    private var modelInputChannel = 0

    private var modelInputWidth = 0

    private var modelInputHeight = 0

    private var modelBatchSize = 0

    private var modelOutputClasses = 0

    init {
        loadModel()
    }


    suspend fun classifyExpression(bitmap: Bitmap): String {

        val buffer = convertBitmapGrayByteBuffer(resizeBitmap(bitmap))
        val result = Array(1) { FloatArray(modelOutputClasses) {0f} }

        interpreter.run(buffer, result)

        return result[0].toList().toString()

    }

    private fun loadModel() {

        val assetFileDescriptor = context.assets.openFd(modelName)

        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)

        val fileChannel = fileInputStream.channel

        val startOffset = assetFileDescriptor.startOffset

        val declaredLength = assetFileDescriptor.declaredLength

        val model = fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            startOffset,
            declaredLength
        )

        model.order(ByteOrder.nativeOrder())

        interpreter = Interpreter(model)

        initModelShape()
    }

    private fun initModelShape() {
        val inputTensor = interpreter.getInputTensor(0)

        val inputShape = inputTensor.shape()

        modelBatchSize = inputShape[0]

        modelInputWidth = inputShape[1]

        modelInputHeight = inputShape[2]

        modelInputChannel = inputShape[3]

        inputTensorImage = TensorImage(inputTensor.dataType())

        val outputTensor = interpreter.getOutputTensor(0)

        val outputShape = outputTensor.shape()

        modelOutputClasses = outputShape[1]

        outputBuffer = TensorBuffer.createFixedSize(outputTensor.shape(), outputTensor.dataType())
    }


    private fun convertBitmapGrayByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(bitmap.byteCount)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        pixels.forEach { pixel ->
            val r = pixel shr 16 and 0xFF
            val g = pixel shr 8 and 0xFF
            val b = pixel and 0xFF

            val avgPixelValue = (r + g + b) / 3.0f
            val normalizedPixelValue = avgPixelValue / 255.0f

            byteBuffer.putFloat(normalizedPixelValue)
        }
        return byteBuffer
    }


    private fun resizeBitmap(bitmap: Bitmap) =
        Bitmap.createScaledBitmap(bitmap, modelInputWidth, modelInputHeight, false)

    companion object {
        const val EXPRESSION_ANALYZER = "tensorflowlite/converted_model.tflite"
    }

}