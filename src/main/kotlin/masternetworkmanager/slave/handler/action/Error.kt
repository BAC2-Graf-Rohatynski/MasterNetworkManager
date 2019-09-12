package masternetworkmanager.slave.handler.action

import apibuilder.slave.Slave
import error.client.ErrorClient
import enumstorage.slave.SlaveResponse
import masternetworkmanager.slave.handler.interfaces.ISlaveAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Error: ISlaveAction {
    private val logger: Logger = LoggerFactory.getLogger(Error::class.java)

    @Synchronized
    override fun parse(slave: Slave) {
        slave.apply {
            logger.info("Parsing message ${SlaveResponse.Error.name} of slave SSID '$ssid' ...")

            errorCode.forEach { code ->
                ErrorClient.sendMessage(code = code, ssid = ssid, enabled = true)
            }
        }
    }
}