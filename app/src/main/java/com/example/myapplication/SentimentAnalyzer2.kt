
import android.content.Context
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class SentimentAnalyzer2(context: Context) {

    private val interpreter: Interpreter
    private val maxSeqLength = 100
    private val tokenizer: HashMap<String, Int>

    init {
        // Load the TFLite model
        val tfliteModel = FileUtil.loadMappedFile(context, "my_model.tflite")
        interpreter = Interpreter(tfliteModel)

        // Load the tokenizer from the file
        val tokenizerFile = context.assets.open("word_dict.json")
        val tokenizerBytes = ByteArray(tokenizerFile.available())
        tokenizerFile.read(tokenizerBytes)
        tokenizerFile.close()
        val tokenizerJson = String(tokenizerBytes)

        // Convert the tokenizer from JSON to a HashMap
        val jsonObject = JSONObject(tokenizerJson)
        val keysIterator = jsonObject.keys()
        tokenizer = HashMap<String, Int>()
        while (keysIterator.hasNext()) {
            val key = keysIterator.next()
            tokenizer[key] = jsonObject.getInt(key)
        }
    }

    // Define a function to preprocess the text and convert it to a numerical sequence
    private fun preprocessText(text: String): ByteBuffer {
        // Tokenize the text
        val tokens = text.lowercase(Locale.ROOT).split(" ").mapNotNull { tokenizer[it] }

        // Pad or truncate the sequence to the specified length
        val paddedTokens = if (tokens.size > maxSeqLength) {
            tokens.subList(0, maxSeqLength)
        } else {
            tokens + List(maxSeqLength - tokens.size) { 0 }
        }.reversed()

        // Convert the sequence to a ByteBuffer of floats
        val inputBuffer = ByteBuffer.allocateDirect(maxSeqLength * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        for (token in paddedTokens) {
            inputBuffer.putFloat(token.toFloat())
        }
        return inputBuffer
    }

    // Define a function to make predictions using the TFLite model
    fun predictEmotion(text: String): Int {
        // Preprocess the text in the same way as the training data
        val inputBuffer = preprocessText(text)

        // Define the output buffer
        val outputBuffer = ByteBuffer.allocateDirect(16)
        outputBuffer.order(ByteOrder.nativeOrder())

        // Make predictions using the TFLite model
        interpreter.run(inputBuffer, outputBuffer)

        // Get the predicted label
        val predictions = FloatArray(4)
        outputBuffer.rewind()
        outputBuffer.asFloatBuffer().get(predictions)
        return predictions.indices.maxByOrNull { predictions[it] }!!
    }
}
