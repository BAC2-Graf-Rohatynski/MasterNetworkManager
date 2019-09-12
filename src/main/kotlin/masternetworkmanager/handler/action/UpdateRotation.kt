package masternetworkmanager.handler.action

import apibuilder.network.UpdateRotationItem
import databaseclient.action.SlaveAction
import enumstorage.network.NetworkCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import masternetworkmanager.watchdog.GeoWatchdog
import masternetworkmanager.slave.modifier.UpdateRotation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UpdateRotation: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(UpdateRotation::class.java)
    private lateinit var item: UpdateRotationItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${NetworkCommand.UpdateRotation.name}' will be executed ...")
        SlaveAction.updateRotating(isRotating = item.isRotating, ssid = item.ssid)
        UpdateRotation.change(ipAddress = item.ipAddress, isRotating = item.isRotating)

        return if (item.macAddress.isNotEmpty()) {
            GeoWatchdog.addSlave(macAddress = item.macAddress)
        } else {
            GeoWatchdog.removeSlave(macAddress = item.macAddress)
        }
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = UpdateRotationItem().toObject(message = message)
        return this
    }
}