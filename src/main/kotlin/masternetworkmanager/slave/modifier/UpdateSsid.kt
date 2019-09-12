package masternetworkmanager.slave.modifier

import apibuilder.slave.request.UpdateSsidSlaveItem
import masternetworkmanager.slave.modifier.interfaces.IUpdateSsid
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UpdateSsid: IUpdateSsid {
    private val logger: Logger = LoggerFactory.getLogger(UpdateSsid::class.java)

    @Synchronized
    override fun change(ipAddress: String, ssid: Int) {
        logger.info("Change SSID on slave '$ipAddress' to '$ssid' ...")
        val item = UpdateSsidSlaveItem().create(ssid = ssid, ipAddress = ipAddress)

        UdpHandler.sendSingleUdpMessage(
                buffer = item.toJson().toByteArray(),
                ipAddress = ipAddress)
    }
}