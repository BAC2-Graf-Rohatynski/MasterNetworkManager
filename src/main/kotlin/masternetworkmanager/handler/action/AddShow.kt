package masternetworkmanager.handler.action

import apibuilder.database.show.AddShowItem
import databaseclient.action.ShowAction
import enumstorage.database.DatabaseCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

object AddShow: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(AddShow::class.java)
    private lateinit var item: AddShowItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${DatabaseCommand.AddShow.name}' will be executed ...")
        assertIfShowExist(show = item.show)
        return ShowAction.newShow(show = item.show)
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = AddShowItem().toObject(message = message)
        return this
    }

    private fun assertIfShowExist(show: String) {
        if (ShowAction.getAllShows().contains(show)) {
            throw Exception("Show '$show' already exist!")
        }
    }
}