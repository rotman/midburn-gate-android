package com.midburn.gate.midburngate.network

import android.content.Context
import com.midburn.gate.midburngate.BuildConfig
import com.midburn.gate.midburngate.consts.AppConsts
import com.midburn.gate.midburngate.utils.AppUtils.isConnected
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

object NetworkApi {

    interface Callback<in T> {
        fun onSuccess(response: T)
        fun onFailure(throwable: Throwable)
    }

    fun enterCar(context: Context, eventId: String, callback: Callback<Unit>) {
        performCall(context, callback, networkCalls.carsIncrement(eventId))
    }

    fun exitCar(context: Context, eventId: String, callback: Callback<Unit>) {
        performCall(context, callback, networkCalls.carsDecrement(eventId))
    }

    fun getEvents(context: Context, callback: Callback<List<String>>) {
        if (!isConnected(context)) {
            callback.onFailure(Throwable("Not connected to a network"))

        } else {
            networkCalls.getEventIds().enqueue(object : retrofit2.Callback<Map<String, List<NetworkCalls.Event>>?> {
                override fun onFailure(call: Call<Map<String, List<NetworkCalls.Event>>?>?, t: Throwable?) {
                    callback.onFailure(t ?: Throwable("Network call failed"))
                }

                override fun onResponse(call: Call<Map<String, List<NetworkCalls.Event>>?>?, response: Response<Map<String, List<NetworkCalls.Event>>?>?) {
                    val events = response?.body()?.get("events")
                    if (events == null) {
                        callback.onFailure(Throwable("Response does not contain a list of events"))
                        return
                    }
                    callback.onSuccess(events.map { it.event_id }.toList())
                }
            })
        }

    }

    fun getSapak(context: Context, contractorId: String, callback: Callback<Contractor>) {
        performCall(context, callback, networkCalls.getContractorDetails(contractorId))
    }

    fun getTicketManually(context: Context, eventId: String, ticket: String, order: String, callback: Callback<TicketNew>) {
        val manualTicketBody = NetworkCalls.ManualTicketBody(eventId, ticket, order)
        performCall(
                context,
                callback,
                networkCalls.getTicketManually(manualTicketBody)
        )
    }

    fun getTicket(context: Context, eventId: String, barcode: String, callback: Callback<TicketNew>) {
        val barcodeTicketBody = NetworkCalls.BarcodeTicketBody(eventId, barcode)
        performCall(
                context,
                callback,
                networkCalls.getTicket(barcodeTicketBody)
        )
    }


    private fun <T> performCall(context: Context, callback: Callback<T>, call: Call<T>) {
        if (!isConnected(context)) {
            callback.onFailure(Throwable("Not connected to a network"))

        } else call.enqueue(object : retrofit2.Callback<T> {
            override fun onFailure(call: Call<T>?, t: Throwable?) {
                callback.onFailure(t ?: Throwable("Network call failed"))
            }

            override fun onResponse(call: Call<T>?, response: Response<T>?) {
                if (response != null && !response.isSuccessful) {
                    callback.onFailure(Throwable("response is not successful"))
                    return
                }
                val body = response?.body()
                if (body != null) callback.onSuccess(body)
                else callback.onFailure(Throwable("Response came back with empty body"))
            }
        })
    }

    private val networkCalls: NetworkCalls by lazy {
        if (BuildConfig.USE_MOCK) {
            val retrofit = getRetrofit()
            // Create a MockRetrofit object with a NetworkBehavior which manages the fake behavior of calls.
            val behavior = NetworkBehavior.create()
            behavior.setFailurePercent(50)
            val mockRetrofit = MockRetrofit.Builder(retrofit)
                    .networkBehavior(behavior)
                    .build()
            val delegate = mockRetrofit.create(NetworkCalls::class.java)
            NetworkCallsMock(delegate)
        } else getRetrofit().create(NetworkCalls::class.java)
    }

    private fun getRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl(AppConsts.SERVER_STAGING_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}
