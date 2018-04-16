package com.midburn.gate.midburngate.contractors

import android.content.Context

import com.midburn.gate.midburngate.BuildConfig
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
        performCall(context, callback, networkCalls.getEventIds())
    }

    fun getSapak(context: Context, contractorId: String, callback: Callback<Contractor>) {
        performCall(context, callback, networkCalls.getContractorDetails(contractorId))
    }

    private fun <T> performCall(context: Context, callback: Callback<T>, call: Call<T>) {
        if (!isConnected(context)) {
            callback.onFailure(Throwable("Not connected to a network"))

        } else call.enqueue(object : retrofit2.Callback<T> {
            override fun onFailure(call: Call<T>?, t: Throwable?) {
                callback.onFailure(t ?: Throwable("Network call failed"))
            }

            override fun onResponse(call: Call<T>?, response: Response<T>?) {
                if (response != null && !response.isSuccessful) callback.onFailure(Throwable("response is not successful"))
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
            .baseUrl("https://spark.midburn.org/api/gate/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}
