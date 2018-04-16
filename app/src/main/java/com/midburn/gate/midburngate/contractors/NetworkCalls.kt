package com.midburn.gate.midburngate.contractors

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkCalls {

    @GET("suppliers/{contractorId}")
    fun getContractorDetails(@Path("contractorId") contractorId: String): Call<Contractor>

    @POST("suppliers/{contractorId}/add_gate_record_info/Inside")
    fun admitContractorInternal(@Path("contractorId") contractorId: String, @Body body: AdmittanceInfo): Call<Void>

    @POST("suppliers/{contractorId}/add_gate_record_info/Outside")
    fun departContractorInternal(@Path("contractorId") contractorId: String, @Body body: DepartureInfo): Call<Contractor>

    @POST("vehicle-action/{eventId}/arrival")
    fun carsIncrement(@Path("eventId") eventId: String): Call<Unit>

    @POST("vehicle-action/{eventId}/departure")
    fun carsDecrement(@Path("eventId") eventId: String): Call<Unit>

    @GET("eventIds")
    fun getEventIds(): Call<List<String>>

    data class AdmittanceInfo(@SerializedName("vehicle_plate_number") val vehiclePlateNumber: String,
                              @SerializedName("allowed_visa_hours") val allowedVisaHours: String = "4")

    data class DepartureInfo(@SerializedName("record_id") val recordId: String)


}

