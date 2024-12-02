package com.example.humayan;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentChat extends Fragment {
    private EditText editTextMessage;
    private Button buttonSend;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ChatViewModel chatViewModel;
    private RequestQueue requestQueue;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.activity_fragment_chat, container, false);
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Gemini");
        }

        // Initialize views
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);
        recyclerView = view.findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize ViewModel
        chatViewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);

        // Initialize RecyclerView Adapter
        chatAdapter = new ChatAdapter(chatViewModel.getChatMessages());
        recyclerView.setAdapter(chatAdapter);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(requireContext());

        // Fetch messages from database
        fetchMessagesFromDatabase();

        // Send button click listener
        buttonSend.setOnClickListener(v -> {
            String userMessage = editTextMessage.getText().toString();
            if (!userMessage.isEmpty()) {
                // Add user message to ViewModel
                chatViewModel.addMessage(new ChatMessage(userMessage, true));
                chatAdapter.notifyItemInserted(chatViewModel.getChatMessages().size() - 1);
                recyclerView.scrollToPosition(chatViewModel.getChatMessages().size() - 1);

                // Save message to database
                saveMessageToDatabase(userMessage, true);

                // Send message to Gemini API
                sendMessageToGemini(userMessage);
            }
        });

        return view;
    }

    private void saveMessageToDatabase(String message, boolean isUserMessage) {
        String url = "https://zel.helioho.st/save_message.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle successful response
                },
                error -> {
                    // Handle error response
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("message", message);
                params.put("is_user_message", String.valueOf(isUserMessage ? 1 : 0));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void fetchMessagesFromDatabase() {
        String url = "https://zel.helioho.st/fetch_messages.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        chatViewModel.clearMessages(); // Clear existing messages
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject messageObj = response.getJSONObject(i);
                            String message = messageObj.getString("message");
                            boolean isUserMessage = messageObj.getInt("is_user_message") == 1;
                            chatViewModel.addMessage(new ChatMessage(message, isUserMessage));
                        }
                        chatAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle error response
                    error.printStackTrace();
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void sendMessageToGemini(String prompt) {
        new Thread(() -> {
            try {
                String response = GeminiAPIHelper.getGeminiResponse(prompt);
                requireActivity().runOnUiThread(() -> {
                    // Add model's response to ViewModel
                    chatViewModel.addMessage(new ChatMessage(response, false));
                    chatAdapter.notifyItemInserted(chatViewModel.getChatMessages().size() - 1);
                    recyclerView.scrollToPosition(chatViewModel.getChatMessages().size() - 1);

                    // Save model response to database
                    saveMessageToDatabase(response, false);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
