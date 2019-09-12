package masternetworkmanager.udp.interfaces

interface IUdpHandler {
    fun sendSingleUdpMessage(buffer: ByteArray, ipAddress: String)
    fun sendBroadcastMessageOverUdp(buffer: ByteArray)
}