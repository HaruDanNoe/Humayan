package com.example.humayan;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface InsectApiService {
    @Multipart
    @POST("api/v1/identification")
    Call<InsectResponse> identifyInsect(
            @Header("Api-key") String apiKey,
            @Part MultipartBody.Part image
    );
}


