package masternetworkmanager.handler.action

import apibuilder.network.UpdateDeviceImageItem
import databaseclient.action.ImageAction
import enumstorage.database.DatabaseCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import masternetworkmanager.slave.modifier.UpdateDeviceImage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UpdateDeviceImage: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(masternetworkmanager.handler.action.UpdateDeviceImage::class.java)
    private lateinit var item: UpdateDeviceImageItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${DatabaseCommand.UpdateDeviceById.name}' will be executed ...")
        ImageAction.updateItemById(item = item.item)
        return UpdateDeviceImage.update(fileName = item.item.fileName, ipAddress = item.ipAddress)
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = UpdateDeviceImageItem().toObject(message = message)
        return this
    }
}