package masternetworkmanager.slave.queue.interfaces

interface ISlaveQueue {
    fun putIntoQueue(message: String)
    fun takeFromQueue(): String?
}