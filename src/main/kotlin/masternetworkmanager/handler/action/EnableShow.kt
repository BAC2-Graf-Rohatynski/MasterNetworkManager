package masternetworkmanager.handler.action

import apibuilder.slave.request.EnableShowItem
import databaseclient.action.ShowAction
import enumstorage.database.DatabaseCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import masternetworkmanager.slave.modifier.EnableShow
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object EnableShow: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(EnableShow::class.java)
    private lateinit var item: EnableShowItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${DatabaseCommand.EnableShow.name}' will be executed ...")
        ShowAction.enableShow(show = item.show)
        return EnableShow.changeAll(show = item.show)
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = EnableShowItem().toObject(message = message)
        return this
    }
}