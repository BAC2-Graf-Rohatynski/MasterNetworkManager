package masternetworkmanager.slave.ssid.interfaces

interface ISsidActivationHandler {
    fun control(isEnabled: Boolean)
    fun isEnabled(): Boolean
}