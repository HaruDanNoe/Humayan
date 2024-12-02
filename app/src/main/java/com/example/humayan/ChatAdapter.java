package com.example.humayan;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        // Handle visibility and background based on whether the message is from the user or the system
        if (chatMessage.isUser()) {
            // Show user message (aligned to the right)
            holder.userMessageCard.setVisibility(View.VISIBLE);
            holder.userMessage.setText(chatMessage.getMessage());
            holder.userMessage.setGravity(Gravity.END); // Align to the right for user

            // Hide system message
            holder.systemMessageCard.setVisibility(View.GONE);
        } else {
            // Show system message (aligned to the left)
            holder.systemMessageCard.setVisibility(View.VISIBLE);
            holder.systemMessage.setText(chatMessage.getMessage());
            holder.systemMessage.setGravity(Gravity.START); // Align to the left for system

            // Hide user message
            holder.userMessageCard.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        CardView userMessageCard, systemMessageCard;
        TextView userMessage, systemMessage;

        public ChatViewHolder(View itemView) {
            super(itemView);
            userMessageCard = itemView.findViewById(R.id.userMessageCard);
            systemMessageCard = itemView.findViewById(R.id.systemMessageCard);
            userMessage = itemView.findViewById(R.id.userMessage); // Referencing userMessage
            systemMessage = itemView.findViewById(R.id.systemMessage); // Referencing systemMessage
        }
    }
}
