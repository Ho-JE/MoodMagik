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
                "AAAA0C0L8FY:APA91bFAReyNn6D70SgaAJTeKWxMeW-xTPXKTzxB6asnik6igow4ieFrASDAlswzKPvm9gVyT-dVRj99Pxgh6Ef7d3LZR-GukCkUkFrT6527b4QjUnvRIf_dUUIruv6kf9w_df93mSky"
            )
            remoteMsgHeaders?.put(
                REMOTE_MSG_CONTENT_TYPE,
                "application/json"
            )
        }
        return remoteMsgHeaders as HashMap<String?, String?>
    }


}