package com.millivalley.chatapp_7feb.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.millivalley.chatapp_7feb.R;
import com.millivalley.chatapp_7feb.utilities.Constants;
import com.millivalley.chatapp_7feb.utilities.PreferenceManager;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    private String TAG = "wow123";
    private PreferenceManager preferenceManager;
    private FrameLayout layoutSignIn;
    private ProgressBar progressBar;
    private MaterialButton signInBtn;
    private TextView newAccount;
    private EditText emailET, passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        newAccount = findViewById(R.id.createNewAccount);
        signInBtn = findViewById(R.id.signInbtn_SignIn);
        emailET = findViewById(R.id.inputEmail_SignIn);
        passwordET = findViewById(R.id.inputPassword_SignIn);
        progressBar = findViewById(R.id.progressBar);

        setListeners();

    }

    private void setListeners() {
        newAccount.setOnClickListener(v ->
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
        signInBtn.setOnClickListener(v -> {
            if (validationSignIn()) {
                signIn();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }

    private void signIn() {
        loading(true);
        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
        dataBase.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo(Constants.KEY_EMAIL, emailET.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, passwordET.getText().toString()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to Sign In");
                    }
                }).addOnFailureListener(exception -> {

                });
    }

    private Boolean validationSignIn() {
        if (emailET.getText().toString().trim().isEmpty()) {
            showToast("Enter Your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()) {
            showToast("Enter Valid Email");
            return false;
        } else if (passwordET.getText().toString().trim().isEmpty()) {
            showToast("Enter Your Password");
            return false;
        } else {
            return true;
        }
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            signInBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            signInBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
        Log.d(TAG, "loading: Loading......." + isLoading);
    }


}