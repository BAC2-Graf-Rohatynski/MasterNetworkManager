package masternetworkmanager.handler.action

import apibuilder.slave.request.ReadShowItem
import enumstorage.network.NetworkCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ReadShow: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(ReadShow::class.java)
    private lateinit var item: ReadShowItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${NetworkCommand.ReadShow.name}' will be executed ...")
        return UdpHandler.sendBroadcastMessageOverUdp(buffer = item.toJson().toByteArray())
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = ReadShowItem().toObject(message = message)
        return this
    }
}