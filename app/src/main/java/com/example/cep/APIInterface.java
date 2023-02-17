package com.example.cep;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIInterface {

    @GET("{cep}/json")
    Call<Address> getAddress(@Path("cep") String cep);

    @GET("{state}/{city}/{street}/json")
    Call<List<Address>> getAddresses(@Path("state") String state, @Path("city") String city, @Path("street") String street);

}