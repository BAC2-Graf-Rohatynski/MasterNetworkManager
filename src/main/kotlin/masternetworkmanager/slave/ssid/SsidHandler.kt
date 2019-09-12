package masternetworkmanager.slave.ssid

import databaseclient.action.ConfigAction
import databaseclient.action.ShowAction
import databaseclient.action.SlaveAction
import enumstorage.database.DatabaseType
import enumstorage.slave.SlaveInformation
import enumstorage.slave.SlaveStatus
import masternetworkmanager.slave.ssid.interfaces.ISsidHandler
import masternetworkmanager.slave.modifier.EnableShow
import masternetworkmanager.slave.modifier.UpdateSsid
import masternetworkmanager.slave.modifier.GoOnline
import apibuilder.slave.Slave
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import propertystorage.WatchdogProperties

class SsidHandler: Thread(), ISsidHandler {
    private val logger: Logger = LoggerFactory.getLogger(SsidHandler::class.java)
    private val watchdogTimeout = WatchdogProperties.getWatchdogTimeout()
    private val enabledShow = ShowAction.getEnabledShow()

    override fun run(slave: Slave) {
        try {
            checkRoutine(slaveObject = slave)
        } catch (ex: Exception) {
            logger.error("Error in SSID handler!\n${ex.message}")
        }
    }

    private fun checkRoutine(slaveObject: Slave, firstRun: Boolean = true) {
        var slave = slaveObject
        showEntries(slaves = SlaveAction.getAllRecordsInDatabase(), tableName = DatabaseType.Information.name)
        showEntries(slaves = ConfigAction.getAllRecordsInDatabase(show = slave.show), tableName = DatabaseType.Configuration.name)

        if (!checkForShow(slave = slave)) {
            changeShow(slave = slave)
            return logger.warn("Slave ${slave.macAddressTemporary} has wrong show. Changing the show ...")
        }

        val configurations = getSlaveConfigurationByDdfHash(slave = slave)
        if (!checkForMatchingDdfHash(configurations = configurations)) {
            return addSlaveAndSetOnline(slave = slave)
        }

        if (!checkForMatchingTemporarySsid(slave = slave, configurations = configurations)) {
            if (checkForMultipleSlavesWithMatchingDdfHash(configurations = configurations)) {
                logger.info("No matching temporary SSID for multiple matching DDF hashes found. Therefore, a slave cannot " +
                        "be assigned without any risks to overwrite the wrong slave!")
                return addSlaveAndSetOnline(slave = slave)
            } else {
                if (checkIfSlaveInDbIsOnlineBySsid(configurations = configurations)) {
                    logger.info("No matching temporary SSID found for slave '${slave.macAddress}' whose slave is offline, " +
                            "so a new SSID will be generated for this slave ...")
                    return addSlaveAndSetOnline(slave = slave)
                }

                logger.info("No matching temporary SSID found for slave '${slave.macAddress}', but original slave isn't " +
                        "online. Trying to merge or add as new slave otherwise ....")
                slave = updateTemporarySsid(slave = slave, configurations = configurations)
            }
        }

        val information = getSlaveInformationBySsid(ssid = slave.ssid)

        if (information.isEmpty()) {
            logger.info("No slave is found in DB. Creating new entry ...")
            return addSlaveAndSetOnline(slave = slave)
        }

        if (checkForMacAddressesAndIpAddressAvailable(information = information)) {
            setTemporaryAndOriginalMacAddressInDatabase(slave = slave)
            setIpAddressInDatabase(slave = slave)
            return slaveIsOk(slave = slave)
        }

        if (checkForTemporaryMacAddress(information = information, slave = slave)) {
            logger.info("Original slave for '${slave.macAddress}' found ...")
            return slaveIsOk(slave = slave)
        }

        if (!checkForOriginalMacAddress(information = information, slave = slave)) {
            /**
             *  E02
             */
            if (firstRun) {
                waitForWatchdogTimeout()
            }

            if (checkIfSlaveInDbIsOnlineByInformation(information = information)) {
                logger.info("Slave '${slave.macAddress}': Valid DDF hash, SSID and temporary MAC but with " +
                        "differing original MAC. After $watchdogTimeout s the original slave is still Online, " +
                        "so a new SSID will be generated for this slave ...")
                return addSlaveAndSetOnline(slave = slaveObject)
            }

            logger.info("New slave detected! Replacing old position ...")
        } else {
            logger.info("Original slave detected! Replacing old position ...")
        }

        updateTemporaryMacAddress(slave = slave)
        slaveIsOk(slave = slave)
    }

