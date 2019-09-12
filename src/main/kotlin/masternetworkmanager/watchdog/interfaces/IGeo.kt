package masternetworkmanager.watchdog.interfaces

interface IGeo {
    fun addSlave(macAddress: String)
    fun removeSlave(macAddress: String)
}