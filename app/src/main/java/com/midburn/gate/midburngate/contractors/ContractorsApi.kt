package com.midburn.gate.midburngate.contractors

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Path

interface ContractorsApi {

    @POST("get-sapak-ticket/?barcode={barcode}")
    fun getContractorDetails(@Path("barcode") barcode: String): Call<Contractor>

    @POST("enter-sapak-ticket/?barcode={barcode}&carPlate={carPlate}")
    fun admitContractor(@Path("barcode") barcode: String, @Path("carPlate") carPlate: String): Call<Contractor>

    @POST("exit-sapak-ticket/?barcode={barcode}&carPlate={carPlate}")
    fun departContractor(@Path("barcode") barcode: String, @Path("carPlate") carPlate: String): Call<Contractor>

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
