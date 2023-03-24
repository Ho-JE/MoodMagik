
import android.content.Context
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SentimentAnalyzer(context: Context) {

    private val tokenizer: HashMap<String, Int>
    private val tflite: Interpreter

    init {
        // Load the word_dict.json file from assets folder
        val jsonString = context.assets.open("word_dict.json").bufferedReader().use { it.readText() }
        tokenizer = JSONObject(jsonString).toHashMap()

        // Load the TFLite model from assets folder
        val tfliteModel = FileUtil.loadMappedFile(context, "my_model.tflite")
        tflite = Interpreter(tfliteModel)
    }

    fun analyzeSentiment(text: String): Int {
        // Preprocess the text using the tokenizer
        val sequence = text.toLowerCase().split(" ")
            .mapNotNull { tokenizer[it] }
            .toIntArray()

        // Pad the sequence with zeros to match the input shape of the model
        val paddedSequence = sequence + IntArray(100 - sequence.size)

        // Convert the padded sequence to a ByteBuffer
        val inputBuffer = ByteBuffer.allocateDirect(paddedSequence.size * 4)
            .order(ByteOrder.nativeOrder())
            .apply {
                asIntBuffer().put(paddedSequence)
            }

        // Allocate a ByteBuffer for the output probabilities
        val outputBuffer = ByteBuffer.allocateDirect(4 * 4)
            .order(ByteOrder.nativeOrder())

        // Run the inference on the TFLite model
        tflite.run(inputBuffer, outputBuffer)

        // Get the predicted probabilities from the output ByteBuffer
        val predictedProbabilities = FloatArray(4)
        outputBuffer.asFloatBuffer().get(predictedProbabilities)

        // Return the index of the highest probability as the predicted label
        return predictedProbabilities.indices.maxByOrNull { predictedProbabilities[it] } ?: -1
    }

    private fun JSONObject.toHashMap(): HashMap<String, Int> {
        val hashMap = HashMap<String, Int>()
        keys().forEach { key ->
            hashMap[key] = getInt(key)
        }
        return hashMap
    }
}
