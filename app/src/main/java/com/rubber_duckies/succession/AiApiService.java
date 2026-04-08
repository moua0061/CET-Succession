package com.rubber_duckies.succession;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AiApiService {
    @POST("generateScenario")
    Call<AiScenarioResponse> generateScenario(@Body AiScenarioRequest request);
}