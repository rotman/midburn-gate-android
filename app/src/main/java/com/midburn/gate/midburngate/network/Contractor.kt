package com.midburn.gate.midburngate.network

data class Contractor(val id: String, val name: String, val entrances: List<SapakEntrance> = mutableListOf()) {

    inner class SapakEntrance(var date: String?, var peopleCount: Int, var carPlate: String?, var isClosed: Boolean) : Comparable<SapakEntrance> {

        override fun compareTo(sapakEntrance: SapakEntrance): Int {
            return if (!sapakEntrance.isClosed) {
                1
            } else if (sapakEntrance.isClosed) {
                -1

            } else {
                0
            }
        }
    }
}

