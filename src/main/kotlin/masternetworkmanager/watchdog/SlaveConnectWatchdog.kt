package masternetworkmanager.watchdog

import apibuilder.slave.request.ConnectSlaveItem
import interfacehelper.MyIpAddress
import masternetworkmanager.MasterNetworkManagerRunner
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.WatchdogProperties
import java.lang.Exception
import kotlin.concurrent.thread

object SlaveConnectWatchdog {
    private val timeout = WatchdogProperties.getSsidHandlerTimeout()
    private val logger: Logger = LoggerFactory.getLogger(SlaveConnectWatchdog::class.java)

    init {
        try {
            logger.info("Starting slave info watchdog")
            startWatchdog()
        } catch (ex: Exception) {
            logger.error("Error occurred while running slave connect watchdog\n${ex.message}")
        }
    }

    private fun startWatchdog() {
        thread {
            val buffer = ConnectSlaveItem().create(
                    ipAddress = MyIpAddress.getAsString() ?: throw Exception("IP address cannot be received!"))
                    .toString()
                    .toByteArray()

            while (MasterNetworkManagerRunner.isRunnable()) {
                try {
                    logger.info("Calling all slaves for connecting ...")
                    UdpHandler.sendBroadcastMessageOverUdp(buffer = buffer)
                } catch (ex: Exception) {
                    logger.error("Error occurred while sending connect message!\n${ex.message}")
                }

                Thread.sleep(timeout)
            }
        }
    }
}