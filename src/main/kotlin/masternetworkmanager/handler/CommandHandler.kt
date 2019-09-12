package masternetworkmanager.handler

import apibuilder.network.response.ResponseItem
import apibuilder.network.header.Header
import enumstorage.network.NetworkCommand
import masternetworkmanager.command.CommandSocketHandler
import masternetworkmanager.handler.action.*
import masternetworkmanager.handler.interfaces.ICommandHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import masternetworkmanager.handler.action.EnableShow
import java.lang.Exception

object CommandHandler: ICommandHandler {
    private val logger: Logger = LoggerFactory.getLogger(CommandHandler::class.java)

    @Synchronized
    override fun parseMessage(header: Header, message: String) {
        val response: ResponseItem = try {
            val value = when (header.command) {
                NetworkCommand.ClearShow.name -> ClearShow.build(message = message).run()
                NetworkCommand.ControlSsidHandling.name -> ControlSsidHandling.build(message = message).run()
                NetworkCommand.DeleteShow.name -> DeleteShow.build(message = message).run()
                NetworkCommand.EnableShow.name -> EnableShow.build(message = message).run()
                NetworkCommand.ReadShow.name -> ReadShow.build(message = message).run()
                NetworkCommand.RenameShow.name -> RenameShow.build(message = message).run()
                NetworkCommand.SaveShow.name -> SaveShow.build(message = message).run()
                NetworkCommand.UpdateConfiguration.name -> UpdateConfiguration.build(message = message).run()
                NetworkCommand.UpdateDeviceImage.name -> UpdateDeviceImage.build(message = message).run()
                NetworkCommand.UpdateGoboImage.name -> UpdateGoboImage.build(message = message).run()
                NetworkCommand.UpdateInformation.name -> UpdateInformation.build(message = message).run()
                NetworkCommand.UpdateRotation.name -> UpdateRotation.build(message = message).run()
                NetworkCommand.UpdateSlave.name -> UpdateSlave.build(message = message).run()
                NetworkCommand.UpdateSsid.name -> UpdateSsid.build(message = message).run()
                else -> logger.error("Invalid command '${header.command}' received!")
            }

            ResponseItem().create(message = message, value = value)
        } catch (ex: Exception) {
            logger.error("Error occurred while parsing message!\n${ex.message}")
            ResponseItem().create(message = message)
        }

        sendResponse(response = response)
    }

    private fun sendResponse(response: ResponseItem) {
        try {
            CommandSocketHandler.sendResponseMessage(response = response)
        } catch (ex: Exception) {
            logger.error("Error while sending response!\n${ex.message}")
        }
    }
}