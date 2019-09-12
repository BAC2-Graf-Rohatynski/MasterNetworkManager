package masternetworkmanager.handler.interfaces

import apibuilder.network.header.Header

interface ICommandHandler {
    fun parseMessage(header: Header, message: String)
}