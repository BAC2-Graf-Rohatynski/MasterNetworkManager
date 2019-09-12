package masternetworkmanager.slave.modifier

import apibuilder.slave.request.EnableShowItem
import apibuilder.slave.Slave
import masternetworkmanager.slave.modifier.interfaces.IEnableShow
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object EnableShow: IEnableShow {
    private val logger: Logger = LoggerFactory.getLogger(EnableShow::class.java)

    @Synchronized
    override fun changeAll(show: String) {
        logger.info("Change show '$show' on all slaves ...")
        UdpHandler.sendBroadcastMessageOverUdp(buffer = EnableShowItem().create(show = show).toJson().toByteArray())
    }

    @Synchronized
    override fun changeSingle(show: String, slave: Slave) {
        logger.info("Change show '$show' on slave ...")
        UdpHandler.sendSingleUdpMessage(
                buffer = EnableShowItem().create(show = show).toJson().toByteArray(),
                ipAddress = slave.ipAddress)
    }
}