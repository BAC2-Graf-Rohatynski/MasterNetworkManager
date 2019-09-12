package masternetworkmanager.slave.handler.interfaces

import apibuilder.slave.Slave

interface ISlaveAction {
    fun parse(slave: Slave)
}