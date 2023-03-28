package com.example.myapplication.classifiers
import android.content.Context
import android.util.Log
import com.example.myapplication.R
import com.jlibrosa.audio.JLibrosa

class MFCCProcessing {

    private lateinit var mfccValues : Array<FloatArray>
    private lateinit var meanMFCCValues: FloatArray

    fun process(context: Context){



        /**
         * Step 1 Create the options and Jlibrosa Instance
         * from Jlibros() class
         */
        val audioFilePath = "android.resource://com.example.myapplication/raw/sample"

        val defaultSampleRate = 22050    //-1 value implies the method to use default sample rate
        val defaultAudioDuration = 4   //-1 value implies the method to process complete audio duration
        val jLibrosa = JLibrosa()

        /**
         * To Read The Magnitude Values of Audio Files
         * equivalent to librosa.load('../audioFiles/1995-1826-0003.wav', sr=None) function
         */
        val audioFeaturesValues = jLibrosa.loadAndRead("android.resource://com.example.myapplication"  + "/" + R.raw.test, defaultSampleRate, defaultAudioDuration)

        /**
         * To read the no of frames present in audio file
         * To read sample rate of audio file
         * To read number of channels in audio file
         */
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
    }

    fun getMFCCValues() : Array<FloatArray>{
        return mfccValues
    }

    fun getMeanMFCCValues() : FloatArray{
        return meanMFCCValues
    }


}