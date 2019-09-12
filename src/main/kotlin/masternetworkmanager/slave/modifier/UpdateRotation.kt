package masternetworkmanager.slave.modifier

import apibuilder.slave.request.UpdateRotationSlaveItem
import masternetworkmanager.slave.modifier.interfaces.IUpdateRotation
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UpdateRotation: IUpdateRotation {
    private val logger: Logger = LoggerFactory.getLogger(UpdateRotation::class.java)

    @Synchronized
    override fun change(ipAddress: String, isRotating: Boolean) {
        logger.info("Update rotation on slave '$ipAddress' to '$isRotating' ...")

        val item = UpdateRotationSlaveItem().create(isRotating = isRotating, ipAddress = ipAddress)

        UdpHandler.sendSingleUdpMessage(
                buffer = item.toJson().toByteArray(),
                ipAddress = ipAddress)
    }
}