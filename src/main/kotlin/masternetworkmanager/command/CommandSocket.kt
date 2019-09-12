package masternetworkmanager.command

import masternetworkmanager.MasterNetworkManagerRunner
import masternetworkmanager.command.interfaces.ICommandSocket
import masternetworkmanager.handler.CommandHandler
import apibuilder.network.header.HeaderBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.lang.Exception

class CommandSocket(private val clientSocket: Socket): Thread(), ICommandSocket {
    private lateinit var printWriter: PrintWriter
    private lateinit var bufferedReader: BufferedReader
    private val logger: Logger = LoggerFactory.getLogger(CommandSocket::class.java)

    override fun run() {
        try {
            openSockets()
            receive()
        } catch (ex: Exception) {
            logger.error("Error occurred while running command socket!\n${ex.message}")
        } finally {
            closeSockets()
        }
    }

    @Synchronized
    override fun send(message: String?) {
        try {
            if (message != null) {
                logger.info("Message to send: $message")
                printWriter.println(message)
            }
        } catch (ex: Exception) {
            logger.error("Error while sending message!\n${ex.message}")
        }
    }

    private fun receive() {
        bufferedReader.use {
            try {
                while (MasterNetworkManagerRunner.isRunnable()) {
                    val inputLine = bufferedReader.readLine()

                    if (inputLine != null) {
                        logger.info("Message '$inputLine' received")
                        val header = HeaderBuilder().build(message = inputLine)
                        CommandHandler.parseMessage(message = inputLine, header = header)
                    }
                }
            } catch (ex: Exception) {
                logger.error("Error occurred while receiving commands!\n${ex.message}")
            }
        }
    }

    private fun openSockets() {
        try {
            logger.info("Opening sockets ...")
            printWriter = PrintWriter(clientSocket.getOutputStream(), true)
            bufferedReader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            logger.info("Sockets opened")
        } catch (ex: Exception) {
            logger.error("Error occurred while opening sockets!\n${ex.message}")
            closeSockets()
        }
    }

    @Synchronized
    override fun closeSockets() {
        try {
            logger.info("Closing sockets ...")

            if (::printWriter.isInitialized) {
                printWriter.close()
            }

            if (!clientSocket.isClosed) {
                clientSocket.close()
            }
            logger.info("Sockets closed")
        } catch (ex: Exception) {
            logger.error("Error occurred while closing sockets!\n${ex.message}")
        }
    }
}