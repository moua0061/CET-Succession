package com.rubber_duckies.succession;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AiClient {

    // Emulator must use 10.0.2.2 to reach your computer
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    public static AiApiService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AiApiService.class);
    }
}
