package masternetworkmanager.slave.modifier.interfaces

import apibuilder.slave.Slave

interface IGoOnline {
    fun set(slave: Slave)
}