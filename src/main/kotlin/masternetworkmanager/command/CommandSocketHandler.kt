package masternetworkmanager.command

import apibuilder.network.response.ResponseItem
import masternetworkmanager.MasterNetworkManagerRunner
import masternetworkmanager.command.interfaces.ICommandSocketHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.PortProperties
import java.net.ServerSocket
import kotlin.concurrent.thread

/**
 * This handler manages all incoming client requests.
 *
 * @author      Markus Graf
 * @see         java.net.ServerSocket
 */
object CommandSocketHandler: ICommandSocketHandler {
    private lateinit var serverSocket: ServerSocket
    private var clientSockets = mutableListOf<CommandSocket>()
    private val port: Int = PortProperties.getNetworkPort()
    private val logger: Logger = LoggerFactory.getLogger(CommandSocketHandler::class.java)

    init {
        thread {
            try {
                openSockets()
                acceptClients()
            } catch (ex: Exception) {
                logger.error("Error occurred while running socket handler!\n${ex.message}")
            } finally {
                closeSockets()
            }
        }
    }

    @Synchronized
    override fun sendResponseMessage(response: ResponseItem) {
        clientSockets.forEach { clientSocket ->
            clientSocket.send(message = response.toJson())
        }
    }

    private fun acceptClients() {
        while (MasterNetworkManagerRunner.isRunnable()) {
            logger.info("Waiting for clients ...")
            val clientSocket = CommandSocket(clientSocket = serverSocket.accept())
            clientSocket.start()
            clientSockets.add(clientSocket)
            logger.info("Client added")
        }
    }

    private fun openSockets() {
        logger.info("Opening socket on port '$port' ...")
        serverSocket = ServerSocket(port)
        logger.info("Socket opened")
    }

    @Synchronized
    override fun closeSockets() {
        try {
            logger.info("Closing sockets ...")

            if (::serverSocket.isInitialized) {
                serverSocket.close()
            }

            clientSockets.forEach { clientSocket ->
                clientSocket.closeSockets()
            }

            clientSockets.clear()

            logger.info("Sockets closed")
        } catch (ex: Exception) {
            logger.error("Error occurred while closing sockets!\n${ex.message}")
        }
    }
}