    /**
     *  A01
     */
    private fun checkForShow(slave: Slave): Boolean {
        logger.info("Checking for valid show on slave ...")
        return enabledShow == slave.show
    }

    /**
     *  A02
     */
    private fun checkForMatchingDdfHash(configurations: List<Slave>): Boolean {
        logger.info("Checking for valid DDF config on slave ...")

        return if (configurations.isEmpty()) {
            logger.info("No matching DDF config found', so a new SSID will be generated for this slave ...")
            false
        } else {
            true
        }
    }

    /**
     *  A03
     */
    private fun checkForMatchingTemporarySsid(slave: Slave, configurations: List<Slave>): Boolean {
        logger.info("Checking for valid temporary SSID on slave ...")

        configurations.forEach { configuration ->
            if (slave.ssid == configuration.ssid) {
                logger.info("Matching temporary SSID found")
                return true
            }
        }

        logger.info("No matching temporary SSID found")
        return false
    }

    /**
     *  A04
     */
    private fun checkForMacAddressesAndIpAddressAvailable(information: List<Slave>): Boolean =
            information.first().macAddressTemporary.isEmpty() &&
            information.first().macAddress.isEmpty() &&
            information.first().ipAddress.isEmpty()

    /**
     *  A05
     */
    private fun checkForTemporaryMacAddress(information: List<Slave>, slave: Slave): Boolean {
        logger.info("Checking for valid temporary MAC address on slave ...")
        return information.first().macAddressTemporary == slave.macAddress
    }

    /**
     *  A06 & A07
     */
    private fun setToOnline(slave: Slave) = GoOnline.set(slave = slave)

    /**
     *  A08
     */
    private fun updateGeo(slave: Slave) {
        slave.apply {
            SlaveAction.updateGeo(slave = slave)
        }
    }


    /**
     *  A09
     */
    private fun updateTimestamp(slave: Slave) = SlaveAction.updateTimeStamp(macAddress = slave.macAddress)

    /**
     *  B01
     */
    private fun getNewSsid(): Int = SlaveAction.getNewSsid()

    /**
     *  B02
     */
    private fun addNewSlave(slave: Slave, newSsid: Int): Slave {
        slave.apply {
            UpdateSsid.change(ipAddress = ipAddress, ssid = ssid)
            SlaveAction.addSlave(slave = this)
        }
        
        logger.info("New slave with SSID '$newSsid' added")
        return slave
    }

    /**
     *  B03
     */
    private fun saveSlaveConfigInDatabase(slave: Slave) {
        slave.apply {
            logger.info("Config of slave ${toJson()} will be added to database ...")
            ConfigAction.addConfig(ssid = ssid, ddfHash = ddfHash, ddfFile = ddfFile, show = show)
            logger.info("Config of slave with SSID '${ssid}' added")
        }
    }

    /**
     *  C01
     */
    private fun checkForMultipleSlavesWithMatchingDdfHash(configurations: List<Slave>): Boolean = configurations.size != 2

    /**
     *  C02
     */
    private fun checkIfSlaveInDbIsOnlineBySsid(configurations: List<Slave>): Boolean {
        val information = getSlaveInformationBySsid(ssid = configurations.first().ssid)
        logger.info("SSID handler || Is DDF matching slave online? || $information")
        return information.first().status == SlaveStatus.Online.name
    }

    /**
     *  E04
     */
    private fun checkIfSlaveInDbIsOnlineByInformation(information: List<Slave>): Boolean {
        logger.info("SSID handler || Is DDF matching slave online? || $information")
        return information.first().status == SlaveStatus.Online.name
    }

