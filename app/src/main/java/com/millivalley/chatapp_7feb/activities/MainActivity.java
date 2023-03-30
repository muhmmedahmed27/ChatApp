package com.millivalley.chatapp_7feb.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;
import com.millivalley.chatapp_7feb.R;
import com.millivalley.chatapp_7feb.adapters.RecentConversationsAdapter;
import com.millivalley.chatapp_7feb.listeners.ConversionListener;
import com.millivalley.chatapp_7feb.models.ChatMessageModel;
import com.millivalley.chatapp_7feb.models.UserModel;
import com.millivalley.chatapp_7feb.utilities.Constants;
import com.millivalley.chatapp_7feb.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ConversionListener {

    private String TAG = "wow123";
    private PreferenceManager preferenceManager;
    private TextView textName;
    RecentConversationsAdapter adapter;
    private RoundedImageView imgProfile;
    private AppCompatImageView imgSignout;
    private List<ChatMessageModel> conversions;
    private FloatingActionButton newChatBtn;
    private RecyclerView conversationRecyclerView;
    private FirebaseFirestore database;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textName = findViewById(R.id.textName);
        progressBar = findViewById(R.id.progressBar);
        conversationRecyclerView = findViewById(R.id.conversationRecyclerView);
        imgProfile = findViewById(R.id.profileImage_MA);
        imgSignout = findViewById(R.id.imgSignOut);
        newChatBtn = findViewById(R.id.addNewChat);
        preferenceManager = new PreferenceManager(getApplicationContext());

        init();
        setListeners();
        loadUserDetails();
        getToken();
        listenerConversation();
    }

    private void setListeners() {
        imgSignout.setOnClickListener(v -> signOut());
        newChatBtn.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }

    private void loadUserDetails() {
        textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imgProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }

    private void listenerConversation() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECIEVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String recieverId = documentChange.getDocument().getString(Constants.KEY_RECIEVER_ID);
                    ChatMessageModel chatMessages = new ChatMessageModel();
                    chatMessages.senderId = senderId;
                    chatMessages.recieverId = recieverId;
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessages.conversionImage = documentChange.getDocument().getString(Constants.KEY_Reciever_IMAGE);
                        chatMessages.conversionName = documentChange.getDocument().getString(Constants.KEY_Reciever_NAME);
                        chatMessages.conversionId = documentChange.getDocument().getString(Constants.KEY_RECIEVER_ID);
                    } else {
                        chatMessages.conversionImage = documentChange.getDocument().getString(Constants.KEY_Sender_IMAGE);
                        chatMessages.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessages.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessages.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessages.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversions.add(chatMessages);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversions.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String recieverId = documentChange.getDocument().getString(Constants.KEY_RECIEVER_ID);
                        if (conversions.get(i).senderId.equals(senderId) && conversions.get(i).recieverId.equals(recieverId)) {
                            conversions.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversions.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversions, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            adapter.notifyDataSetChanged();
            conversationRecyclerView.smoothScrollToPosition(0);
            conversationRecyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    };

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(this::updateToken)
                .addOnFailureListener(exception -> showToast("Unable to Update Token"));
        Log.d(TAG, "getToken: " + FirebaseMessaging.getInstance().getToken());
    }

    private void updateToken(String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
        DocumentReference documentReference = dataBase.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(exception -> showToast("Unable to Update Token"));
        Log.d(TAG, "updateToken: " + token);
    }

    private void init() {
        conversions = new ArrayList<>();
        adapter = new RecentConversationsAdapter(conversions, this);
        conversationRecyclerView.setAdapter(adapter);
        database = FirebaseFirestore.getInstance();
    }

    private void signOut() {
        showToast("Signing Out.....");
        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();

        DocumentReference documentReference = dataBase.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {
            preferenceManager.clear();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }).addOnFailureListener(e -> showToast("Unable to Sign Out"));
    }

    @Override
    public void onConversionClicked(UserModel userModel) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, userModel);
        startActivity(intent);
    }
}