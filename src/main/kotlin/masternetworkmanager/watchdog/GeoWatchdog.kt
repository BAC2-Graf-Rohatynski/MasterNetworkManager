package masternetworkmanager.watchdog

import apibuilder.slave.request.GetGeoSlaveItem
import apibuilder.slave.Slave
import databaseclient.action.SlaveAction
import masternetworkmanager.MasterNetworkManagerRunner
import masternetworkmanager.watchdog.interfaces.IGeo
import masternetworkmanager.slave.port.SlavePorts
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.WatchdogProperties
import java.lang.Exception
import kotlin.concurrent.thread

object GeoWatchdog: IGeo {
    private val logger: Logger = LoggerFactory.getLogger(GeoWatchdog::class.java)
    private val slavesWithRotation = mutableListOf<String>()
    private val threshold = WatchdogProperties.getRotationInterval()

    init {
        try {
            logger.info("Starting geo watchdog")
            addAllSlavesAtInit()
            sendRotationMessages()
        } catch (ex: Exception) {
            logger.error("Error occurred while running rotation watchdog!\n${ex.message}")
        }
    }

    private fun getAllSlavesWithEnabledRotation(): List<Slave> = SlaveAction.getSlaveByEnabledRotation()

    private fun addAllSlavesAtInit() {
        logger.info("Detecting all slaves with enabled rotation ...")

        getAllSlavesWithEnabledRotation().forEach {slave ->
            try {
                if (!slavesWithRotation.contains(slave.macAddress)) {
                    slavesWithRotation.add(slave.macAddress)
                }
            } catch (ex: Exception) {
                logger.error("Error while retrieving slave information!\n${ex.message}")
            }
        }

        logger.info("All slaves with rotation detected")
    }

    private fun sendRotationMessages() {
        thread {
            val buffer = GetGeoSlaveItem().create().toJson().toByteArray()

            while (MasterNetworkManagerRunner.isRunnable()) {
                try {
                    slavesWithRotation.forEach { macAddress ->
                        logger.info("Calling slave '$macAddress' for geo watchdog ...")
                        UdpHandler.sendSingleUdpMessage(
                                buffer = buffer,
                                ipAddress = SlavePorts.getIpAddress(macAddress = macAddress))
                    }
                } catch (ex: Exception) {
                    logger.error("Error occurred while sending rotation message!\n${ex.message}")
                }

                Thread.sleep(threshold)
            }
        }
    }

    @Synchronized
    override fun addSlave(macAddress: String) {
        if (slavesWithRotation.contains(macAddress)) {
            logger.info("Slave with MAC address '$macAddress' already added!")
        } else {
            if (macAddress.isNotEmpty() && macAddress.isNotBlank()) {
                if (slavesWithRotation.add(macAddress)) {
                    return logger.warn("Slave with MAC address '$macAddress' added!")
                }
            }
        }
    }

    @Synchronized
    override fun removeSlave(macAddress: String) {
        if (slavesWithRotation.contains(macAddress)) {
            if (slavesWithRotation.remove(macAddress)) {
                return logger.info("Slave with MAC address '$macAddress' removed!")
            }
        } else {
            logger.warn("Slave with MAC address '$macAddress' isn't added!")
        }
    }
}