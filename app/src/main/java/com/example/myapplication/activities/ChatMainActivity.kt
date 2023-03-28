package com.example.myapplication.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.example.myapplication.adapters.RecentConversationAdapter
import com.example.myapplication.databinding.ActivityChatMainBinding
import com.example.myapplication.listeners.ConversionListener
import com.example.myapplication.models.ChatMessage
import com.example.myapplication.models.User
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*


class ChatMainActivity : BaseActivity(), ConversionListener {
    private var binding: ActivityChatMainBinding? = null
    private var preferenceManager: PreferenceManager? = null
    private var conversations: MutableList<ChatMessage>? = null
    private var conversationAdapter: RecentConversationAdapter? = null
    private var database: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        preferenceManager = PreferenceManager(applicationContext)
        init()
        loadUserDetails()
        token
        setListeners()
        listenConversations()
    }

    private fun init() {
        conversations = ArrayList()
        conversationAdapter = RecentConversationAdapter(conversations as ArrayList<ChatMessage>, this)
        binding!!.conversationsRecyclerView.adapter = conversationAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun setListeners() {
        binding!!.imageSignOut.setOnClickListener { v: View? -> signOut() }
        binding!!.fabNewChat.setOnClickListener { v: View? -> startActivity(Intent(applicationContext, UserActivity::class.java)) }
    }

    private fun loadUserDetails() {
        binding!!.textName.text = preferenceManager!!.getString(Constants.KEY_NAME)
        val bytes = Base64.decode(preferenceManager!!.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        binding!!.imageProfile.setImageBitmap(bitmap)
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun listenConversations() {
        database!!.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager!!.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
        database!!.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager!!.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener = EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
        if (error == null){
            if (value != null) {
                for (documentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        val chatMessage = ChatMessage()
                        chatMessage.senderId = senderId
                        chatMessage.receiverId = receiverId
                        if (preferenceManager!!.getString(Constants.KEY_USER_ID) == senderId) {
                            chatMessage.conversionImage = documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)
                            chatMessage.conversionName = documentChange.document.getString(Constants.KEY_RECEIVER_NAME)
                            chatMessage.conversionId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        } else {
                            chatMessage.conversionImage = documentChange.document.getString(Constants.KEY_SENDER_IMAGE)
                            chatMessage.conversionName = documentChange.document.getString(Constants.KEY_SENDER_NAME)
                            chatMessage.conversionId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        }
                        chatMessage.message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                        chatMessage.dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                        conversations!!.add(chatMessage)
                    } else if (documentChange.type == DocumentChange.Type.MODIFIED) {
                        var i = 0
                        while (i < conversations!!.size) {
                            val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                            val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                            if (conversations!![i].senderId == senderId && conversations!![i].receiverId == receiverId) {
                                conversations!![i].message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                                conversations!![i].dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                                break
                            }
                            i++
                        }
                    }
                }
                Collections.sort(conversations) { obj1: ChatMessage, obj2: ChatMessage -> obj2.dateObject!!.compareTo(obj1.dateObject) }
                conversationAdapter!!.notifyDataSetChanged()
                binding!!.conversationsRecyclerView.smoothScrollToPosition(0)
                binding!!.conversationsRecyclerView.visibility = View.VISIBLE
                binding!!.progressBar.visibility = View.GONE
            }
        }

    }
    private val token: Unit
        private get() {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String -> updateToken(token) }
        }

    private fun updateToken(token: String) {
        preferenceManager!!.putString(Constants.KEY_FCM_TOKEN, token)
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager!!.getString(Constants.KEY_USER_ID)!!
        )
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnSuccessListener { unused: Void? -> showToast("Token updated successfully") }
            .addOnFailureListener { e: Exception? -> showToast("Unable to update token") }
    }

    private fun signOut() {
        showToast("Signing out...")
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager!!.getString(Constants.KEY_USER_ID)!!
        )
        val updates = HashMap<String, Any>()
        updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
        documentReference.update(updates)
            .addOnSuccessListener { unused: Void? ->
                preferenceManager!!.clear()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
            }
            .addOnFailureListener { e: Exception? -> showToast("Unable to sign out") }
    }

    override fun onConversionClicked(user: User?) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
    }
}