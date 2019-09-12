package masternetworkmanager.handler.action

import masternetworkmanager.handler.interfaces.ICommandHandlerAction
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UpdateSlave: ICommandHandlerAction {
    private val logger: Logger = LoggerFactory.getLogger(UpdateSlave::class.java)

    @Synchronized
    override fun run(): Any {
        // TODO
        return String()
    }

    @Synchronized
    override fun build(message: String): ICommandHandlerAction {
        return this
    }
}