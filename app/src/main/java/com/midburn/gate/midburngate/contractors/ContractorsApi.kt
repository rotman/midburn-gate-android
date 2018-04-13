package com.midburn.gate.midburngate.contractors

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

interface ContractorsApi {

    @GET("suppliers/{contractorId}")
    fun getContractorDetails(@Path("contractorId") contractorId: String): Call<Contractor>

    @POST("suppliers/{contractorId}/add_gate_record_info/Inside")
    fun admitContractor(@Path("contractorId") contractorId: String, @Body body: AdmittanceInfo): Call<Void>

    data class AdmittanceInfo(@SerializedName("vehicle_plate_number") val vehiclePlateNumber: String,
                              @SerializedName("allowed_visa_hours") val allowedVisaHours: String = "4")

    @POST("suppliers/{contractorId}/add_gate_record_info/Outside")
    fun departContractor(@Path("contractorId") contractorId: String, @Body body: DepartureInfo): Call<Contractor>

    data class DepartureInfo(@SerializedName("record_id") val recordId: String)

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
        override fun departContractor(contractorId: String, body: DepartureInfo): Call<Contractor> {
            return delegate.returningResponse(null).departContractor(contractorId, body)
        }

        override fun getContractorDetails(contractorId: String): Call<Contractor> {
            return delegate.returningResponse(Contractor(contractorId, "Israel Israeli")).getContractorDetails(contractorId)
        }

        override fun admitContractor(contractorId: String, body: AdmittanceInfo): Call<Void> {
            return delegate.returningResponse(null).admitContractor(contractorId, body)
        }
    }
}

