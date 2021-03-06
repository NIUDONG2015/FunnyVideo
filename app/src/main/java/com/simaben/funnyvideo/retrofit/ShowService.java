package com.simaben.funnyvideo.retrofit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

/**
 * Created by simaben on 24/3/16.
 */
public class ShowService {
    private ShowService() {
    }

    public static ShowApi createService() {
        Retrofit.Builder builder = new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://route.showapi.com/");

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request newReq = request.newBuilder()
//                            .addHeader("Authorization", format("token %s", githubToken))
                        .build();
                return chain.proceed(newReq);
            }
        }).build();
        builder.client(client);
        return builder.build().create(ShowApi.class);
    }
}
