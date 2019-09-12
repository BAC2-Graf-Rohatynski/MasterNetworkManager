package masternetworkmanager.slave.handler.action

import apibuilder.slave.Slave
import databaseclient.action.SlaveAction
import enumstorage.slave.SlaveResponse
import masternetworkmanager.slave.handler.interfaces.ISlaveAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Geo: ISlaveAction {
    private val logger: Logger = LoggerFactory.getLogger(Geo::class.java)

    @Synchronized
    override fun parse(slave: Slave) {
        slave.apply {
            logger.info("Parsing message ${SlaveResponse.Geo.name} of slave SSID '$macAddress ...")
            SlaveAction.updateGeo(slave = this)
        }
    }
}