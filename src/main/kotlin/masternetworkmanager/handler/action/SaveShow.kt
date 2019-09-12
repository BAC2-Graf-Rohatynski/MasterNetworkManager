package masternetworkmanager.handler.action

import apibuilder.slave.request.SaveShowItem
import databaseclient.action.SlaveAction
import enumstorage.network.NetworkCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SaveShow: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(SaveShow::class.java)
    private lateinit var item: SaveShowItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${NetworkCommand.SaveShow.name}' will be executed ...")
        SlaveAction.saveSlaveInformationBackup()
        return UdpHandler.sendBroadcastMessageOverUdp(buffer = item.toJson().toByteArray())
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = SaveShowItem().toObject(message = message)
        return this
    }
}