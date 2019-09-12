package masternetworkmanager.slave.modifier.interfaces

interface IUpdateRotation {
    fun change(ipAddress: String, isRotating: Boolean)
}