package com.example.myapplication.recordings

import WavFileBuilder
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
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
import com.example.myapplication.classifiers.MFCCProcessing
import com.example.myapplication.tasks.MoodMagicApplication
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class RecordingActivity : Fragment() {
    private lateinit var topicChose: String
    private var disgustProgressVal = 0
    private var happinessProgressVal = 0
    private var sadnessProgressVal = 0
    private var fearProgressVal = 0
    private var angerProgressVal = 0
    private var topics = ArrayList<String>()

    // adapter for topic spinner
    private lateinit var spinAdapter: ArrayAdapter<String>

    // whether microphone button is pressed
    private var buttonPressed = false

    private lateinit var recordingName: String
    private var output: String? = null // path for recording
    private var recorderState: Boolean = false
    private var timeList: ArrayList<String> = ArrayList()
    private var emotionList: ArrayList<String> = ArrayList()
    private var funtimer: Timer = Timer()
    private val voiceRecorder = VoiceRecorder()
    private val SAMPLE_RATE = 48000
    private var startTime = System.currentTimeMillis()

    // roomdb
    // In the recordings module
    val recordingViewModel: RecordingViewModel by viewModels {
        val application = requireActivity().application as MoodMagicApplication
        RecordingViewModel.RecordingItemModelFactory(application.recordingRepository)
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
        microphoneBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED &&
                !Environment.isExternalStorageManager()
            ) {
                val permissions = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(requireActivity(), permissions, 1)
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
        val scanner = Scanner(resources.openRawResource(R.raw.topics))
        readFile(scanner)

        // topics spinner
        val suggestedTopicSpinner = root.findViewById<Spinner>(R.id.suggestedTopicSpinner)

        spinAdapter =
            ArrayAdapter<String>(requireContext(), R.layout.selected_topic, topics.shuffled())
        spinAdapter.setDropDownViewResource(R.layout.topic_spinner_dropdown)
        suggestedTopicSpinner.adapter = spinAdapter

        suggestedTopicSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
                topicChose = p0.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        // needs some machine learning algo to give the percentage
        disgustProgressVal = 0
        val disgustProgressBar = root.findViewById<ProgressBar>(R.id.disgustDegree)
        setProgressBar(disgustProgressBar, disgustProgressVal)

        happinessProgressVal = 0
        val happinessProgressBar = root.findViewById<ProgressBar>(R.id.happinessDegree)
        setProgressBar(happinessProgressBar, happinessProgressVal)

        sadnessProgressVal = 0
        val sadnessProgressBar = root.findViewById<ProgressBar>(R.id.sadnessDegree)
        setProgressBar(sadnessProgressBar, sadnessProgressVal)

        fearProgressVal = 0
        val fearProgressBar = root.findViewById<ProgressBar>(R.id.fearDegree)
        setProgressBar(fearProgressBar, fearProgressVal)

        angerProgressVal = 0
        val angerProgressBar = root.findViewById<ProgressBar>(R.id.angerDegree)
        setProgressBar(angerProgressBar, angerProgressVal)

        // change emotes on popup
        // if(){ // if the values change
        changeEmotePop(
            disgustProgressVal,
            happinessProgressVal,
            sadnessProgressVal,
            fearProgressVal,
            angerProgressVal
        )
        //}

        return root
    }

    // set progress bar
    private fun setProgressBar(view: ProgressBar, progressVal: Int) {
        // need to get value
        view.setProgress(progressVal, true)
    }

    private fun changeEmotePop(
        disgustDegree: Int,
        happinessDegree: Int,
        sadnessDegree: Int,
        fearDegree: Int,
        angerDegree: Int
    ) {
        val progressValMap = mapOf(
            "disgust" to disgustDegree,
            "happiness" to happinessDegree,
            "sadness" to sadnessDegree,
            "fear" to fearDegree,
            "anger" to angerDegree
        )

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

        if (maxKey != null) {
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
    private fun recording(view: View) {
        // recording button
        val microphoneBtn = view.findViewById<ImageButton>(R.id.microphoneBtn)
        // button description
        val buttonDescription = view.findViewById<TextView>(R.id.microphoneBtnDescription)

        if (!recorderState) {
            buttonPressed = true
            microphoneBtn.isActivated = true
            microphoneBtn.isSelected = true
            buttonDescription.text = buildString {
                append("Press the button below to stop listening")
            }
            startRecording(view)
        } else {
            buttonPressed = false
            microphoneBtn.isActivated = false
            microphoneBtn.isSelected = false
            buttonDescription.text = buildString {
                append("Press the button below to start listening")
            }
            stopRecording(view)
        }
    }

    // read topics from local res
    private fun readFile(scanner: Scanner) {
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            topics.add(line)
        }
    }

    private fun startRecording(view: View) {
        //Initialize empty lists to store
        timeList = ArrayList()
        emotionList = ArrayList()
        //Initialize recording start
        startTime = System.currentTimeMillis()

        // recorder
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmm ss", Locale.getDefault()).format(Date())
        recordingName = "/EmotionRecording_$timestamp.wav"
        output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            .toString() + recordingName

        Log.d("output saved?", output.toString())
        voiceRecorder.prepare(SAMPLE_RATE, 10000).start()

        recorderState = true
        Toast.makeText(requireContext(), "Recording started!", Toast.LENGTH_SHORT).show()


        // start sending data to M
        funtimer = Timer()
        funtimer.scheduleAtFixedRate(
            timerTask()
            {
                //ml function here
                saveRecord(false, output!!, view)
            }, 10000, 10000
        )
    }


    private fun saveRecord(stop: Boolean, filepath: String, view: View) {
        var wavFile = WavFileBuilder()
            .setAudioFormat(WavFileBuilder.PCM_AUDIO_FORMAT)
            .setSampleRate(SAMPLE_RATE)
            .setBitsPerSample(WavFileBuilder.BITS_PER_SAMPLE_16)
            .setNumChannels(WavFileBuilder.CHANNELS_STEREO)
            .setSubChunk1Size(WavFileBuilder.SUBCHUNK_1_SIZE_PCM)
            .build(voiceRecorder.stopShort())
        if (stop) {
            funtimer.cancel()
            wavFile = WavFileBuilder()
                .setAudioFormat(WavFileBuilder.PCM_AUDIO_FORMAT)
                .setSampleRate(SAMPLE_RATE)
                .setBitsPerSample(WavFileBuilder.BITS_PER_SAMPLE_16)
                .setNumChannels(WavFileBuilder.CHANNELS_MONO)
                .setSubChunk1Size(WavFileBuilder.SUBCHUNK_1_SIZE_PCM)
                .build(voiceRecorder.stop())
        }
        var output = HashMap<String, Int>()

        if (!stop) {
            val rootFolder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val file = File(rootFolder, "/test.wav")
            if (!file.exists()) file.createNewFile()
            val fos = FileOutputStream(file)
            fos.write(wavFile)
            fos.flush()
            fos.close()
            val MFCC = MFCCProcessing(requireContext(), file.toString())
            output = MFCC.process(requireContext())
        } else {
            val fos = FileOutputStream(filepath)
            fos.write(wavFile)
            fos.flush()
            fos.close()
            val MFCC = MFCCProcessing(requireContext(), filepath!!)
            output = MFCC.process(requireContext())
        }

        // needs some machine learning algo to give the percentage
        disgustProgressVal = output["Neutral"]!!
        val disgustProgressBar = view.findViewById<ProgressBar>(R.id.disgustDegree)
        setProgressBar(disgustProgressBar, disgustProgressVal)

        happinessProgressVal = output["Happy"]!!
        val happinessProgressBar = view.findViewById<ProgressBar>(R.id.happinessDegree)
        setProgressBar(happinessProgressBar, happinessProgressVal)

        sadnessProgressVal = output["Sad"]!!
        val sadnessProgressBar = view.findViewById<ProgressBar>(R.id.sadnessDegree)
        setProgressBar(sadnessProgressBar, sadnessProgressVal)

        fearProgressVal =  output["Fear"]!!
        val fearProgressBar = view.findViewById<ProgressBar>(R.id.fearDegree)
        setProgressBar(fearProgressBar, fearProgressVal)

        angerProgressVal = output["Angry"]!!
        val angerProgressBar = view.findViewById<ProgressBar>(R.id.angerDegree)
        setProgressBar(angerProgressBar, angerProgressVal)

        changeEmotePop(
            disgustProgressVal,
            happinessProgressVal,
            sadnessProgressVal,
            fearProgressVal,
            angerProgressVal
        )

        val probableEmotion = output.entries.maxByOrNull { it.value }?.key
        val currentTime = System.currentTimeMillis() - startTime

        val currentSeconds = (currentTime / 1000) % 60
        val currentMinutes = currentTime / (1000 * 60)

        timeList.add(" $currentMinutes:$currentSeconds")
        emotionList.add(probableEmotion!!)


    }

    private fun stopRecording(view: View) {
        if (recorderState) {
            saveRecord(true, output!!, view)
            recorderState = false
            Toast.makeText(requireContext(), "You have stopped the recording!", Toast.LENGTH_SHORT)
                .show()

            Log.d("Time list", timeList.toString())
            Log.d("emo list", emotionList.toString())

            processRecordingData(
                recordingName,
                timeList,
                emotionList,
                LocalDate.now(ZoneId.of("Asia/Singapore"))
            )

        } else {
            Toast.makeText(requireContext(), "You are not recording right now!", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun processRecordingData(
        recordingName: String,
        timeList: ArrayList<String>,
        emotionList: ArrayList<String>,
        Date: LocalDate
    ) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateString = Date.format(formatter)
        val recordingItem = RecordingItem(recordingName,RecordingItem.fromArrayList(timeList),
            RecordingItem.fromArrayList( emotionList), dateString,timeList[timeList.size-1])
        recordingViewModel.addRecordingItem(recordingItem)
    }


}

