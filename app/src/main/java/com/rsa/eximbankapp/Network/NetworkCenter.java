package com.rsa.eximbankapp.Network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rsa.eximbankapp.Utils.GsonUTCDateAdapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetworkCenter {
    private static final String TAG = "NetworkCenter";

    Context context;
    Gson gson;
    SimpleDateFormat sdf;
    Retrofit retrofit;
    OkHttpClient okHttpClient;

    public NetworkCenter(Context context) {
        this.context = context;

        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));

        gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Date.class, new GsonUTCDateAdapter())
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(300, TimeUnit.SECONDS)
                .connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }

    private void InitRetrofit() {
        String url = ApiEndpoint.ROOT_URL;
        Log.d("Network Center", url);

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public boolean AuthTransactionI(String user, String code) {
        InitRetrofit();

        try {
            Call<String> result = retrofit.create(NetworkService.class).authTransactionI(user, code);
            int statusCode = result.execute().code();
            if (statusCode == 200) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean AuthTransactionII(String user, String txSigned, String txId) {
        InitRetrofit();

        try {
            Call<String> result = retrofit.create(NetworkService.class).authTransactionII(user, txSigned, txId);
            int statusCode = result.execute().code();
            if (statusCode == 200) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
