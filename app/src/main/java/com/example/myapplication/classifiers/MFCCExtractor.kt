package com.example.myapplication.classifiers

import android.content.Context
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.mfcc.MFCC
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.roundToInt

class MFCCExtractor(context: Context) {
    private lateinit var audioDispatcher: AudioDispatcher
    private lateinit var mfcc: MFCC
    private lateinit var interpreter: Interpreter

    init {
        // Load the TFLite model
        val tfliteModel = File(context.filesDir, "vocal.tflite")
        interpreter = Interpreter(tfliteModel)

        // Set up the MFCC extractor
        val sampleRate = 22050f // sample rate of your audio file
        val numCoefficients = 40 // number of MFCC coefficients you want to extract
        val bufferSize = (sampleRate * 0.04).roundToInt()
        val bufferOverlap = (sampleRate * (0.04 - 0.02)).roundToInt()


        val minFrequency = 0f // minimum frequency for Mel filter bank
        val maxFrequency = sampleRate / 2 // maximum frequency for Mel filter bank

        val mfcc = MFCC(bufferSize,
            bufferOverlap.toFloat(), numCoefficients, sampleRate.toInt(), minFrequency, maxFrequency)
    }

    fun extractMFCC(audioPath: String): FloatArray {
        // Load and preprocess the audio file
        audioDispatcher = AudioDispatcherFactory.fromPipe(audioPath, 22050, 1024, 512)
        audioDispatcher.addAudioProcessor(mfcc)
        audioDispatcher.run()

        // Get the MFCC features
        val featureBuffer = mfcc.mfcc
        val numFrames = featureBuffer.size / mfcc.coefficients
        val featureArray = FloatArray(mfcc.coefficients * numFrames)

        for (i in 0 until numFrames) {
            for (j in 0 until mfcc.coefficients) {
                featureArray[i * mfcc.coefficients + j] = featureBuffer[i][j]
            }
        }

        // Normalize the MFCC features
        val mean = featureArray.average()
        val std = featureArray.std()
        for (i in featureArray.indices) {
            featureArray[i] = (featureArray[i] - mean) / std
        }

        return featureArray
    }

    fun predictEmotion(features: FloatArray): String {
        // Set the input to the TFLite model and run inference
        val inputBuffer = ByteBuffer.allocateDirect(features.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        inputBuffer.put(features)
        interpreter.run(inputBuffer, outputBuffer)

        // Get the output of the TFLite model and convert it to a string
        val emotionIndex = outputBuffer[0].toInt()
        return when (emotionIndex) {
            0 -> "neutral"
            1 -> "calm"
            2 -> "happy"
            3 -> "sad"
            4 -> "angry"
            else -> "unknown"
        }
    }
}
