package com.millivalley.chatapp_7feb.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECIEVER_ID = "recieverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_Reciever_NAME = "recieverName";
    public static final String KEY_Sender_IMAGE = "senderImage";
    public static final String KEY_Reciever_IMAGE = "recieverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String REMOTE_AUTHORIZATION = "Authorization";
    public static final String REMOTE_CONTENT_MSG_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTARTION_IDs = "registration_ids";


    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getremoteMsgHeaders() {
        if (remoteMsgHeaders == null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_AUTHORIZATION,
                    "AAAAK_xZ1Do:APA91bG_5VacS3EtpOhZQttIPObr4CeZOTcE9_N9HKQM9DULojEgl6AgaKeF_kKu6j3WYlKirpWGDiK2Kb8q3UVwlMOnmmj7qB-rH-V7g6Rpt46tzWkdAZzbRG5c4ju9SwIds1DgVV98"
            );
            remoteMsgHeaders.put(
              REMOTE_CONTENT_MSG_TYPE,
              "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}