    /**
     *  C03
     */
    private fun updateTemporarySsid(slave: Slave, configurations: List<Slave>): Slave {
        slave.ssid = configurations.first().ssid
        UpdateSsid.change(ipAddress = slave.ipAddress, ssid = slave.ssid)
        return slave
    }

    /**
     *  D01
     */
    private fun setTemporaryAndOriginalMacAddressInDatabase(slave: Slave) {
        logger.info("MAC addresses not saved for slave '${slave.macAddress}. Setting MAC addresses in database ...")
        SlaveAction.updateBySsid(ssid = slave.ssid, field = SlaveInformation.MacAddress.name, value = slave.macAddress)
        SlaveAction.updateBySsid(ssid = slave.ssid, field = SlaveInformation.MacAddressTemporary.name, value = slave.macAddress)
    }

    /**
     *  D02
     */
    private fun setIpAddressInDatabase(slave: Slave) {
        logger.info("Setting IP address in database for slave '${slave.ipAddress} ...")
        SlaveAction.updateBySsid(ssid = slave.ssid, field = SlaveInformation.IpAddress.name, value = slave.ipAddress)
    }

    /**
     *  E01
     */
    private fun checkForOriginalMacAddress(information: List<Slave>, slave: Slave): Boolean {
        logger.info("Checking for valid original MAC address on slave ...")
        return information.first().macAddressTemporary == slave.macAddress
    }

    /**
     *  E03
     */
    private fun waitForWatchdogTimeout() {
        logger.info("Waiting for watchdog timeout of ${watchdogTimeout / 1000} s to verify if the original slave is still offline ...")
        sleep(watchdogTimeout)
    }

    /**
     *  E05
     */
    private fun updateTemporaryMacAddress(slave: Slave) = SlaveAction.updateBySsid(
            ssid = slave.ssid,
            field = SlaveInformation.MacAddressTemporary.name,
            value = slave.macAddress)

    /**
     *  F01
     */
    private fun changeShow(slave: Slave) = EnableShow.changeSingle(show = enabledShow, slave = slave)

    /*******************************************************************************************************************
     * HELPER FUNCTIONS
     ******************************************************************************************************************/

    /**
     *  B01 & B02 & B03 & A06 & A07 & A08 & A09
     */
    private fun addSlaveAndSetOnline(slave: Slave) {
        logger.info("Slave ${slave.macAddress} gets new SSID ...")
        val newSsid = getNewSsid()
        val newSlave = addNewSlave(slave = slave, newSsid = newSsid)
        saveSlaveConfigInDatabase(slave = newSlave)
        slaveIsOk(slave = slave)
    }

    /**
     *  A06 & A07 & A08 & A09
     */
    private fun slaveIsOk(slave: Slave) {
        setToOnline(slave = slave)
        updateTimestamp(slave = slave)
        updateGeo(slave = slave)
        logger.info("Slave is online")
    }

    private fun getSlaveConfigurationByDdfHash(slave: Slave): List<Slave> {
        logger.info("Requesting slave config for '${slave.ddfHash}' ...")
        return ConfigAction.getConfigurationByDdfHash(ddfHash = slave.ddfHash, show = enabledShow)
    }

    private fun getSlaveInformationBySsid(ssid: Int): List<Slave> = SlaveAction.getSlaveBySsid(ssid = ssid)

    private fun showEntries(slaves: List<Slave>, tableName: String) {
        logger.info("-------------------------------------------------------------")
        logger.info("-------------------------------------------------------------\n")
        logger.info("Database entries of table $tableName")

        slaves.forEach { slave ->
            logger.info("*********************************************************")
            logger.info("SSID: ${slave.ssid}")
            logger.info("MAC Address: ${slave.macAddress}")
            logger.info("MAC Address Temporary: ${slave.macAddressTemporary}")
            logger.info("DDF Hash: ${slave.ddfHash} ")
            logger.info("*********************************************************\n")
        }

        logger.info("-------------------------------------------------------------")
        logger.info("-------------------------------------------------------------\n")
    }
}