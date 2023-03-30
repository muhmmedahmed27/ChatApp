package com.millivalley.chatapp_7feb.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.millivalley.chatapp_7feb.R;
import com.millivalley.chatapp_7feb.listeners.UserListener;
import com.millivalley.chatapp_7feb.models.UserModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final List<UserModel> modelList;
    private final UserListener userListener;

    public UserAdapter(List<UserModel> modelList, UserListener userListener) {
        this.modelList = modelList;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(modelList.get(position));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView profileImg;
        private TextView profileName, profileEmail;
//        ItemContainerUserBinding binding;

//        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
//            super(itemContainerUserBinding.getRoot());
//            binding = itemContainerUserBinding;
//        }

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.profileName);
            profileEmail = itemView.findViewById(R.id.profileEmail);
            profileImg = itemView.findViewById(R.id.imgProfile);
        }

        void setUserData(UserModel userModel) {
            profileName.setText(userModel.name);
            profileEmail.setText(userModel.email);
            profileImg.setImageBitmap(getUserImage(userModel.image));
            itemView.getRootView().setOnClickListener(v -> userListener.onUserClicked(userModel));
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
