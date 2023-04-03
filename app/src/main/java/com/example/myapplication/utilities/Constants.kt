package com.example.myapplication.utilities

object Constants {
    const val KEY_COLLECTION_USERS = "users"
    const val KEY_NAME = "name"
    const val KEY_EMAIL = "email"
    const val KEY_PASSWORD = "password"
    const val KEY_PREFERENCE_NAME = "chatAppPreference"
    const val KEY_IS_SIGNED_IN = "isSignedIn"
    const val KEY_USER_ID = "userId"
    const val KEY_IMAGE = "image"
    const val KEY_FCM_TOKEN = "fcmToken"
    const val KEY_USER = "user"
    const val KEY_COLLECTION_CHAT = "chat"
    const val KEY_SENDER_ID = "senderId"
    const val KEY_RECEIVER_ID = "receiverId"
    const val KEY_MESSAGE = "message"
    const val KEY_TIMESTAMP = "timestamp"
    const val KEY_EMOTION = "emotion"
    const val KEY_COLLECTION_CONVERSATIONS = "conversations"
    const val KEY_SENDER_NAME = "senderName"
    const val KEY_RECEIVER_NAME = "receiverName"
    const val KEY_SENDER_IMAGE = "senderImage"
    const val KEY_RECEIVER_IMAGE = "receiverImage"
    const val KEY_LAST_MESSAGE = "lastMessage"
    const val KEY_AVAILABILITY = "availablity"
    const val REMOTE_MSG_AUTHORIZATION = "Authorization"
    const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
    const val REMOTE_MSG_DATA = "data"
    const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"
    private var remoteMsgHeaders: HashMap<String, String>? = null

    fun getRemoteMsgHeaders(): HashMap<String?, String?> {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = HashMap()
            remoteMsgHeaders?.put(
                REMOTE_MSG_AUTHORIZATION,
                "AAAAcQrjAz8:APA91bGT1b27aV1ngJXUqOBL4yUjqFhseXJLUJEq_fkvWIzkOgxuWXyK3glMa2chhG2sVyhmotLArIGG3yUML5xL8p2esUJBHqbW6vxK-7tsCmS4Ghy23Vdm3ePOM2of7Sg3b1jSpjfr"
            )
            remoteMsgHeaders?.put(
                REMOTE_MSG_CONTENT_TYPE,
                "application/json"
            )
        }
        return remoteMsgHeaders as HashMap<String?, String?>
    }


}