package com.midburn.gate.midburngate.network

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkCalls {

    data class BarcodeTicketBody(val event_id: String, val barcode: String)
    data class ManualTicketBody(val event_id: String, val ticket: String, val order: String)

    @POST("/api/gate/get-ticket")
    fun getTicket(@Body body: BarcodeTicketBody): Call<TicketNew>

    @POST("/api/gate/gate-enter")
    fun gateEnter(@Body body: BarcodeTicketBody): Call<Unit>

    @POST("/api/gate/gate-exit")
    fun gateExit(@Body body: BarcodeTicketBody): Call<Unit>

    @POST("/api/gate/get-ticket")
    fun getTicketManually(@Body body: ManualTicketBody): Call<TicketNew>

    @POST("/api/gate/gate-enter")
    fun gateEnterManually(@Body body: ManualTicketBody): Call<Unit>

    @POST("/api/gate/gate-exit")
    fun gateExitManually(@Body body: ManualTicketBody): Call<Unit>

    @GET("/api/gate/suppliers/{contractorId}")
    fun getContractorDetails(@Path("contractorId") contractorId: String): Call<Contractor>

    @POST("/api/gate/suppliers/{contractorId}/add_gate_record_info/Inside")
    fun admitContractorInternal(@Path("contractorId") contractorId: String, @Body body: AdmittanceInfo): Call<Void>

    @POST("/api/gate/suppliers/{contractorId}/add_gate_record_info/Outside")
    fun departContractorInternal(@Path("contractorId") contractorId: String, @Body body: DepartureInfo): Call<Contractor>

    @POST("/api/gate/vehicle-action/{eventId}/arrival")
    fun carsIncrement(@Path("eventId") eventId: String): Call<Unit>

    @POST("/api/gate/vehicle-action/{eventId}/departure")
    fun carsDecrement(@Path("eventId") eventId: String): Call<Unit>

    @GET("events")
    fun getEventIds(): Call<Map<String, List<Event>>>

    data class Event(val event_id: String)

    data class AdmittanceInfo(@SerializedName("vehicle_plate_number") val vehiclePlateNumber: String,
                              @SerializedName("allowed_visa_hours") val allowedVisaHours: String = "4")

    data class DepartureInfo(@SerializedName("record_id") val recordId: String)


}

