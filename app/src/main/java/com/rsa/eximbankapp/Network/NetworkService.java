package com.rsa.eximbankapp.Network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface NetworkService {

    @Headers("Content-Type: application/json")
    @GET(ApiEndpoint.AUTHEN_TRANSACTION)
    Call<String> authTransactionI(@Query("user") String user, @Query("code") String code);

    @Headers("Content-Type: application/json")
    @GET(ApiEndpoint.AUTHEN_TRANSACTION)
    Call<String> authTransactionII(@Query("user") String user, @Query("code") String code, @Query("txid") String txid);
}
