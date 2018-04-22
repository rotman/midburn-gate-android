package com.midburn.gate.midburngate.network

import retrofit2.Call
import retrofit2.mock.BehaviorDelegate

class NetworkCallsMock(private val delegate: BehaviorDelegate<NetworkCalls>) : NetworkCalls {
    override fun gateEnter(body: NetworkCalls.BarcodeTicketBody): Call<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun gateExit(body: NetworkCalls.BarcodeTicketBody): Call<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun gateEnterManually(body: NetworkCalls.ManualTicketBody): Call<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun gateExitManually(body: NetworkCalls.ManualTicketBody): Call<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTicket(body: NetworkCalls.BarcodeTicketBody): Call<TicketNew> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTicketManually(body: NetworkCalls.ManualTicketBody): Call<TicketNew> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun carsIncrement(eventId: String): Call<Unit> {
        return delegate.returningResponse(null).carsIncrement(eventId)
    }

    override fun carsDecrement(eventId: String): Call<Unit> {
        return delegate.returningResponse(null).carsDecrement(eventId)
    }

    override fun getEventIds(): Call<Map<String, List<NetworkCalls.Event>>> {
        return delegate.returningResponse(mapOf("events" to listOf(NetworkCalls.Event("MIDBURN2017"), NetworkCalls.Event("MIDBURN2018")))).getEventIds()
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