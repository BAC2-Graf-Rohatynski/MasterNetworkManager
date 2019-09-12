package masternetworkmanager.slave.handler.action

import enumstorage.slave.SlaveResponse
import interfacehelper.MyIpAddress
import masternetworkmanager.slave.handler.interfaces.ISlaveAction
import masternetworkmanager.slave.port.SlavePorts
import apibuilder.slave.Slave
import apibuilder.slave.request.ConnectedSlaveItem
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

object Greeting: ISlaveAction {
    private val logger: Logger = LoggerFactory.getLogger(Greeting::class.java)

    @Synchronized
    override fun parse(slave: Slave) {
        logger.info("Parsing message ${SlaveResponse.Greeting.name} of slave SSID '${slave.ssid}' ...")

        if (SlavePorts.addPort(slave = slave)) {
            logger.info("Greeting message received from slave '${slave.ssid}")
            val item = ConnectedSlaveItem().create(ipAddress = MyIpAddress.getAsString() ?: throw Exception("IP address cannot be received!"))

            UdpHandler.sendSingleUdpMessage(
                    buffer = item.toJson().toByteArray(),
                    ipAddress = slave.ipAddress)
        }
    }
}