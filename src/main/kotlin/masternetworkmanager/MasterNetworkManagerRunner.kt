package masternetworkmanager

import error.ErrorClientRunner
import enumstorage.update.ApplicationName
import masternetworkmanager.command.CommandSocketHandler
import masternetworkmanager.slave.handler.SlaveHandler
import masternetworkmanager.udp.UdpHandler
import masternetworkmanager.watchdog.GeoWatchdog
import masternetworkmanager.watchdog.SlaveConnectWatchdog
import masternetworkmanager.watchdog.TimestampWatchdog
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object MasterNetworkManagerRunner {
    private val logger: Logger = LoggerFactory.getLogger(MasterNetworkManagerRunner::class.java)

    @Volatile
    private var runApplication = true

    fun start() {
        logger.info("Starting application")
        ErrorClientRunner
        UdpHandler
        SlaveConnectWatchdog
        TimestampWatchdog
        GeoWatchdog
        CommandSocketHandler
        SlaveHandler
    }

    @Synchronized
    fun isRunnable(): Boolean = runApplication

    fun stop() {
        logger.info("Stopping application")
        runApplication = false

        databaseclient.command.CommandSocketHandler.closeSockets()
        CommandSocketHandler.closeSockets()
        ErrorClientRunner.stop()
    }

    fun getUpdateInformation(): JSONObject = UpdateInformation.getAsJson(applicationName = ApplicationName.Network.name)
}