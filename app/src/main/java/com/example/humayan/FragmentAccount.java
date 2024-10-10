package com.example.humayan;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class FragmentAccount extends Fragment {

    private String userName;
    private String userEmail;
    private String userBirthdate;
    private String userAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Set title for the toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Account");

        // Inflate the layout
        View view = inflater.inflate(R.layout.activity_fragment_account, container, false);

        // Example: Retrieve user data (this could come from arguments, SharedPreferences, or a database)
        userName = "John Doe"; // Replace with actual data
        userEmail = "john.doe@example.com"; // Replace with actual data
        userBirthdate = "January 1, 1990"; // Replace with actual data
        userAddress = "123 Main St, City, State"; // Replace with actual data

        // Set user data in text fields
        ((TextView) view.findViewById(R.id.profile_name)).setText(userName);
        ((TextView) view.findViewById(R.id.profile_birthdate)).setText("Birthdate: " + userBirthdate);
        ((TextView) view.findViewById(R.id.profile_address)).setText("Address: " + userAddress);

        // QR code generation
        ImageView qrCodeImageView = view.findViewById(R.id.qr_code_image);

        // Combine user data into a single string for the QR code
        String userInfo = "Name: " + userName + "\nEmail: " + userEmail + "\nBirthdate: " + userBirthdate + "\nAddress: " + userAddress;

        // Generate the QR code with user info
        generateQRCode(qrCodeImageView, userInfo);

        return view;
    }

    private void generateQRCode(ImageView qrCodeImageView, String qrContent) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 200, 200);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
