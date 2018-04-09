package com.midburn.gate.midburngate.contractors

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

interface ContractorsApi {

    @POST("get-sapak-ticket")
    fun getContractorDetails(@Query("barcode") barcode: String): retrofit2.Call<Contractor>

    @POST("enter-sapak-ticket")
    fun admitContractor(@Query("barcode") barcode: String, @Query("carPlate") carPlate: String): Call<Contractor>

    @POST("exit-sapak-ticket")
    fun departContractor(@Query("barcode") barcode: String, @Query("carPlate") carPlate: String): Call<Contractor>

    companion object {
        private val contractorsApi by lazy {
            getRetrofit().create(ContractorsApi::class.java)
        }

        fun get(): ContractorsApi = contractorsApi

        fun getMock(): ContractorsApi {
            val retrofit = getRetrofit()
            // Create a MockRetrofit object with a NetworkBehavior which manages the fake behavior of calls.
            val behavior = NetworkBehavior.create()
            val mockRetrofit = MockRetrofit.Builder(retrofit)
                    .networkBehavior(behavior)
                    .build()
            val delegate = mockRetrofit.create(ContractorsApi::class.java)
            return ContractorsApiMock(delegate)
        }

        private fun getRetrofit(): Retrofit = Retrofit.Builder()
                .baseUrl("https://spark.midburn.org/api/gate/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private class ContractorsApiMock(private val delegate: BehaviorDelegate<ContractorsApi>) : ContractorsApi {
        override fun getContractorDetails(barcode: String): Call<Contractor> {
            return delegate.returningResponse(Contractor("123123", "Israel Israeli")).getContractorDetails(barcode)
        }

        override fun admitContractor(barcode: String, carPlate: String): Call<Contractor> {
            return delegate.returningResponse(Contractor("123123", "Israel Israeli")).admitContractor(barcode, carPlate)
        }

        override fun departContractor(barcode: String, carPlate: String): Call<Contractor> {
            return delegate.returningResponse(Contractor("123123", "Israel Israeli")).departContractor(barcode, carPlate)
        }
    }
}

