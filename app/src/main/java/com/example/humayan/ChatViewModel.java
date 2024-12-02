package com.example.humayan;

// ChatViewModel.java
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void addMessage(ChatMessage message) {
        chatMessages.add(message);
    }
    public void clearMessages() {
        chatMessages.clear();
    }
}


