package com.example.humayan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;

public class FragmentChat extends Fragment {
    private EditText editTextMessage;
    private Button buttonSend;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ChatViewModel chatViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.activity_fragment_chat, container, false);
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Gemini");
        }
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);
        recyclerView = view.findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the ViewModel instance
        chatViewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);

        chatAdapter = new ChatAdapter(chatViewModel.getChatMessages());
        recyclerView.setAdapter(chatAdapter);

        buttonSend.setOnClickListener(v -> {
            String userMessage = editTextMessage.getText().toString();
            if (!userMessage.isEmpty()) {
                chatViewModel.addMessage(new ChatMessage(userMessage, true)); // Add user message
                chatAdapter.notifyItemInserted(chatViewModel.getChatMessages().size() - 1);
                recyclerView.scrollToPosition(chatViewModel.getChatMessages().size() - 1);

                // Send message to Gemini API
                sendMessageToGemini(userMessage);
            }
        });

        return view;
    }

    private void sendMessageToGemini(String prompt) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    return GeminiAPIHelper.getGeminiResponse(params[0]);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return "Error getting response.";
                }
            }

            @Override
            protected void onPostExecute(String response) {
                // Add model's response to the chat using ViewModel
                chatViewModel.addMessage(new ChatMessage(response, false)); // Add model's response
                chatAdapter.notifyItemInserted(chatViewModel.getChatMessages().size() - 1);
                recyclerView.scrollToPosition(chatViewModel.getChatMessages().size() - 1);
            }
        }.execute(prompt);
    }
}
