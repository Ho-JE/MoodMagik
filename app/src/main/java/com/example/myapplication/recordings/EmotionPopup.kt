package com.example.myapplication.recordings
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R


class EmotionPopup : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_emotion_popup, container, false)

        // change the emote and description
        val emote = rootView.findViewById<ImageView>(R.id.emoteImg)
        val emoteDescription = rootView.findViewById<TextView>(R.id.emotionPopupDescription)

        // get data
        val bundle = arguments
        val maxEmote = bundle!!.getString("maxEmote")

        when (maxEmote) {
            "neutrality" -> {
                emote.setImageResource(R.drawable.neutrality_emotion)
                emoteDescription.text = buildString {
                    append("\u2022 Choose one of the topic from the dropdown to start a conversation \n\u2022 Ask how they have been \n\u2022 Excuse yourself if the conversation remains neutral")
                }
            }
            "happiness" -> {
                emote.setImageResource(R.drawable.happiness_emotion)
                emoteDescription.text = buildString {
                    append("\u2022 Share your thoughts on the topic \n\u2022 Listen \n\u2022 Express interests")
                }
            }
            "sadness" -> {
                emote.setImageResource(R.drawable.sadness_emotion)
                emoteDescription.text = buildString {
                    append("\u2022 Listen \n\u2022 Ask if the person is fine")
                }
            }
            "fear" -> {
                emote.setImageResource(R.drawable.fear_emotion)
                emoteDescription.text = buildString {
                    append("\u2022 Ask if the person is fine \n\u2022 Ask if they need help")
                }
            }
            "anger" -> {
                emote.setImageResource(R.drawable.anger_emotion)
                emoteDescription.text = buildString {
                    append("\u2022 Switch topics \n\u2022 Calm the other parties down \n\u2022 Leave if the conversation is too heated")
                }
            }
        }

        // button to close the fragment
        val closeBtn = rootView.findViewById<Button>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            dismiss()
        }

        return rootView
    }
}
