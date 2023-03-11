package com.example.moodmagik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.util.*

class RecordingActivity : AppCompatActivity() {
    private lateinit var topicChose: String
    private var disgustProgressVal = 0
    private var happinessProgressVal = 0
    private var sadnessProgressVal = 0
    private var fearProgressVal = 0
    private var angerProgressVal = 0
    private var topics = ArrayList<String>()
    // adapter for topic spinner
    private lateinit var spinAdapter:ArrayAdapter<String>
    // whether microphone button is pressed
    private var buttonPressed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recording_screen)


        // get topic saved in local res
        val scanner  = Scanner(resources.openRawResource(R.raw.topics))
        readFile(scanner)

        // topics spinner
        val suggestedTopicSpinner = findViewById<Spinner>(R.id.suggestedTopicSpinner)

        spinAdapter = ArrayAdapter<String>(this, R.layout.selected_topic, topics)
        spinAdapter.setDropDownViewResource(R.layout.topic_spinner_dropdown)
        suggestedTopicSpinner.adapter = spinAdapter

        suggestedTopicSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>, p1: View?, p2: Int, p3: Long){
                topicChose = p0!!.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        // needs some machine learning algo to give the percentage
        disgustProgressVal = 90;
        val disgustProgressBar = findViewById<ProgressBar>(R.id.disgustDegree)
        setProgressBar(disgustProgressBar, disgustProgressVal)

        happinessProgressVal = 50;
        val happinessProgressBar = findViewById<ProgressBar>(R.id.happinessDegree)
        setProgressBar(happinessProgressBar, happinessProgressVal)

        sadnessProgressVal = 70;
        val sadnessProgressBar = findViewById<ProgressBar>(R.id.sadnessDegree)
        setProgressBar(sadnessProgressBar, sadnessProgressVal)

        fearProgressVal = 10;
        val fearProgressBar = findViewById<ProgressBar>(R.id.fearDegree)
        setProgressBar(fearProgressBar, fearProgressVal)

        angerProgressVal = 30;
        val angerProgressBar = findViewById<ProgressBar>(R.id.angerDegree)
        setProgressBar(angerProgressBar, angerProgressVal)

        // change emotes on popup
        // if(){ // if the values change
        changeEmotePop(disgustProgressVal, happinessProgressVal, sadnessProgressVal, fearProgressVal, angerProgressVal)
        //}
    }

    // set progress bar
    fun setProgressBar(view: ProgressBar, progressVal: Int) {
        // need to get value
        view.setProgress(progressVal, true);
    }

    fun changeEmotePop(disgustDegree: Int, happinessDegree: Int, sadnessDegree: Int, fearDegree: Int, angerDegree: Int) {
        val progressValMap = mapOf("disgust" to disgustDegree, "happiness" to happinessDegree, "sadness" to sadnessDegree, "fear" to fearDegree, "anger" to angerDegree)

        // find the emotion with the largest degree
        var maxKey: String? = null
        for (key in progressValMap.keys) {
            if (maxKey == null || progressValMap[key]!! > progressValMap[maxKey]!!) {
                // only show fragment if the emotion is >=90
                if (progressValMap[key]!! >= 90) {
                    maxKey = key
                }
            }
        }

        if(maxKey != null){
            val emotionPopupFragmentManager = supportFragmentManager
            val emotionPopup = EmotionPopup()
            emotionPopup.show(emotionPopupFragmentManager, "My Fragment")

            // change fragment emote
            // declare transaction with fragment
//        val emotionPopupFragmentTransaction = emotionPopupFragmentManager.beginTransaction()
            val emotionPopupBundle = Bundle()

            emotionPopupBundle.putString("maxEmote", maxKey)
            emotionPopup.arguments = emotionPopupBundle
        }
    }

    // click fun for microphone button
    fun startRecording(view: View) {

        // button description
        val buttonDescripton = findViewById<TextView>(R.id.microphoneBtnDescription)

        if(!buttonPressed) {
            buttonPressed = true
            view.isActivated = true
            view.isSelected = true
            buttonDescripton.text = buildString {
                append("Press the button below to stop listening")
            }
        } else {
            buttonPressed = false
            view.isActivated = false
            view.isSelected = false
            buttonDescripton.text = buildString {
                append("Press the button below to start listening")
            }
        }
    }

    // read topics from local res
    private fun readFile(scanner: Scanner){
        topics.add("Select a topic")
        while(scanner.hasNextLine()){
            val line = scanner.nextLine()
            topics.add(line)
        }
    }
}