package masternetworkmanager.watchdog

import apibuilder.slave.Slave
import databaseclient.action.SlaveAction
import enumstorage.slave.SlaveInformation
import enumstorage.slave.SlaveStatus
import masternetworkmanager.MasterNetworkManagerRunner
import masternetworkmanager.slave.port.SlavePorts
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.WatchdogProperties
import java.lang.Exception
import kotlin.concurrent.thread

object TimestampWatchdog {
    private val logger: Logger = LoggerFactory.getLogger(TimestampWatchdog::class.java)
    private val threshold = WatchdogProperties.getWatchdogTimeout()

    init {
        try {
            startWatchdog()
        } catch (ex: Exception) {
            logger.error("Error occurred while running timestamp watchdog!\n${ex.message}")
        }
    }

    private fun startWatchdog() {
        logger.info("Starting timestamp watchdog with threshold '$threshold' ...")

        thread {
            while (MasterNetworkManagerRunner.isRunnable()) {
                try {
                    logger.info("Calling all slaves for timeout ...")

                    getAllSlaves().forEach { slave ->
                        try {
                            if (checkForValidSlaveTimeStamp(slaveTimeStamp = slave.timestamp)) {
                                if (!slave.macAddress.isBlank() && !slave.macAddress.isEmpty()) {
                                    disableSlaveInDatabaseByMacAddress(macAddress = slave.macAddress)
                                    removePortFromStorage(macAddress = slave.macAddress)
                                } else if (slave.ssid > 0) {
                                    disableSlaveInDatabaseBySsid(ssid = slave.ssid)
                                }
                            }
                        } catch (ex: Exception) {
                            logger.error("Error while retrieving slave information!\n${ex.message}")
                        }
                    }
                } catch (ex: Exception) {
                    logger.error("Error occurred while checking slave timestamps!\n${ex.message}")
                }

                Thread.sleep(threshold)
            }
        }
    }

    private fun getAllSlaves(): List<Slave> = SlaveAction.getAllRecordsInDatabase()

    private fun getCurrentTimeStamp(): Long = System.currentTimeMillis()

    private fun checkForValidSlaveTimeStamp(slaveTimeStamp: Long) = (getCurrentTimeStamp() - slaveTimeStamp) > threshold

    private fun removePortFromStorage(macAddress: String) = SlavePorts.removePortByMacAddress(macAddress = macAddress)

    private fun disableSlaveInDatabaseByMacAddress(macAddress: String) {
        logger.warn("Slave with MAC address $macAddress not responding after ${threshold/1000} s! Will be set to offline!")
        SlaveAction.updateByMacAddress(
                field = SlaveInformation.Status.name,
                value = SlaveStatus.Timeout.name,
                macAddress = macAddress)
        SlavePorts.removePortByMacAddress(macAddress = macAddress)
    }

    private fun disableSlaveInDatabaseBySsid(ssid: Int) {
        logger.warn("Slave with SSID $ssid not responding after ${threshold/1000} s! Will be set to offline!")
        SlaveAction.updateBySsid(
                field = SlaveInformation.Status.name,
                value = SlaveStatus.Timeout.name,
                ssid = ssid)
        SlavePorts.removePortBySsid(ssid = ssid)
    }
}