package masternetworkmanager.slave.handler

import apibuilder.slave.Slave
import apibuilder.slave.response.header.HeaderBuilder
import enumstorage.slave.SlaveResponse
import masternetworkmanager.MasterNetworkManagerRunner
import masternetworkmanager.slave.handler.action.*
import masternetworkmanager.slave.handler.interfaces.ISlaveHandler
import masternetworkmanager.slave.queue.SlaveQueue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import kotlin.concurrent.thread

object SlaveHandler: ISlaveHandler {
    private val logger: Logger = LoggerFactory.getLogger(SlaveHandler::class.java)

    init {
        try {
            logger.info("Starting slave handler ...")
            parseIncomingMessages()
        } catch (ex: Exception) {
            logger.error("Error occurred while running slave handler!\n${ex.message}")
        }
    }

    private fun parseIncomingMessages() {
        thread {
            while (MasterNetworkManagerRunner.isRunnable()) {
                try {
                    val message = SlaveQueue.takeFromQueue()

                    if (message != null) {
                        logger.info("Parsing slave message ...")
                        val header = HeaderBuilder().build(message = message)

                        when (header.command) {
                            SlaveResponse.Connect.name -> {
                                val slave = Slave().parseConnect(message = message)
                                Connect.parse(slave = slave)
                            }

                            SlaveResponse.FirstConnect.name -> {
                                val slave = Slave().parseFirstConnect(message = message)
                                FirstConnect.parse(slave = slave)
                            }

                            SlaveResponse.Read.name -> {
                                val slave = Slave().parseRead(message = message)
                                Read.parse(slave = slave)
                            }

                            SlaveResponse.Greeting.name -> {
                                val slave = Slave().parseGreeting(message = message)
                                Greeting.parse(slave = slave)
                            }

                            SlaveResponse.Geo.name -> {
                                val slave = Slave().parseGeo(message = message)
                                Geo.parse(slave = slave)
                            }

                            SlaveResponse.Error.name -> {
                                val slave = Slave().parseError(message = message)
                                Error.parse(slave = slave)
                            }

                            else -> logger.warn("Unsupported slave response '${header.command}' received!")
                        }
                    }
                } catch (ex: Exception) {
                    logger.error("Error occurred while parsing incoming slave message!\n${ex.message}")
                }
            }
        }
    }
}