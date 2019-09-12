package masternetworkmanager.slave.modifier

import apibuilder.slave.request.UpdateGoboImageSlaveItem
import databaseclient.action.GoboAction
import masternetworkmanager.slave.modifier.interfaces.IUpdateGoboImage
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

object UpdateGoboImage: IUpdateGoboImage {
    private val logger: Logger = LoggerFactory.getLogger(UpdateGoboImage::class.java)

    @Synchronized
    override fun update(fileName: String, ipAddress: String) {
        try {
            logger.info("Change gobo image '$fileName' on slave '$ipAddress' ...")

            GoboAction.getAllItems().forEach { gobo ->
                if (gobo.fileName == fileName) {
                    val item = UpdateGoboImageSlaveItem().create(
                            fileName = gobo.fileName,
                            fileStream = gobo.fileStream,
                            ipAddress = ipAddress)

                    UdpHandler.sendSingleUdpMessage(
                            buffer = item.toJson().toByteArray(),
                            ipAddress = ipAddress)

                    return logger.warn("Gobo '$fileName' updated!")
                }
            }

            throw Exception("Gobo image '$fileName' not found in database!")
        } catch (ex: Exception) {
            logger.error("Error occurred while updating gobo image '$fileName' of slave '$ipAddress'!\n${ex.message}")
        }
    }
}