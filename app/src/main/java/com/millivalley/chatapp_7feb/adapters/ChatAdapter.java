package com.millivalley.chatapp_7feb.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.millivalley.chatapp_7feb.databinding.ItemContainerReceivedMessageBinding;
import com.millivalley.chatapp_7feb.databinding.ItemContainerSendMessageBinding;
import com.millivalley.chatapp_7feb.models.ChatMessageModel;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessageModel> modelList;
    private Bitmap reciverProfileImg;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECIEVE = 2;

    public void setReciverProfileImg(Bitmap bitmap) {
        reciverProfileImg = bitmap;
    }

    public ChatAdapter(List<ChatMessageModel> modelList, Bitmap reciverProfileImg, String senderId) {
        this.modelList = modelList;
        this.reciverProfileImg = reciverProfileImg;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSendMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        } else {
            return new recieveMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(modelList.get(position));
        } else {
            ((recieveMessageViewHolder) holder).setData(modelList.get(position), reciverProfileImg);
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (modelList.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECIEVE;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        public final ItemContainerSendMessageBinding binding;

        SentMessageViewHolder(ItemContainerSendMessageBinding itemContainerSendMessageBinding) {
            super(itemContainerSendMessageBinding.getRoot());
            binding = itemContainerSendMessageBinding;
        }

        void setData(ChatMessageModel messageModel) {
            binding.textMessageSM.setText(messageModel.message);
            binding.textDateTimeSM.setText(messageModel.dateTime);
        }
    }

    public static class recieveMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        public recieveMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessageModel messageModel, Bitmap recieverProfileImg) {
            binding.textMessageRM.setText(messageModel.message);
            binding.textDateTimeRM.setText(messageModel.dateTime);
            if (recieverProfileImg != null) {
                binding.profileImgRM.setImageBitmap(recieverProfileImg);
            }
        }
    }
}
