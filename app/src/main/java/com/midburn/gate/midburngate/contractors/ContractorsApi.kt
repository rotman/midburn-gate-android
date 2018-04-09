package com.midburn.gate.midburngate.contractors

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

interface ContractorsApi {

    @POST("get-sapak-ticket")
    fun getContractorDetails(@Query("barcode") barcode: String): retrofit2.Call<Contractor>

    @POST("enter-sapak-ticket")
    fun admitContractor(@Query("barcode") barcode: String, @Query("carPlate") carPlate: String): Call<Contractor>

    @POST("exit-sapak-ticket")
    fun departContractor(@Query("barcode") barcode: String, @Query("carPlate") carPlate: String): Call<Contractor>

    companion object {
        private val contractorsApi by lazy {
            Retrofit.Builder()
                    .baseUrl("https://spark.midburn.org/api/gate/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ContractorsApi::class.java)
        }

        fun get(): ContractorsApi = contractorsApi
    }
}
