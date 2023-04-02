package com.example.myapplication.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemContainerReceivedMessageBinding
import com.example.myapplication.databinding.ItemContainerSentMessageBinding
import com.example.myapplication.models.ChatMessage

class ChatAdapter(private val chatMessages: List<ChatMessage>, private var receiverProfileImage: Bitmap, private val senderId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun setReceiverProfileImage(bitmap: Bitmap) {
        receiverProfileImage = bitmap
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(
                ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ReceivedMessageViewHolder(
                ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            (holder as SentMessageViewHolder).setData(chatMessages[position])
        } else {
            (holder as ReceivedMessageViewHolder).setData(chatMessages[position], receiverProfileImage)
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].senderId == senderId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    internal class SentMessageViewHolder(private val binding: ItemContainerSentMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: ChatMessage) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
            if (chatMessage.emotion == "sad"){
                binding.textEmotionEmoji.setImageResource(R.drawable.sad)
                binding.textEmotionEmoji.setTag(R.string.image_tag, "sad")
            }
            else if (chatMessage.emotion == "happy"){
                binding.textEmotionEmoji.setImageResource(R.drawable.happy)
                binding.textEmotionEmoji.setTag(R.string.image_tag, "happy")
            }
            else if (chatMessage.emotion == "fear"){
                binding.textEmotionEmoji.setImageResource(R.drawable.fear_emotion)
                binding.textEmotionEmoji.setTag(R.string.image_tag, "fear_emotion")
            }
            else if (chatMessage.emotion == "angry"){
                binding.textEmotionEmoji.setImageResource(R.drawable.angry)
                binding.textEmotionEmoji.setTag(R.string.image_tag, "angry")
            }

        }
    }

    internal class ReceivedMessageViewHolder(private val binding: ItemContainerReceivedMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: ChatMessage, receiverProfileImage: Bitmap?) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
            if (receiverProfileImage != null) {
                binding.imageProfile.setImageBitmap(receiverProfileImage)
            }
            if (chatMessage.emotion == "sad"){
                binding.textEmotionEmoji.setImageResource(R.drawable.sad)
                binding.textEmotionEmoji.setTag(R.string.image_tag, "sad")
            }
            else if (chatMessage.emotion == "happy"){
                binding.textEmotionEmoji.setImageResource(R.drawable.happy)
                binding.textEmotionEmoji.setTag(R.string.image_tag, "happy")
            }
            else if (chatMessage.emotion == "fear"){
                binding.textEmotionEmoji.setImageResource(R.drawable.fear_emotion)
                binding.textEmotionEmoji.setTag(R.string.image_tag, "fear_emotion")
            }
            else if (chatMessage.emotion == "angry"){
                binding.textEmotionEmoji.setImageResource(R.drawable.angry)
                binding.textEmotionEmoji.setTag(R.string.image_tag, "angry")
            }
        }
    }

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }
}