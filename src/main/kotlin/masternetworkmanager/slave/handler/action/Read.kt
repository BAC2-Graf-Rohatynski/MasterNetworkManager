package masternetworkmanager.slave.handler.action

import databaseclient.action.SlaveAction
import enumstorage.slave.SlaveInformation
import enumstorage.slave.SlaveResponse
import masternetworkmanager.slave.handler.interfaces.ISlaveAction
import apibuilder.slave.Slave
import masternetworkmanager.slave.modifier.UpdateConfiguration
import masternetworkmanager.slave.modifier.UpdateDeviceImage
import masternetworkmanager.slave.modifier.UpdateGoboImage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Read: ISlaveAction {
    private val logger: Logger = LoggerFactory.getLogger(Read::class.java)

    @Synchronized
    override fun parse(slave: Slave) {
        slave.apply {
            logger.info("Parsing message ${SlaveResponse.Read.name} of slave SSID '$ssid' ...")

            SlaveAction.getSlaveBySsid(ssid = ssid).forEach { slaveEntry ->
                if (slaveEntry.deviceImageHash != deviceImageHash) {
                    logger.warn("Field '${SlaveInformation.DeviceImageHash.name}' of slave SSID '$ssid' doesn't match with database! Updating slave")
                    UpdateDeviceImage.update(fileName = slaveEntry.deviceImageHash, ipAddress = ipAddress)
                }

                if (slaveEntry.goboImageHash != goboImageHash) {
                    logger.warn("Field '${SlaveInformation.GoboImageHash.name}' of slave SSID '$ssid' doesn't match with database! Updating slave")
                    UpdateGoboImage.update(fileName = slaveEntry.goboImageHash, ipAddress = ipAddress)
                }

                if (slaveEntry.manufacturer != manufacturer ||
                        slaveEntry.type != type ||
                        slaveEntry.device != device ||
                        slaveEntry.isRotating != isRotating
                ) {
                    logger.warn("One or more information field of slave SSID '$ssid' doesn't match with database! Updating slave")
                    UpdateConfiguration.update(ddfHash = slave.ddfHash, ddfFile = slave.ddfFile, ipAddress = slave.ipAddress)
                }

                return logger.info("Slave '${slaveEntry.ssid}' updated")
            }
        }
    }
}