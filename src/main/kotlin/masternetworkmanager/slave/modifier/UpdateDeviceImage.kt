package masternetworkmanager.slave.modifier

import apibuilder.slave.request.UpdateDeviceImageSlaveItem
import databaseclient.action.ImageAction
import masternetworkmanager.slave.modifier.interfaces.IUpdateDeviceImage
import masternetworkmanager.udp.UdpHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

object UpdateDeviceImage: IUpdateDeviceImage {
    private val logger: Logger = LoggerFactory.getLogger(UpdateDeviceImage::class.java)

    @Synchronized
    override fun update(fileName: String, ipAddress: String) {
        try {
            logger.info("Change device image '$fileName' on slave '$ipAddress' ...")

            ImageAction.getAllItems().forEach { image ->
                if (image.fileName == fileName) {
                    val item = UpdateDeviceImageSlaveItem().create(
                            fileName = image.fileName,
                            fileStream = image.fileStream,
                            ipAddress = ipAddress)

                    UdpHandler.sendSingleUdpMessage(
                            buffer = item.toJson().toByteArray(),
                            ipAddress = ipAddress)

                    return logger.warn("Image '$fileName' updated!")
                }
            }

            throw Exception("Device image '$fileName' not found in database!")
        } catch (ex: Exception) {
            logger.error("Error occurred while updating device image '$fileName' of slave '$ipAddress'!\n${ex.message}")
        }
    }
}