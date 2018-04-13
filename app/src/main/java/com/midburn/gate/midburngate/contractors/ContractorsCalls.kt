package com.midburn.gate.midburngate.contractors

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

interface ContractorsCalls {

    @GET("suppliers/{contractorId}")
    fun getContractorDetails(@Path("contractorId") contractorId: String): Call<Contractor>

    @POST("suppliers/{contractorId}/add_gate_record_info/Inside")
    fun admitContractorInternal(@Path("contractorId") contractorId: String, @Body body: AdmittanceInfo): Call<Void>

    @POST("suppliers/{contractorId}/add_gate_record_info/Outside")
    fun departContractorInternal(@Path("contractorId") contractorId: String, @Body body: DepartureInfo): Call<Contractor>

    @GET("cars/increment")
    fun carsIncrement(): Call<Void>

    @GET("cars/decrement")
    fun carsDecrement(): Call<Void>

    @GET("eventIds")
    fun getEventIds(): Call<List<String>>

    data class AdmittanceInfo(@SerializedName("vehicle_plate_number") val vehiclePlateNumber: String,
                              @SerializedName("allowed_visa_hours") val allowedVisaHours: String = "4")

    data class DepartureInfo(@SerializedName("record_id") val recordId: String)

    companion object {
        private val contractorsCalls by lazy {
            getRetrofit().create(ContractorsCalls::class.java)
        }

        fun get(): ContractorsCalls = contractorsCalls

        fun getMock(): ContractorsCalls {
            val retrofit = getRetrofit()
            // Create a MockRetrofit object with a NetworkBehavior which manages the fake behavior of calls.
            val behavior = NetworkBehavior.create()
            val mockRetrofit = MockRetrofit.Builder(retrofit)
                    .networkBehavior(behavior)
                    .build()
            val delegate = mockRetrofit.create(ContractorsCalls::class.java)
            return ContractorsCallsMock(delegate)
        }

        private fun getRetrofit(): Retrofit = Retrofit.Builder()
                .baseUrl("https://spark.midburn.org/api/gate/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        private class ContractorsCallsMock(private val delegate: BehaviorDelegate<ContractorsCalls>) : ContractorsCalls {
            override fun getEventIds(): Call<List<String>> {
                return delegate.returningResponse(listOf("1231", "33333", "111111", "444444")).getEventIds()
            }

            override fun carsIncrement(): Call<Void> {
                return delegate.returningResponse(null).carsIncrement()
            }

            override fun carsDecrement(): Call<Void> {
                return delegate.returningResponse(null).carsDecrement()
            }

            override fun departContractorInternal(contractorId: String, body: DepartureInfo): Call<Contractor> {
                return delegate.returningResponse(null).departContractorInternal(contractorId, body)
            }

            override fun getContractorDetails(contractorId: String): Call<Contractor> {
                return delegate.returningResponse(Contractor(contractorId, "Israel Israeli")).getContractorDetails(contractorId)
            }

            override fun admitContractorInternal(contractorId: String, body: AdmittanceInfo): Call<Void> {
                return delegate.returningResponse(null).admitContractorInternal(contractorId, body)
            }
        }
    }

}

