package com.millivalley.chatapp_7feb.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.millivalley.chatapp_7feb.R;
import com.millivalley.chatapp_7feb.utilities.Constants;
import com.millivalley.chatapp_7feb.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private String encodedImage;
    private String TAG = "wow123";

    private PreferenceManager preferenceManager;
    private FrameLayout layoutImage;
    private RoundedImageView profileImage;
    private ProgressBar progressBar;
    private MaterialButton signUpBtn;
    private TextView signInText, addImageTV;
    private EditText nameET, emailET, passwordET, confirmPasswordET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signInText = findViewById(R.id.signInText);
        addImageTV = findViewById(R.id.addImageTV);
        nameET = findViewById(R.id.inputName_SignUp);
        emailET = findViewById(R.id.inputEmail_SignUp);
        passwordET = findViewById(R.id.inputPassword_SignUp);
        confirmPasswordET = findViewById(R.id.inputConfirmPassword_SignIn);
        signUpBtn = findViewById(R.id.signUpbtn_SignUp);
        progressBar = findViewById(R.id.progressBar);
        profileImage = findViewById(R.id.imageProfile);
        layoutImage = findViewById(R.id.layoutImage);
        preferenceManager = new PreferenceManager(getApplicationContext());

        setListener();
    }

    private void setListener() {
        signInText.setOnClickListener(v -> onBackPressed());
        signUpBtn.setOnClickListener(v -> {
            if (validationSignUp()) {
                signUp();
                Log.d(TAG, "validationSignUp: Validation Successful" + validationSignUp());
            }
            Log.d(TAG, "SignUpBtn: SignUp Button Clicked");
        });
        layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
            Log.d(TAG, "Layout Image: " + pickImage);
        });
        Log.d(TAG, "setListner: Fuction Apply");
    }

    private void showToast(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "showToast: " + message);
    }

    private void signUp() {
        loading(true);
        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, nameET.getText().toString());
        user.put(Constants.KEY_EMAIL, emailET.getText().toString());
        user.put(Constants.KEY_PASSWORD, passwordET.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);

        dataBase.collection(Constants.KEY_COLLECTION_USERS).add(user).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "OnSuccessListener: " + dataBase);
            loading(false);
            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
            preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
            preferenceManager.putString(Constants.KEY_NAME, nameET.getText().toString());
            preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }).addOnFailureListener(exception -> {
            loading(false);
            Log.d(TAG, "OnFailureListener: " + exception.getMessage());
        });
    }

    private String encodedImage(Bitmap bitmap) {

        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewbitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewbitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        Log.d(TAG, "Uri Image: ImageURI" + imageUri);

                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            profileImage.setImageBitmap(bitmap);
                            Log.d(TAG, "Profile Image: Image Uploaded" + profileImage);
                            addImageTV.setVisibility(View.GONE);
                            Log.d(TAG, "Image Text: Visibility is GONE" + addImageTV);
                            encodedImage = encodedImage(bitmap);
                            Log.d(TAG, "Encoded Image: Image" + encodedImage);
                        } catch (FileNotFoundException e) {
                            Log.d(TAG, "Error: There is a PROBLEM" + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            });

    private boolean validationSignUp() {
        if (encodedImage == null) {
            showToast("Select Profile Pic");
            return false;
        } else if (nameET.getText().toString().trim().isEmpty()) {
            showToast("Enter Your Name");
            return false;
        } else if (emailET.getText().toString().trim().isEmpty()) {
            showToast("Enter Your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()) {
            showToast("Enter Valid Email");
            return false;
        } else if (passwordET.getText().toString().trim().isEmpty()) {
            showToast("Enter Your Password");
            return false;
        } else if (confirmPasswordET.getText().toString().trim().isEmpty()) {
            showToast("Confirm Your Password");
            return false;
        } else if (!passwordET.getText().toString().equals(confirmPasswordET.getText().toString())) {
            showToast("Password & Confirm Password Must be SAME");
            return false;
        } else {
            return true;
        }
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            signUpBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            signUpBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
        Log.d(TAG, "loading: Loading......." + isLoading);
    }

}