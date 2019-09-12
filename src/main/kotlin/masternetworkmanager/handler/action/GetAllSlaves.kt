package masternetworkmanager.handler.action

import apibuilder.database.information.GetAllSlavesItem
import databaseclient.action.SlaveAction
import enumstorage.database.DatabaseCommand
import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GetAllSlaves: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(GetAllSlaves::class.java)
    private lateinit var item: GetAllSlavesItem

    @Synchronized
    override fun run(): Any {
        logger.info("Command '${DatabaseCommand.GetAllSlaves.name}' will be executed ...")
        val slaves = JSONArray()

        SlaveAction.getAllRecordsInDatabase().forEach { slave ->
            slaves.put(slave.toJson())
        }

        return slaves
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        item = GetAllSlavesItem().toObject(message = message)
        return this
    }
}