package masternetworkmanager.slave.handler.action

import apibuilder.slave.Slave
import enumstorage.slave.SlaveResponse
import masternetworkmanager.slave.handler.interfaces.ISlaveAction
import masternetworkmanager.slave.ssid.SsidActivationHandler
import masternetworkmanager.slave.ssid.SsidHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Connect: ISlaveAction {
    private val logger: Logger = LoggerFactory.getLogger(Connect::class.java)

    @Synchronized
    override fun parse(slave: Slave) {
        logger.info("Parsing message ${SlaveResponse.Connect.name} of slave SSID '${slave.ssid}' ...")

        if (SsidActivationHandler.isEnabled()) {
            SsidHandler().run(slave = slave)
        }
    }
}