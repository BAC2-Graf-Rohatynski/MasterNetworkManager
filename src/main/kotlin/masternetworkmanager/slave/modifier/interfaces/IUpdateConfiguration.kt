package masternetworkmanager.slave.modifier.interfaces

interface IUpdateConfiguration {
    fun update(ddfHash: String, ddfFile: String, ipAddress: String)
}