package masternetworkmanager.handler.interfaces

interface ICommandHandlerAction {
    fun run(): Any
    fun build(message: String): ICommandHandlerAction
}