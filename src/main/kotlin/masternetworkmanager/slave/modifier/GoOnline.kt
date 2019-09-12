package masternetworkmanager.slave.modifier

import apibuilder.slave.request.GoOnlineSlaveItem
import databaseclient.action.SlaveAction
import enumstorage.slave.SlaveInformation
import enumstorage.slave.SlaveStatus
import apibuilder.slave.Slave
import masternetworkmanager.slave.modifier.interfaces.IGoOnline
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GoOnline: IGoOnline {
    private val logger: Logger = LoggerFactory.getLogger(GoOnline::class.java)

    @Synchronized
    override fun set(slave: Slave) {
        logger.info("Setting slave '${slave.ssid}' online ...")

        SlaveAction.updateBySsid(
                ssid = slave.ssid,
                field = SlaveInformation.Status.name,
                value = SlaveStatus.Online.name
        )

        UdpHandler.sendSingleUdpMessage(
                buffer = GoOnlineSlaveItem().create().toJson().toByteArray(),
                ipAddress = slave.ipAddress)
    }
}