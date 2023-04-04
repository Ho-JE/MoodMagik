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
        val tfliteModel = FileUtil.loadMappedFile(context, "retrained.tflite")
        interpreter = Interpreter(tfliteModel)
        interpreter.allocateTensors()

        //val inputDetails = interpreter.getInputTensor(0).shape()
        //val outputDetails = interpreter.getOutputTensor(0).shape()

        //Log.d("input shape", inputDetails.joinToString(","))
        //Log.d("output shape", outputDetails.joinToString(","))

    }

    fun process(context: Context): HashMap<String, Int> {

        val mean1 = doubleArrayOf(-5.76745617e+02, 6.58544802e+01, 5.80975091e-01, 1.18145527e+01,
            -2.47655326e+00, 1.33424500e+01, -1.03149895e+00, 2.47755477e+00,
            1.64036360e+00, 2.41025006e+00, 9.71591819e-01, -1.19792496e+00,
            -4.34510136e+00, -1.36082091e+00, -3.54728063e+00, -2.95729366e+00,
            -2.38221245e+00, -1.91011578e+00, -1.82225500e+00, -1.10413259e+00,
            -2.94390251e+00, -8.77243306e-01, -2.06091450e+00, -8.60987430e-01,
            -1.24018740e+00, -3.72183166e-01, -1.36052094e+00, -3.95165278e-01,
            -4.40472123e-01, -6.30947073e-01, -8.52806561e-01, -4.41116808e-01,
            -1.15637392e+00, -5.86280194e-01, -1.05465328e+00, -7.04828085e-01,
            -7.32723884e-01, -7.22304223e-01, -7.82963901e-01, -6.11532459e-01)


        val std1 = doubleArrayOf(113.64142462, 11.65100555, 9.78297986, 7.75821532, 5.72829058,
            5.5274545, 6.04500998, 5.93827005, 4.7502253, 4.1108269,
            5.48532326, 4.82583129, 3.78430059, 3.73745234, 3.86892268,
            2.6180769, 2.6011091, 2.40562066, 2.39020351, 2.82515836,
            2.16488258, 2.09040339, 2.17389517, 2.02918261, 2.25505879,
            1.95404566, 1.90537322, 1.98785949, 2.09455197, 1.88823734,
            1.96193186, 1.90731562, 1.80314983, 1.78991787, 1.82470146,
            1.73241674, 1.72460455, 1.66476619, 1.74285045, 1.70974009)

//        val mean1 = doubleArrayOf(
//            -579.170504, 65.5657456, 0.435613332, 11.8870739, -2.52469565,
//            13.2878518, -1.06117453, 2.40647447, 1.6291746, 2.32823327,
//            0.966475463, -1.28377384, -4.34413323, -1.44867217, -3.54243876,
//            -3.04092811, -2.36799594, -1.9702195, -1.80912153, -1.15073068,
//            -2.94353073, -0.913568348, -2.04468401, -0.843240847, -1.21947541,
//            -0.402798351, -1.36990986, -0.419850706, -0.458062488, -0.670280343,
//            -0.868790766, -0.455628248, -1.15451284, -0.597593589, -1.07132119,
//            -0.706004164, -0.746182986, -0.730016735, -0.791410979, -0.623878229
//        )
//        val std1 = doubleArrayOf(
//            111.2019194, 11.61164999, 9.46707807, 7.73407672, 5.64903146,
//            5.5158024, 5.99770053, 5.87645955, 4.78943771, 4.05692761,
//            5.5034084, 4.65239109, 3.73389499, 3.64010182, 3.86862719,
//            2.54078029, 2.5423658, 2.20106905, 2.37986113, 2.75964876,
//            2.14411156, 2.03859933, 2.17970062, 2.04282205, 2.25326973,
//            1.92840135, 1.89220239, 1.9473582, 2.06364991, 1.85776172,
//            1.91591541, 1.89023351, 1.77867487, 1.77521959, 1.81375852,
//            1.7260439, 1.73075539, 1.6676271, 1.74452991, 1.70853141
//        )

//        val array1 = doubleArrayOf(-576.770037, 65.8530411, 0.582853177, 11.8083044, -2.47519879, 13.3418631,
//            -1.01373792, 2.48586756, 1.63761168, 2.39743235, 0.968055869, -1.19768088,
//            -4.33198356, -1.36529312, -3.55294148, -2.94734398, -2.37290665, -1.91710415,
//            -1.8217904, -1.0973939, -2.93317181, -0.882200535, -2.06555725, -0.860587255,
//            -1.23697129, -0.373588705, -1.35762551, -0.394518487, -0.444031545, -0.633578739,
//            -0.849433285, -0.441172715, -1.15887166, -0.585133383, -1.05122058, -0.704361702,
//            -0.732622445, -0.721980699, -0.783992357, -0.60879357)
//
//        val array2 = doubleArrayOf(113.56401229, 11.64771444, 9.78881869, 7.76280071, 5.73144082, 5.52735349,
//            6.03694418, 5.94358038, 4.75102696, 4.0919672, 5.4888297, 4.82664052,
//            3.77157045, 3.72499626, 3.87162986, 2.64560057, 2.59153638, 2.37120137,
//            2.38971366, 2.83455343, 2.15892798, 2.07719301, 2.17116792, 2.02847629,
//            2.24955258, 1.95224949, 1.90237682, 1.98771137, 2.0968551, 1.88699314,
//            1.95697829, 1.90740212, 1.80656338, 1.78848259, 1.82037529, 1.73146429,
//            1.72452489, 1.66473808, 1.74205226, 1.71104407)




        /**
         * Step 1 Create the options and Jlibrosa Instance
         * from Jlibros() class
         */
        //val audioFilePath = "/sdcard/Audiobooks/Actor_01/03-01-06-01-01-01-01.wav"
//        val audioFilePath = "/sdcard/Audiobooks/Actor_01/03-01-06-01-01-01-01.wav"
        //val audioFilePath = "/storage/emulated/0/DCIM/neutral rerecord.wav"
        val audioFilePath = filePath

        //Log.d("path", audioFilePath)

        val defaultSampleRate = 22050   //Does not work, LoadAndRead doesn't use this at all -1 value implies the method to use default sample rate
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


        //Log.d("outputbuffer",outputBuffer.toString())

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