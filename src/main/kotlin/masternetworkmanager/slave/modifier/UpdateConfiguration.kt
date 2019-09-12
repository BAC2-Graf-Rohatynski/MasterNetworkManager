package masternetworkmanager.slave.modifier

import apibuilder.slave.request.UpdateConfigurationSlaveItem
import masternetworkmanager.slave.modifier.interfaces.IUpdateConfiguration
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UpdateConfiguration: IUpdateConfiguration {
    private val logger: Logger = LoggerFactory.getLogger(UpdateConfiguration::class.java)

    @Synchronized
    override fun update(ddfHash: String, ddfFile: String, ipAddress: String) {
        logger.info("Change configuration on slave '$ipAddress' ...")
        val item = UpdateConfigurationSlaveItem().create(ddfFile = ddfFile, ddfHash = ddfHash, ipAddress = ipAddress)

        UdpHandler.sendSingleUdpMessage(
                buffer = item.toJson().toByteArray(),
                ipAddress = ipAddress)

        logger.info("Updating configuration of slave '$ddfHash' updated")
    }
}