package com.midburn.gate.midburngate.contractors

import retrofit2.Call
import retrofit2.mock.BehaviorDelegate

class NetworkCallsMock(private val delegate: BehaviorDelegate<NetworkCalls>) : NetworkCalls {
    override fun carsIncrement(eventId: String): Call<Unit> {
        return delegate.returningResponse(null).carsIncrement(eventId)
    }

    override fun carsDecrement(eventId: String): Call<Unit> {
        return delegate.returningResponse(null).carsDecrement(eventId)
    }

    override fun getEventIds(): Call<List<String>> {
        return delegate.returningResponse(listOf("1231", "33333", "111111", "444444")).getEventIds()
    }

    override fun departContractorInternal(contractorId: String, body: NetworkCalls.DepartureInfo): Call<Contractor> {
        return delegate.returningResponse(null).departContractorInternal(contractorId, body)
    }

    override fun getContractorDetails(contractorId: String): Call<Contractor> {
        return delegate.returningResponse(Contractor(contractorId, "Israel Israeli")).getContractorDetails(contractorId)
    }

    override fun admitContractorInternal(contractorId: String, body: NetworkCalls.AdmittanceInfo): Call<Void> {
        return delegate.returningResponse(null).admitContractorInternal(contractorId, body)
    }
}