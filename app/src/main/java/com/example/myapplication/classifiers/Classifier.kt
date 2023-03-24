package com.example.myapplication.classifiers

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Classifier(context: Context) {
    private var interpreter: Interpreter
    private var wordIndex: MutableMap<String, Int>

    init {
        // Load the model file from the assets folder
        val modelFile = File(context.cacheDir, "my_model.tflite")
        context.assets.open("my_model.tflite").use { inputStream ->
            modelFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // Load the word index dictionary from the assets folder
        val wordIndexFile = context.assets.open("word_dict.json")
        val wordIndexJson = wordIndexFile.bufferedReader().use { it.readText() }
        wordIndex = Gson().fromJson(wordIndexJson, object : TypeToken<MutableMap<String, Int>>() {}.type)
        val interpreterOptions = Interpreter.Options()

        // Create a TFLite interpreter object
        // Create a TFLite interpreter object
        try {
            interpreter = Interpreter(modelFile, interpreterOptions)
        } catch (e: Exception) {
            Log.e("Classifier", "Error creating interpreter: ${e.message}")
            throw e
        }

        // Log the input and output tensor types
        val inputTensor = interpreter.getInputTensor(0)
        val outputTensor = interpreter.getOutputTensor(0)
        Log.i("Classifier", "Input tensor type: ${inputTensor.dataType()}")
        Log.i("Classifier", "Output tensor type: ${outputTensor.dataType()}")
    }


    fun predict(inputText: String): Int {
        // Preprocess the input text using the same method as during training
        val inputTextPreprocessed = normalizeAndLemmatize(inputText)

        // Tokenize the input text using the word index dictionary
        val inputTextTokens = inputTextPreprocessed.split(" ")
        val inputTextSeq = inputTextTokens.map { wordIndex[it] ?: 0 }

        // Pad the sequence to the same length as the input to the model
        val inputTextPad = padSequence(inputTextSeq.reversed(), MAX_SEQ_LENGTH)

        // Create a byte buffer to hold the input data for the model
        val inputBuffer = ByteBuffer.allocateDirect(4 * MAX_SEQ_LENGTH)
        inputBuffer.order(ByteOrder.nativeOrder())

        // Fill the input buffer with the input data
        for (i in 0 until MAX_SEQ_LENGTH) {
            inputBuffer.putFloat(inputTextPad.getOrElse(i) { 0 }.toFloat())
        }

        // Create a byte buffer to hold the output data from the model
        val outputBuffer = ByteBuffer.allocateDirect(4)
        outputBuffer.order(ByteOrder.nativeOrder())

        // Run the inference on the input buffer and store the output in the output buffer
        interpreter.run(inputBuffer, outputBuffer)

        // Return the predicted label
        return outputBuffer.getFloat(0).toInt()
    }

    companion object {
        const val MAX_SEQ_LENGTH = 100

        /**
         * Pads the input sequence to the specified length with zeros.
         */
        fun padSequence(sequence: List<Int>, maxSeqLength: Int): List<Int> {
            return sequence.take(maxSeqLength).plus(List(maxSeqLength - sequence.size) { 0 })
        }

        /**
         * Normalizes and lemmatizes the input text.
         */
        fun normalizeAndLemmatize(text: String): String {
            // TODO: Implement normalization and lemmatization
            return text
        }
    }
}
