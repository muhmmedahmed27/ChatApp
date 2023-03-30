package com.millivalley.chatapp_7feb.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.millivalley.chatapp_7feb.R;
import com.millivalley.chatapp_7feb.adapters.UserAdapter;
import com.millivalley.chatapp_7feb.listeners.UserListener;
import com.millivalley.chatapp_7feb.models.UserModel;
import com.millivalley.chatapp_7feb.utilities.Constants;
import com.millivalley.chatapp_7feb.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {
//    private String TAG = "wow123";
    private ProgressBar progressBar;
    private TextView errorMsgTV;
    private RecyclerView userRecyclerView;
    private AppCompatImageView backImg;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        progressBar = findViewById(R.id.progressBar);
        errorMsgTV = findViewById(R.id.textErrorMessage);
        backImg = findViewById(R.id.backBtn);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
        getUser();
    }

    private void setListeners() {
        backImg.setOnClickListener(v -> onBackPressed());
    }

    private void getUser() {
        loading(true);
        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
        dataBase.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
            loading(false);
            String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
            if (task.isSuccessful() && task.getResult() != null) {
                List<UserModel> modelList = new ArrayList<>();
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                        continue;
                    }
                    UserModel list = new UserModel();
                    list.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                    list.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                    list.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                    list.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                    list.id = queryDocumentSnapshot.getId();
                    modelList.add(list);
                }
                if (modelList.size() > 0) {
                    userRecyclerView.setAdapter(new UserAdapter(modelList, this));
                    userRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    showErrorMsg();
                }
            } else {
                showErrorMsg();
            }
        });
    }

    private void showErrorMsg() {
        errorMsgTV.setText(String.format("%s", "No user available"));
        errorMsgTV.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(UserModel userModel) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, userModel);
        startActivity(intent);
        finish();
    }
}