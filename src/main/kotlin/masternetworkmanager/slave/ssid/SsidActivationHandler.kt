package masternetworkmanager.slave.ssid

import masternetworkmanager.slave.ssid.interfaces.ISsidActivationHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SsidActivationHandler: ISsidActivationHandler {
    private val logger: Logger = LoggerFactory.getLogger(SsidHandler::class.java)
    private var isEnabled = true

    @Synchronized
    override fun control(isEnabled: Boolean) {
        SsidActivationHandler.isEnabled = isEnabled
        if (isEnabled) logger.info("SSID handler enabled") else logger.info("SSID handler disabled")
    }

    @Synchronized
    override fun isEnabled(): Boolean = isEnabled
}