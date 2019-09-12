package masternetworkmanager.udp

import interfacehelper.MyIpAddress
import masternetworkmanager.MasterNetworkManagerRunner
import apibuilder.json.Json
import interfacehelper.GetAllIpAddresses
import masternetworkmanager.slave.port.SlavePorts
import masternetworkmanager.slave.queue.SlaveQueue
import masternetworkmanager.udp.interfaces.IUdpHandler
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.PortProperties
import java.lang.Exception
import java.net.*
import kotlin.concurrent.thread

object UdpHandler: IUdpHandler {
    private val sendingSocket = DatagramSocket()
    private lateinit var receivingSocket: DatagramSocket
    private var outgoingPort = PortProperties.getUdpMasterPort()
    private val outgoingInterface = MyIpAddress.getMyInterface()
    private val logger: Logger = LoggerFactory.getLogger(UdpHandler::class.java)

    init {
        try {
            logger.info("Starting UDP handler ...")
            receive()
        } catch (ex: Exception) {
            logger.error("Error while running UDP handler!\n${ex.message}")
            closeSockets()
        }
    }

    @Synchronized
    override fun sendSingleUdpMessage(buffer: ByteArray, ipAddress: String) {
        GetAllIpAddresses.get().forEach { address ->
            if (ipAddress == address.hostAddress) {
                val port = SlavePorts.getPortByIpAddress(ipAddress = ipAddress)

                if (port > 0) {
                    sendUdpPacket(buffer = buffer, it = address, port = port)
                }
            }
        }
    }

    @Synchronized
    override fun sendBroadcastMessageOverUdp(buffer: ByteArray) {
        GetAllIpAddresses.get().forEach {address ->
            val port = SlavePorts.getPortByIpAddress(ipAddress = address.hostAddress)

            if (port > 0) {
                sendUdpPacket(buffer = buffer, it = address, port = port)
            }
        }
    }

    @Synchronized
    private fun sendUdpPacket(buffer: ByteArray, it: InetAddress, port: Int) {
        logger.info("Sending message to ${it.address}")
        val packet = DatagramPacket(buffer, buffer.size, it, port)
        sendingSocket.send(packet)
    }

    private fun receive() {
        val myIpAddress = MyIpAddress.getAsInetAddress() ?: throw Exception("Interface for network $outgoingInterface not found!")

        thread {
            val buffer = ByteArray(size = 1024)
            receivingSocket = DatagramSocket(outgoingPort, myIpAddress)
            val packet = DatagramPacket(buffer, buffer.size)
            logger.info("Hearing for slaves and other masters ...")

            while (MasterNetworkManagerRunner.isRunnable()) {
                try {
                    receivingSocket.receive(packet)
                    val message = String(bytes = packet.data, offset = 0, length = packet.length)
                    logger.info("Message $message received")
                    SlaveQueue.putIntoQueue(message = message)
                } catch (ex: Exception) {
                    logger.error("Error occurred while receiving UDP packets!\n${ex.message}")
                }
            }
        }
    }

    private fun closeSockets() {
        try {
            logger.info("Closing sockets ...")

            if (!sendingSocket.isClosed) {
                sendingSocket.close()
            }

            if (::receivingSocket.isInitialized) {
                receivingSocket.close()
            }
            logger.info("Socket closed")
        } catch (ex: Exception) {
            logger.error("Error occurred while closing sockets!\n${ex.message}")
        }
    }
}