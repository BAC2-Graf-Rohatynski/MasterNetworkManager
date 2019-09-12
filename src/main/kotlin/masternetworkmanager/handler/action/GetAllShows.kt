package masternetworkmanager.handler.action

import apibuilder.database.show.GetAllShowsItem
import databaseclient.action.ShowAction
import enumstorage.database.DatabaseCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GetAllShows: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(GetAllShows::class.java)
    private lateinit var item: GetAllShowsItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${DatabaseCommand.GetAllSlaves.name}' will be executed ...")
        return ShowAction.getAllShows()
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = GetAllShowsItem().toObject(message = message)
        return this
    }
}