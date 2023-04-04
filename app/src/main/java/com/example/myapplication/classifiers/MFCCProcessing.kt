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
        val tfliteModel = FileUtil.loadMappedFile(context, "82_1_final.tflite")
        interpreter = Interpreter(tfliteModel)
        interpreter.allocateTensors()

        //val inputDetails = interpreter.getInputTensor(0).shape()
        //val outputDetails = interpreter.getOutputTensor(0).shape()

        //Log.d("input shape", inputDetails.joinToString(","))
        //Log.d("output shape", outputDetails.joinToString(","))

    }

    fun process(context: Context): HashMap<String, Int> {

        val mean1 = doubleArrayOf(-5.77827875e+02, 6.58255203e+01, 3.81008535e-01, 1.18187279e+01,
            -2.52884888e+00, 1.32814891e+01, -1.03445590e+00, 2.36730820e+00,
            1.61975385e+00, 2.36538828e+00, 9.62271603e-01, -1.26351203e+00,
            -4.30395068e+00, -1.44755949e+00, -3.50737487e+00, -2.96329631e+00,
            -2.32437247e+00, -1.96720518e+00, -1.76416341e+00, -1.13155542e+00,
            -2.91848394e+00, -9.04855025e-01, -2.03658068e+00, -8.68865652e-01,
            -1.22133077e+00, -3.86830074e-01, -1.35000608e+00, -3.77403957e-01,
            -4.14840524e-01, -6.20583128e-01, -8.43920059e-01, -4.23775376e-01,
            -1.14199763e+00, -5.79127409e-01, -1.03994089e+00, -6.74715725e-01,
            -7.13172187e-01, -7.16587254e-01, -7.81511219e-01, -6.07242053e-01)


        val std1 = doubleArrayOf(111.73847039, 11.64313745, 9.64376207, 7.76524934, 5.64386423,
            5.51485871, 6.0309662, 5.88010843, 4.76364026, 4.03984795,
            5.49029462, 4.69099362, 3.77982746, 3.61184357, 3.90055301,
            2.58631642, 2.67110511, 2.194203, 2.43979569, 2.78993074,
            2.17039551, 2.04161262, 2.18806728, 2.02480147, 2.24271566,
            1.94144536, 1.89845971, 1.99154304, 2.07397189, 1.90386202,
            1.955197, 1.90478787, 1.79093742, 1.78675186, 1.83353214,
            1.73598997, 1.74907107, 1.67095276, 1.74413654, 1.71399268)

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
        val audioFilePath = "/sdcard/DCIM/EmotionRecording_20230404_1346 23.wav"
        //val audioFilePath = "/sdcard/Audiobooks/Actor_01/03-01-06-01-01-01-01.wav"
        //val audioFilePath = "/storage/emulated/0/DCIM/neutral rerecord.wav"
        //val audioFilePath = filePath

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