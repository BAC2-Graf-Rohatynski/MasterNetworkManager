package masternetworkmanager.slave.port.interfaces

import apibuilder.slave.Slave

interface ISlavePorts {
    fun addPort(slave: Slave): Boolean
    fun removePortByMacAddress(macAddress: String): Boolean
    fun removePortBySsid(ssid: Int): Boolean
    fun getPortByIpAddress(ipAddress: String): Int
    fun getPortByMacAddress(macAddress: String): Int
    fun getIpAddress(macAddress: String): String
}