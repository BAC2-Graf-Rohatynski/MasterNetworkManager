package masternetworkmanager.handler.action

import apibuilder.network.DeleteShowItem
import databaseclient.action.ShowAction
import enumstorage.database.DatabaseCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

object DeleteShow: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(DeleteShow::class.java)
    private lateinit var item: DeleteShowItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${DatabaseCommand.DeleteShow.name}' will be executed ...")
        assertIfShowExist(show = item.show)
        assertIfShowIsEnabled(show = item.show)
        return ShowAction.deleteShow(show = item.show)
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = DeleteShowItem().toObject(message = message)
        return this
    }

    private fun assertIfShowExist(show: String) {
        if (!ShowAction.getAllShows().contains(show)) {
            throw Exception("Show '$show' doesn't exist!")
        }
    }

    private fun assertIfShowIsEnabled(show: String) {
        if (ShowAction.getEnabledShow() == show) {
            throw Exception("Show '$show' is enabled!")
        }
    }
}