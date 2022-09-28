package com.example.cep;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIInterface {

    @GET("{cep}/json")
    Call<Address> getAddress(@Path("cep") String cep);

//    @GET("/api/users?")
//    Call<UserList> doGetUserList(@Query("page") String page);

}