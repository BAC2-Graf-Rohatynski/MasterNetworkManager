package masternetworkmanager.command.interfaces

import apibuilder.network.response.ResponseItem

interface ICommandSocketHandler {
    fun sendResponseMessage(response: ResponseItem)
    fun closeSockets()
}