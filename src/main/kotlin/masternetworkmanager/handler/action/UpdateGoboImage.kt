package masternetworkmanager.handler.action

import masternetworkmanager.slave.modifier.UpdateGoboImage
import apibuilder.network.UpdateGoboImageItem
import databaseclient.action.GoboAction
import enumstorage.database.DatabaseCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UpdateGoboImage: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(UpdateGoboImage::class.java)
    private lateinit var goboItem: UpdateGoboImageItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${DatabaseCommand.UpdateDeviceById.name}' will be executed ...")
        GoboAction.updateItemById(item = goboItem.item)
        return UpdateGoboImage.update(fileName = goboItem.item.fileName, ipAddress = goboItem.ipAddress)
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        goboItem = UpdateGoboImageItem().toObject(message = message)
        return this
    }
}