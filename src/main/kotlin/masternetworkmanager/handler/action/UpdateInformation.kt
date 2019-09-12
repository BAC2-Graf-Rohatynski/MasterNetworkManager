package masternetworkmanager.handler.action

import apibuilder.network.UpdateInformationItem
import databaseclient.action.SlaveAction
import enumstorage.network.NetworkCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import masternetworkmanager.slave.modifier.UpdateConfiguration
import masternetworkmanager.slave.modifier.UpdateInformation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UpdateInformation: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(UpdateConfiguration::class.java)
    private lateinit var item: UpdateInformationItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${NetworkCommand.UpdateInformation.name}' will be executed ...")
        UpdateInformation.update(slave = item.slave, ipAddress = item.slave.ipAddress)
        return SlaveAction.updateInformationBySsid(slave = item.slave)
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = UpdateInformationItem().toObject(message = message)
        return this
    }
}