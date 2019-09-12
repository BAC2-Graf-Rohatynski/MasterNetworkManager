package masternetworkmanager.slave.queue

import masternetworkmanager.MasterNetworkManagerRunner
import masternetworkmanager.slave.queue.interfaces.ISlaveQueue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

object SlaveQueue: ISlaveQueue {
    private val receivingQueue = mutableListOf<String>()
    private val logger: Logger = LoggerFactory.getLogger(SlaveQueue::class.java)
    private var isBlocked = false

    override fun putIntoQueue(message: String) {
        try {
            logger.info("Pushing message into queue ...")
            while (isBlocked) {
                Thread.sleep(5)
            }
            isBlocked = true
            receivingQueue.add(message)
            isBlocked = false
            logger.info("Put into queue")
        } catch (ex: Exception) {
            logger.error("Queue error occurred while pushing! Clearing queue ...\n${ex.message}")
            receivingQueue.clear()
            isBlocked = false
        }
    }

    override fun takeFromQueue(): String? {
        try {
            while (MasterNetworkManagerRunner.isRunnable()) {
                if (!isBlocked) {
                    if (receivingQueue.size > 0) {
                        receivingQueue.forEach { result ->
                            logger.info("Taking from queue: $result")
                            isBlocked = true
                            receivingQueue.remove(result)
                            isBlocked = false
                            return result
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            logger.error("Queue error occurred while pulling! Clearing queue ...\n${ex.message}")
            receivingQueue.clear()
            isBlocked = false
        }

        return null
    }
}
