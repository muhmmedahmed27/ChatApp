package com.millivalley.chatapp_7feb.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.millivalley.chatapp_7feb.databinding.ItemContainerRecentConversationBinding;
import com.millivalley.chatapp_7feb.listeners.ConversionListener;
import com.millivalley.chatapp_7feb.models.ChatMessageModel;
import com.millivalley.chatapp_7feb.models.UserModel;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHoldaer>{

    private final List<ChatMessageModel> chatMessage;
    private final ConversionListener conversionListener;

    public RecentConversationsAdapter(List<ChatMessageModel> chatMessage, ConversionListener conversionListener) {
        this.chatMessage = chatMessage;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHoldaer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHoldaer(
                ItemContainerRecentConversationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHoldaer holder, int position) {
        holder.setData(chatMessage.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessage.size();
    }

    class ConversionViewHoldaer extends RecyclerView.ViewHolder {
        ItemContainerRecentConversationBinding binding;
        ConversionViewHoldaer(ItemContainerRecentConversationBinding itemContainerRecentConversationBinding) {
            super(itemContainerRecentConversationBinding.getRoot());
            binding = itemContainerRecentConversationBinding;
        }
        void setData(ChatMessageModel chatMessage) {
            binding.imgProfile.setImageBitmap(getConversationImg(chatMessage.conversionImage));
            binding.profileName.setText(chatMessage.conversionName);
            binding.recentMsg.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v -> {
                UserModel userModel = new UserModel();
                userModel.id = chatMessage.conversionId;
                userModel.name = chatMessage.conversionName;
                userModel.image = chatMessage.conversionImage;
                conversionListener.onConversionClicked(userModel);
            });
        }
    }

    private Bitmap getConversationImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
