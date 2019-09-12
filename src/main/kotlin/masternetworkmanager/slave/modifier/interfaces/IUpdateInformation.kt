package masternetworkmanager.slave.modifier.interfaces

import apibuilder.slave.Slave

interface IUpdateInformation {
    fun update(slave: Slave, ipAddress: String)
}