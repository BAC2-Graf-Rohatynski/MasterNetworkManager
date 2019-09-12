package masternetworkmanager.handler.action

import apibuilder.network.RenameShowItem
import databaseclient.action.ShowAction
import enumstorage.database.DatabaseCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import masternetworkmanager.slave.modifier.EnableShow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

object RenameShow: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(RenameShow::class.java)
    private lateinit var item: RenameShowItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${DatabaseCommand.RenameShow.name}' will be executed ...")
        assertIfShowsExist(show = item.show, newShow = item.newShow)

        if (ShowAction.getEnabledShow() == item.show) {
            EnableShow.changeAll(show = item.newShow)
        }

        return ShowAction.renameShow(show = item.show, value = item.newShow)
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = RenameShowItem().toObject(message = message)
        return this
    }

    private fun assertIfShowsExist(show: String, newShow: String) {
        if (!ShowAction.getAllShows().contains(show)) {
            throw Exception("Show '$show' doesn't exist!")
        }

        if (ShowAction.getAllShows().contains(newShow)) {
            throw Exception("Show '$show' already exist!")
        }
    }
}