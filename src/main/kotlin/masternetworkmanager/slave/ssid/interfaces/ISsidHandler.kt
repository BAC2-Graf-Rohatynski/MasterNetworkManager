package masternetworkmanager.slave.ssid.interfaces

import apibuilder.slave.Slave

interface ISsidHandler {
    fun run(slave: Slave)
}