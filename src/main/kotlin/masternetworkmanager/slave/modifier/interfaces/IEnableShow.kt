package masternetworkmanager.slave.modifier.interfaces

import apibuilder.slave.Slave

interface IEnableShow {
    fun changeAll(show: String)
    fun changeSingle(show: String, slave: Slave)
}