package masternetworkmanager.slave.port

import apibuilder.slave.Slave
import masternetworkmanager.slave.port.interfaces.ISlavePorts
import masternetworkmanager.watchdog.TimestampWatchdog
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SlavePorts: ISlavePorts {
    private val slaves = mutableListOf<SlavePortObject>()
    private val logger: Logger = LoggerFactory.getLogger(TimestampWatchdog::class.java)

    private fun updateSlaveInformation(slave: Slave) {
        slave.apply {
            slaves.forEach { slavePortObject ->
                if (slavePortObject.macAddress == macAddress) {
                    slavePortObject.ipAddress = ipAddress
                    slavePortObject.commandPort = commandPort
                    slavePortObject.ssid = ssid
                    return logger.info("Slave '$ssid' updated")
                }
            }
        }
    }

    @Synchronized
    override fun addPort(slave: Slave): Boolean {
        slave.apply {
            slaves.forEach { slavePortObject ->
                if (slavePortObject.macAddress == macAddress) {
                    updateSlaveInformation(slave = slave)
                    return false
                }
            }

            slaves.add(SlavePortObject(
                    ipAddress = ipAddress,
                    macAddress = macAddress,
                    commandPort = commandPort,
                    ssid = ssid))

            logger.info("Slave '$ssid' added")
            return true
        }
    }

    @Synchronized
    override fun removePortByMacAddress(macAddress: String): Boolean {
        slaves.forEach { slavePortObject ->
            if (slavePortObject.macAddress == macAddress) {
                logger.info("Slave '$macAddress' added")
                return slaves.remove(slavePortObject)
            }
        }

        return false
    }

    @Synchronized
    override fun removePortBySsid(ssid: Int): Boolean {
        slaves.forEach { slavePortObject ->
            if (slavePortObject.ssid == ssid) {
                logger.info("Slave '$ssid' added")
                return slaves.remove(slavePortObject)
            }
        }

        return false
    }

    @Synchronized
    override fun getPortByIpAddress(ipAddress: String): Int {
        slaves.forEach { slavePortObject ->
            if (slavePortObject.ipAddress == ipAddress) {
                return slavePortObject.commandPort
            }
        }

        return 0
    }

    @Synchronized
    override fun getPortByMacAddress(macAddress: String): Int {
        slaves.forEach { slavePortObject ->
            if (slavePortObject.macAddress == macAddress) {
                return slavePortObject.commandPort
            }
        }

        return 0
    }


    @Synchronized
    override fun getIpAddress(macAddress: String): String {
        slaves.forEach { slavePortObject ->
            if (slavePortObject.macAddress == macAddress) {
                return slavePortObject.ipAddress
            }
        }

        return String()
    }
}