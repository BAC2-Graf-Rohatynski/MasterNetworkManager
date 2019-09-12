package masternetworkmanager.slave.modifier.interfaces

interface IUpdateDeviceImage {
    fun update(fileName: String, ipAddress: String)
}