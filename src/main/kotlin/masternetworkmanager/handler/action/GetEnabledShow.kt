package masternetworkmanager.handler.action

import apibuilder.database.show.GetEnabledShowItem
import databaseclient.action.ShowAction
import enumstorage.database.DatabaseCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GetEnabledShow: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(GetEnabledShow::class.java)
    private lateinit var item: GetEnabledShowItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${DatabaseCommand.GetEnabledShow.name}' will be executed ...")
        return ShowAction.getEnabledShow()
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = GetEnabledShowItem().toObject(message = message)
        return this
    }
}