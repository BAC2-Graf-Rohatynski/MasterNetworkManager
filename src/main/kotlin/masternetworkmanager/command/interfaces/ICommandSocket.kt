package masternetworkmanager.command.interfaces

interface ICommandSocket {
    fun send(message: String?)
    fun closeSockets()
}