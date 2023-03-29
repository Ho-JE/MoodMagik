package com.example.myapplication.classifiers
import android.content.Context
import android.util.Log
import com.jlibrosa.audio.JLibrosa
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.lang.Math.round
import java.math.BigDecimal
import kotlin.math.roundToInt

class MFCCProcessing(context: Context, filePath:String) {

    private lateinit var mfccValues : Array<FloatArray>
    private lateinit var meanMFCCValues: FloatArray
    private val interpreter: Interpreter
    private val filePath : String = filePath
    private val outputHash :HashMap<String,Int> = HashMap()

    init {
        val tfliteModel = FileUtil.loadMappedFile(context, "vocal.tflite")
        interpreter = Interpreter(tfliteModel)
        interpreter.allocateTensors()

        //val inputDetails = interpreter.getInputTensor(0).shape()
        //val outputDetails = interpreter.getOutputTensor(0).shape()

        //Log.d("input shape", inputDetails.joinToString(","))
        //Log.d("output shape", outputDetails.joinToString(","))

    }

    fun process(context: Context): HashMap<String, Int> {

        val mean1 = doubleArrayOf(
            -579.170504, 65.5657456, 0.435613332, 11.8870739, -2.52469565,
            13.2878518, -1.06117453, 2.40647447, 1.6291746, 2.32823327,
            0.966475463, -1.28377384, -4.34413323, -1.44867217, -3.54243876,
            -3.04092811, -2.36799594, -1.9702195, -1.80912153, -1.15073068,
            -2.94353073, -0.913568348, -2.04468401, -0.843240847, -1.21947541,
            -0.402798351, -1.36990986, -0.419850706, -0.458062488, -0.670280343,
            -0.868790766, -0.455628248, -1.15451284, -0.597593589, -1.07132119,
            -0.706004164, -0.746182986, -0.730016735, -0.791410979, -0.623878229
        )
        val std1 = doubleArrayOf(
            111.2019194, 11.61164999, 9.46707807, 7.73407672, 5.64903146,
            5.5158024, 5.99770053, 5.87645955, 4.78943771, 4.05692761,
            5.5034084, 4.65239109, 3.73389499, 3.64010182, 3.86862719,
            2.54078029, 2.5423658, 2.20106905, 2.37986113, 2.75964876,
            2.14411156, 2.03859933, 2.17970062, 2.04282205, 2.25326973,
            1.92840135, 1.89220239, 1.9473582, 2.06364991, 1.85776172,
            1.91591541, 1.89023351, 1.77867487, 1.77521959, 1.81375852,
            1.7260439, 1.73075539, 1.6676271, 1.74452991, 1.70853141
        )

        /**
         * Step 1 Create the options and Jlibrosa Instance
         * from Jlibros() class
         */
        //val audioFilePath = "/sdcard/Audiobooks/Actor_01/03-01-06-01-01-01-01.wav"
        val audioFilePath = filePath
        val defaultSampleRate = 22050   //-1 value implies the method to use default sample rate
        val defaultAudioDuration = 4   //-1 value implies the method to process complete audio duration
        val jLibrosa = JLibrosa()

        /**
         * To Read The Magnitude Values of Audio Files
         * equivalent to librosa.load('../audioFiles/1995-1826-0003.wav', sr=None) function
         */
        val audioFeaturesValues = jLibrosa.loadAndReadWithOffset(audioFilePath, defaultSampleRate, defaultAudioDuration,0)

        //Log.i("audioFeaturesValues", audioFeaturesValues.joinToString(", "))

        val sampleRate = jLibrosa.sampleRate

        Log.d("MFCC_PROCESSING", "Sample Rate : $sampleRate")


        mfccValues = jLibrosa.generateMFCCFeatures(audioFeaturesValues, 22050, 40)
        /*
        for (i in 0 until 1) {
            Log.d("mfccValues", mfccValues[i].joinToString(", "))
        }
        */

        meanMFCCValues = jLibrosa.generateMeanMFCCFeatures(mfccValues, mfccValues.size, mfccValues[0].size)

        //Log.i("meanMFCCValues", meanMFCCValues.joinToString(", "))

        var doubleMeanMFCCValues = meanMFCCValues.map { it.toDouble() }.toDoubleArray()

        for (i in doubleMeanMFCCValues.indices){
            doubleMeanMFCCValues[i] -= mean1[i]
            doubleMeanMFCCValues[i] /= std1[i]
        }

        val normalizedMFCCValues = doubleMeanMFCCValues.map { it.toFloat() }.toFloatArray()

        //Log.i("normalizedMFCCValues", normalizedMFCCValues.joinToString(", "))

        var inputBuffer: Array<FloatArray> = Array(1) { FloatArray(40) }

        for (i in normalizedMFCCValues.indices) {
            inputBuffer[0][i] = normalizedMFCCValues[i]
        }
        /*
        for (i in 0 until inputBuffer.size) {
            Log.d("inputBuffer", inputBuffer[i].joinToString(", "))
        }

         */

        val inputBuffer1 = Array(1) { Array(40) { FloatArray(1) { 0f } } }

        for (i in 0 until 40) {
            inputBuffer1[0][i][0] = normalizedMFCCValues[i]
        }

        val outputBuffer = Array(1) { FloatArray(5) }

        interpreter.run(inputBuffer1, outputBuffer)

        val emoList = ArrayList<String>()
        emoList.add("Angry")
        emoList.add("Fear")
        emoList.add("Happy")
        emoList.add("Neutral")
        emoList.add("Sad")


        Log.d("outputbuffer",outputBuffer.toString())

        for (i in 0 until outputBuffer.size) {
            Log.d("Result", outputBuffer[i].joinToString(", "))
            for (i in 0 until outputBuffer[i].size) {
                val input  = outputBuffer[0][i]*100
                outputHash[emoList[i]] = input.roundToInt()
            }
        }

        return outputHash

    }



    fun getMFCCValues() : Array<FloatArray>{
        return mfccValues
    }

    fun getMeanMFCCValues() : FloatArray{
        return meanMFCCValues
    }


}

/*
        val nNoOfFrames = jLibrosa.noOfFrames
        val sampleRate = jLibrosa.sampleRate
        val noOfChannels = jLibrosa.noOfChannels

        Log.d("MFCC_PROCESSING", "Signal : " + audioFeaturesValues[0].toString())
        Log.d("MFCC_PROCESSING", "Sample Rate : $sampleRate")
        Log.d("MFCC_PROCESSING", "Size of MFCC Feature Values: ($nNoOfFrames )")

        /**
         * Process the MFCC
         * **/
        mfccValues = jLibrosa.generateMFCCFeatures(audioFeaturesValues, 8000, 32)
        meanMFCCValues = jLibrosa.generateMeanMFCCFeatures(mfccValues, mfccValues.size, mfccValues[0].size)
        Log.d("MFCC_PROCESSING","Size of MFCC Feature Values: (" + mfccValues.size + " , " + mfccValues[0].size + " )")
        */