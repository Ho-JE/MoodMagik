package com.example.myapplication.recordings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.tasks.MoodMagicApplication
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask


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
    // recorder
    private var mediaRecorder: MediaRecorder? = null
    private lateinit var recordingName: String
    private var output: String? = null // path for recording
    private var recorderState: Boolean = false
    private var timeList: ArrayList<Date> = ArrayList()
    private var emotionList: ArrayList<String> = ArrayList()
    private val funtimer: Timer = Timer()

    // roomdb
    val recordingViewModel: RecordingViewModel by viewModels {
        val application = requireActivity().application
        RecordingViewModel.RecordingItemModelFactory((application as MoodMagicApplication).repository)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_recording_screen, container, false)

        // recording button
        val microphoneBtn = root.findViewById<ImageButton>(R.id.microphoneBtn)
        microphoneBtn.setOnClickListener{
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                !Environment.isExternalStorageManager()) {
                val permissions = arrayOf(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(requireActivity(), permissions,1)
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            } else {
                recording(root)
            }
        }

        // get topic saved in local res
        val scanner  = Scanner(resources.openRawResource(R.raw.topics))
        readFile(scanner)

        // topics spinner
        val suggestedTopicSpinner = root.findViewById<Spinner>(R.id.suggestedTopicSpinner)

        spinAdapter = ArrayAdapter<String>(requireContext(), R.layout.selected_topic, topics.shuffled())
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
    private fun recording(view:View) {
        // recording button
        val microphoneBtn = view.findViewById<ImageButton>(R.id.microphoneBtn)
        // button description
        val buttonDescription = view.findViewById<TextView>(R.id.microphoneBtnDescription)

        if(!recorderState) {
            buttonPressed = true
            microphoneBtn.isActivated = true
            microphoneBtn.isSelected = true
            buttonDescription.text = buildString {
                append("Press the button below to stop listening")
            }
            startRecording()
        } else {
            buttonPressed = false
            microphoneBtn.isActivated = false
            microphoneBtn.isSelected = false
            buttonDescription.text = buildString {
                append("Press the button below to start listening")
            }
            stopRecording()
        }
    }

    // read topics from local res
    private fun readFile(scanner: Scanner){
        while(scanner.hasNextLine()){
            val line = scanner.nextLine()
            topics.add(line)
        }
    }

    private fun startRecording() {
        try {
            if(mediaRecorder == null) {
                // recorder
                val timestamp =
                    SimpleDateFormat("yyyyMMdd_HHmm ss", Locale.getDefault()).format(Date())
                recordingName = "/EmotionRecording_$timestamp.mp3"
                output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString() + recordingName

                Log.d("output saved?", output.toString())

                mediaRecorder = MediaRecorder()

                mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                println("media recorder set audio source")
                mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                println("media recorder set output format")
                mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                println("media recorder set encoder")
                mediaRecorder?.setOutputFile(output)
                println("media recorder set output file")
            }
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            recorderState = true
            Toast.makeText(requireContext(), "Recording started!", Toast.LENGTH_SHORT).show()

            // start sending data to ML
            funtimer.scheduleAtFixedRate(
                timerTask()
                {
                    sendDataToML()
                }, 10000, 10000)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording(){
        if(recorderState){
            mediaRecorder?.stop()
            mediaRecorder?.release()
            recorderState = false
            mediaRecorder = null
            Toast.makeText(requireContext(), "You have stopped the recording!", Toast.LENGTH_SHORT).show()

            // stop sending data to ML
            processRecordingData(recordingName, timeList, emotionList)
        }else{
            Toast.makeText(requireContext(), "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendDataToML(){
        // ml

    }

    private fun processRecordingData(recordingName:String, timeList:ArrayList<Date>, emotionList:ArrayList<String>){


//        recordingViewModel.addRecordingItem(newRecording)
    }
}