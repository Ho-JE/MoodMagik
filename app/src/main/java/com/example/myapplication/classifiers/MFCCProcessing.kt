package com.example.myapplication.classifiers
import android.content.Context
import android.util.Log
import com.jlibrosa.audio.JLibrosa
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

class MFCCProcessing(context: Context) {

    private lateinit var mfccValues : Array<FloatArray>
    private lateinit var meanMFCCValues: FloatArray
    private val interpreter: Interpreter

    init {
        val tfliteModel = FileUtil.loadMappedFile(context, "vocal.tflite")
        interpreter = Interpreter(tfliteModel)
        interpreter.allocateTensors()

        val inputDetails = interpreter.getInputTensor(0).shape()
        val outputDetails = interpreter.getOutputTensor(0).shape()

        Log.d("input shape", inputDetails.joinToString(","))
        Log.d("output shape", outputDetails.joinToString(","))

    }

    fun process(context: Context){

        val mean1 = doubleArrayOf(-5.38765039e+02, 5.42052202e+01, -1.16134864e+01, 9.51006640e+00, -2.44696425e+00, -4.51306082e+00, -9.05774729e+00, -8.91655961e+00, -1.09047655e+01, -8.61029162e-01, -6.22743905e+00, -2.62460427e+00, -4.15896006e+00, 1.09023702e-01, -4.42701842e+00, -9.65115867e-03, -5.34222985e+00, 5.38647406e-01, -3.30328248e+00, 8.88827897e-01, -3.41090854e+00, 1.45961204e+00, -3.03899151e+00, 1.81500991e+00, -2.19446149e+00, 2.87380392e+00, -1.52337354e+00, 3.19354312e+00, -5.38503214e-01, 2.86250952e+00, -2.68321984e-01, 2.62809572e+00, 1.72041366e-02, 2.73349257e+00, 4.51808369e-01, 2.70367178e+00, 2.15396295e-01, 1.55810800e+00, -3.62766892e-01, 1.65063948e+00)
        val std1 = doubleArrayOf(105.98097526, 18.04039544, 14.17778077, 8.60646451, 9.46007456, 7.66515995, 6.53409868, 5.82887903, 5.00876992, 4.64178437, 4.88224971, 4.56725622, 4.85289566, 4.55974559, 4.29942259, 4.58195452, 4.20305122, 3.9984854, 4.0180477, 4.38036527, 4.35810805, 3.74893276, 3.65336059, 4.04771669, 3.57452011, 3.74309061, 3.51353645, 3.34027755, 3.07620115, 3.09413098, 2.99316573, 3.1808704, 3.09882951, 3.25015346, 3.11179776, 2.69942749, 2.52326477, 2.61054209, 2.51132991, 2.42797095)

        /**
         * Step 1 Create the options and Jlibrosa Instance
         * from Jlibros() class
         */
        val audioFilePath = "/sdcard/Audiobooks/03-01-03-02-01-02-02.wav"

        val defaultSampleRate = 22050   //-1 value implies the method to use default sample rate
        val defaultAudioDuration = 4   //-1 value implies the method to process complete audio duration
        val jLibrosa = JLibrosa()

        /**
         * To Read The Magnitude Values of Audio Files
         * equivalent to librosa.load('../audioFiles/1995-1826-0003.wav', sr=None) function
         */
        val audioFeaturesValues = jLibrosa.loadAndRead(audioFilePath, 22050, defaultAudioDuration)
        Log.i("audioFeaturesValues", audioFeaturesValues.joinToString(", "))

        mfccValues = jLibrosa.generateMFCCFeatures(audioFeaturesValues, 44100, 40)

        for (i in 0 until 1) {
            Log.d("mfccValues", mfccValues[i].joinToString(", "))
        }

        meanMFCCValues = jLibrosa.generateMeanMFCCFeatures(mfccValues, mfccValues.size, mfccValues[0].size)

        Log.i("meanMFCCValues", meanMFCCValues.joinToString(", "))

        var doubleMeanMFCCValues = meanMFCCValues.map { it.toDouble() }.toDoubleArray()

        Log.i("doubleMeanMFCCValues", doubleMeanMFCCValues.joinToString(", "))




        for (i in doubleMeanMFCCValues.indices){
            doubleMeanMFCCValues[i] -= mean1[i]
            doubleMeanMFCCValues[i] /= std1[i]
        }

        val normalizedMFCCValues = doubleMeanMFCCValues.map { it.toFloat() }.toFloatArray()

        Log.i("normalizedMFCCValues", normalizedMFCCValues.joinToString(", "))

        var inputBuffer: Array<FloatArray> = Array(1) { FloatArray(40) }

        for (i in normalizedMFCCValues.indices) {
            inputBuffer[0][i] = normalizedMFCCValues[i]
        }

        for (i in 0 until inputBuffer.size) {
            Log.d("inputBuffer", inputBuffer[i].joinToString(", "))
        }

        val inputBuffer1 = Array(1) { Array(40) { FloatArray(1) { 0f } } }

        for (i in 0 until 40) {
            inputBuffer1[0][i][0] = normalizedMFCCValues[i]
        }

        val outputBuffer = Array(1) { FloatArray(5) }

        interpreter.run(inputBuffer1, outputBuffer)

        for (i in 0 until outputBuffer.size) {
            Log.d("Result", outputBuffer[i].joinToString(", "))
        }

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