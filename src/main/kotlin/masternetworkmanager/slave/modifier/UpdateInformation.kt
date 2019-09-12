package masternetworkmanager.slave.modifier

import apibuilder.slave.Slave
import apibuilder.slave.request.UpdateInformationSlaveItem
import masternetworkmanager.slave.modifier.interfaces.IUpdateInformation
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UpdateInformation: IUpdateInformation {
    private val logger: Logger = LoggerFactory.getLogger(UpdateInformation::class.java)

    @Synchronized
    override fun update(slave: Slave, ipAddress: String) {
        logger.info("Change information on slave '$ipAddress' ...")

        val item = UpdateInformationSlaveItem().create(slave = slave)

        UdpHandler.sendSingleUdpMessage(
                buffer = item.toJson().toByteArray(),
                ipAddress = ipAddress)

        logger.info("Updating configuration of slave '${slave.ssid}' updated")
    }
}