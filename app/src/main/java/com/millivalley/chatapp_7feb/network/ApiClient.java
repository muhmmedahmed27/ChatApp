package com.millivalley.chatapp_7feb.network;

import retrofit2.Retrofit;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClent() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl("https://fcm.google.com/fcm/").build();
        }
        return retrofit;
    }
}
