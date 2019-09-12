package masternetworkmanager.handler.action

import apibuilder.network.UpdateConfigurationItem
import databaseclient.action.ConfigAction
import enumstorage.network.NetworkCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import masternetworkmanager.slave.modifier.UpdateConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UpdateConfiguration: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(UpdateConfiguration::class.java)
    private lateinit var item: UpdateConfigurationItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${NetworkCommand.UpdateConfiguration.name}' will be executed ...")
        UpdateConfiguration.update(ddfHash = item.ddfHash, ddfFile = item.ddfFile, ipAddress = item.ipAddress)
        return ConfigAction.updateBySsid(ssid = item.ssid, ddfHash = item.ddfHash, ddfFile = item.ddfFile)
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = UpdateConfigurationItem().toObject(message = message)
        return this
    }
}