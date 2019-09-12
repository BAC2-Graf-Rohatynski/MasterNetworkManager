package masternetworkmanager.slave.modifier.interfaces

interface IUpdateSsid {
    fun change(ipAddress: String, ssid: Int)
}