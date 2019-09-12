package masternetworkmanager.handler.action

import apibuilder.network.UpdateSsidItem
import databaseclient.action.SlaveAction
import enumstorage.network.NetworkCommand
import enumstorage.slave.SlaveInformation
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import masternetworkmanager.slave.modifier.UpdateSsid
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UpdateSsid: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(UpdateSsid::class.java)
    private lateinit var item: UpdateSsidItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${NetworkCommand.UpdateSsid.name}' will be executed ...")
        UpdateSsid.change(ssid = item.newSsid, ipAddress = item.ipAddress)
        return SlaveAction.updateBySsid(ssid = item.ssid, field = SlaveInformation.Ssid.name, value = item.newSsid.toString())
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = UpdateSsidItem().toObject(message = message)
        return this
    }
}