package com.example.humayan;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentInsect extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    private Retrofit retrofit;
    private InsectApiService apiService;

    private TextView commonNameTextView, scientificNameTextView, descriptionTextView;
    private ImageView insectImageView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_insect, container, false);

        commonNameTextView = view.findViewById(R.id.common_names);
        scientificNameTextView = view.findViewById(R.id.scientific_name);
        descriptionTextView = view.findViewById(R.id.insect_description);
        insectImageView = view.findViewById(R.id.insect_image);

        Button pickImageButton = view.findViewById(R.id.pick_image_button);
        Button identifyInsectButton = view.findViewById(R.id.identify_insect_button);
        identifyInsectButton.setEnabled(false); // Initially disable the button

        identifyInsectButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                identifyInsect(selectedImageUri);
            }
        });

        pickImageButton.setOnClickListener(v -> openImagePicker());

        setupRetrofit();

        return view;
    }

    private void setupRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://insect.kindwise.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(InsectApiService.class);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void identifyInsect(Uri imageUri) {
        File imageFile = new File(getRealPathFromURI(imageUri));

        if (!imageFile.exists()) {
            Log.e("FragmentInsect", "File not found: " + imageFile.getAbsolutePath());
            descriptionTextView.setText("File not found. Please select a valid image.");
            return;
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("image/*"), // Correct MIME type for the image
                imageFile
        );

        MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                "image", // Make sure this is the correct key expected by the server
                imageFile.getName(),
                requestBody
        );

        apiService.identifyInsect(
                "lE9FfW57Zrpxs63wz72uRinUDH1uRLSP9hoYybgvJNsAAy4GHj",
                imagePart
        ).enqueue(new Callback<InsectResponse>() {
            @Override
            public void onResponse(Call<InsectResponse> call, Response<InsectResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayInsectInfo(response.body());
                } else {
                    Log.e("FragmentInsect", "Response Code: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("FragmentInsect", "Error Body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e("FragmentInsect", "Error reading response body", e);
                    }
                    descriptionTextView.setText("Error identifying insect. Try again.");
                }
            }

            @Override
            public void onFailure(Call<InsectResponse> call, Throwable t) {
                Log.e("FragmentInsect", "Network Error", t);
                descriptionTextView.setText("Network error: " + t.getMessage());
            }
        });
    }

    private void displayInsectInfo(InsectResponse insect) {
        if (insect.getResult() != null &&
                insect.getResult().getClassification() != null &&
                insect.getResult().getClassification().getSuggestions() != null &&
                !insect.getResult().getClassification().getSuggestions().isEmpty()) {

            InsectResponse.Result.Classification.Suggestion suggestion =
                    insect.getResult().getClassification().getSuggestions().get(0);

            // Display common names
            if (suggestion.getDetails() != null && suggestion.getDetails().getCommonNames() != null) {
                commonNameTextView.setText("Common Names: " + String.join(", ", suggestion.getDetails().getCommonNames()));
            } else {
                commonNameTextView.setText("Common Names: Not available");
            }

            if (suggestion.getName() != null) {
                scientificNameTextView.setText("Scientific Name: " + suggestion.getName());
            } else {
                scientificNameTextView.setText("Scientific Name: Not available");
            }

            if (suggestion.getDetails() != null && suggestion.getDetails().getDescription() != null && suggestion.getDetails().getDescription().getValue() != null) {
                descriptionTextView.setText("Description: " + suggestion.getDetails().getDescription().getValue());
            } else {
                descriptionTextView.setText("Description: Not available");
            }




        } else {
            commonNameTextView.setText("No data found.");
            scientificNameTextView.setText("");
            descriptionTextView.setText("");
            insectImageView.setImageResource(R.drawable.ic_insect); // Fallback image
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Log the URI of the selected image
            Log.d("FragmentInsect", "Selected image URI: " + selectedImageUri.toString());

            // Load the selected image into the ImageView using Picasso
            Picasso.get()
                    .load(selectedImageUri) // Load the image from the URI
                    .into(insectImageView); // Set the image in the ImageView

            Button identifyInsectButton = getView().findViewById(R.id.identify_insect_button);
            identifyInsectButton.setEnabled(true); // Enable the button after image is selected
        } else {
            // Log if no image was selected
            Log.d("FragmentInsect", "No image selected or result canceled.");
        }
    }


    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }
}
