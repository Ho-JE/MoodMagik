package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.util.*

class RecordingActivity : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.recording_screen, container, false)

        // recording button
        val microphoneBtn = root.findViewById<ImageButton>(R.id.microphoneBtn)
        microphoneBtn.setOnClickListener{
            // button description
            val buttonDescription = root.findViewById<TextView>(R.id.microphoneBtnDescription)

            if(!buttonPressed) {
                buttonPressed = true
                microphoneBtn.isActivated = true
                microphoneBtn.isSelected = true
                buttonDescription.text = buildString {
                    append("Press the button below to stop listening")
                }
            } else {
                buttonPressed = false
                microphoneBtn.isActivated = false
                microphoneBtn.isSelected = false
                buttonDescription.text = buildString {
                    append("Press the button below to start listening")
                }
            }
        }

        // get topic saved in local res
        val scanner  = Scanner(resources.openRawResource(R.raw.topics))
        readFile(scanner)

        // topics spinner
        val suggestedTopicSpinner = root.findViewById<Spinner>(R.id.suggestedTopicSpinner)

        spinAdapter = ArrayAdapter<String>(requireContext(), R.layout.selected_topic, topics)
        spinAdapter.setDropDownViewResource(R.layout.topic_spinner_dropdown)
        suggestedTopicSpinner.adapter = spinAdapter

        suggestedTopicSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>, p1: View?, p2: Int, p3: Long){
                topicChose = p0.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        // needs some machine learning algo to give the percentage
        disgustProgressVal = 90
        val disgustProgressBar = root.findViewById<ProgressBar>(R.id.disgustDegree)
        setProgressBar(disgustProgressBar, disgustProgressVal)

        happinessProgressVal = 50
        val happinessProgressBar = root.findViewById<ProgressBar>(R.id.happinessDegree)
        setProgressBar(happinessProgressBar, happinessProgressVal)

        sadnessProgressVal = 70
        val sadnessProgressBar = root.findViewById<ProgressBar>(R.id.sadnessDegree)
        setProgressBar(sadnessProgressBar, sadnessProgressVal)

        fearProgressVal = 10
        val fearProgressBar = root.findViewById<ProgressBar>(R.id.fearDegree)
        setProgressBar(fearProgressBar, fearProgressVal)

        angerProgressVal = 30
        val angerProgressBar = root.findViewById<ProgressBar>(R.id.angerDegree)
        setProgressBar(angerProgressBar, angerProgressVal)

        // change emotes on popup
        // if(){ // if the values change
        changeEmotePop(disgustProgressVal, happinessProgressVal, sadnessProgressVal, fearProgressVal, angerProgressVal)
        //}

        return root
    }

    // set progress bar
    private fun setProgressBar(view: ProgressBar, progressVal: Int) {
        // need to get value
        view.setProgress(progressVal, true)
    }

    private fun changeEmotePop(disgustDegree: Int, happinessDegree: Int, sadnessDegree: Int, fearDegree: Int, angerDegree: Int) {
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
            val emotionPopupFragmentManager = parentFragmentManager
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
//    private fun startRecording(microphoneBtn:ImageButton, buttonDescription:TextView): ImageButton {
//        if(!buttonPressed) {
//            buttonPressed = true
//            microphoneBtn.isActivated = true
//            microphoneBtn.isSelected = true
//            buttonDescription.text = buildString {
//                append("Press the button below to stop listening")
//            }
//        } else {
//            buttonPressed = false
//            microphoneBtn.isActivated = false
//            microphoneBtn.isSelected = false
//            buttonDescription.text = buildString {
//                append("Press the button below to start listening")
//            }
//        }
//        return microphoneBtn,buttonDescription
//    }

    // read topics from local res
    private fun readFile(scanner: Scanner){
        topics.add("Select a topic")
        while(scanner.hasNextLine()){
            val line = scanner.nextLine()
            topics.add(line)
        }
    }
}