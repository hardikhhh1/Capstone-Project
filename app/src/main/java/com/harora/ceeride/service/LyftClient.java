package com.harora.ceeride.service;

import android.util.Base64;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by harora on 4/6/16.
 */
public final class LyftClient {
    public static final String BASE_URL = "https://api.lyft.com/";

    private String clientId;
    private String clientSecret;
    private String authToken;
    private static LyftClient lyftClient;
    private Boolean isAuthenticated;

    Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    OkHttpClient.Builder okHttpClientBuilder;
    Retrofit retrofit;

    public LyftClient(final String clientId, final String clientSecret){
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        okHttpClientBuilder = new OkHttpClient.Builder();

        okHttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .method(original.method(), original.body());
                    if(isAuthenticated != null && isAuthenticated && authToken != null){
                        requestBuilder.header("Authorization", "Bearer " +
                                authToken);
                    } else if(isAuthenticated != null && !isAuthenticated){

                        requestBuilder.header("Authorization",
                                "Basic " + net.iharder.Base64.encodeBytes((clientId + ":" +
                                        clientSecret).getBytes()));

                    } else if(authToken != null){
                        requestBuilder.header("Authorization", "Bearer " + authToken);
                    }

                return chain.proceed(requestBuilder.build());
            }
        });

        okHttpClientBuilder.authenticator(new Authenticator() {

                @Override
                public Request authenticate(Route route, Response response) throws IOException {
                    System.out.println("Authenticating for response: " + response);
                    System.out.println("Challenges: " + response.challenges());
                    isAuthenticated = false;

                    HashMap<String, Object> data = new HashMap<String, Object>();
                    data.put("grant_type","client_credentials");
                    data.put("scope","public");
                    String credential = Credentials.basic(clientId, clientSecret);

                    if (credential.equals(response.request().header("Authorization"))) {
                        return null; // If we already failed with these credentials, don't retry.
                    }

                    LyftService lyft = lyftClient.retrofit.create(LyftService.class);
                    retrofit2.Response<TokenInformation> call = lyft.authenticate(data).execute();
                    TokenInformation info = call.body();
                    isAuthenticated = true;
                    authToken = info.accessToken;
                    Request request= response.request().newBuilder()
                            .header("Authorization", "Bearer " + authToken)
                            .build();
                    return request;
                }


        });


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        retrofit.create(LyftService.class);

    }


    public static LyftService getLyftService(final String clientId, final String clientSecret){
        if(lyftClient != null){
            return lyftClient.retrofit.create(LyftService.class);
        }
        lyftClient = new LyftClient(clientId, clientSecret);
        return lyftClient.retrofit.create(LyftService.class);
    }

    public interface LyftService {

        @POST("oauth/token")
        Call<TokenInformation> authenticate(@Body HashMap<String, Object> body);

        @GET("v1/cost")
        Call<CostEstimates> getCosts(@Query("start_lat") Double startLatitude,
                                     @Query("start_lng") Double startLongitude,
                                     @Query("end_lat") Double endLatitude,
                                     @Query("end_lng") Double endLongitude);

    }

}
