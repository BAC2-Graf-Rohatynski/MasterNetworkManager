package masternetworkmanager.handler.action

import apibuilder.network.ClearShowItem
import databaseclient.action.ConfigAction
import databaseclient.action.ShowAction
import databaseclient.action.SlaveAction
import enumstorage.network.NetworkCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ClearShow: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(ClearShow::class.java)
    private lateinit var item: ClearShowItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${NetworkCommand.ClearShow.name}' will be executed ...")
        SlaveAction.deleteAllRecordsInDatabase()
        ConfigAction.deleteAllRecordsInDatabase(show = ShowAction.getEnabledShow())
        return UdpHandler.sendBroadcastMessageOverUdp(buffer = item.toJson().toByteArray())
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = ClearShowItem().toObject(message = message)
        return this
    }
}