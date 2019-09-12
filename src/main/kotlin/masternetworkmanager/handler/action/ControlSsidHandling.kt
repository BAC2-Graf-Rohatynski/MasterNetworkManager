package masternetworkmanager.handler.action

import apibuilder.network.ControlSsidHandlingItem
import enumstorage.network.NetworkCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import masternetworkmanager.slave.ssid.SsidActivationHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ControlSsidHandling: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(ControlSsidHandling::class.java)
    private lateinit var item: ControlSsidHandlingItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${NetworkCommand.ControlSsidHandling.name}' will be executed ...")
        return SsidActivationHandler.control(isEnabled = item.isEnabled)
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = ControlSsidHandlingItem().toObject(message = message)
        return this
    }
}