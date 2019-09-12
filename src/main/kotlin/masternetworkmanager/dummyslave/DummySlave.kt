package masternetworkmanager.dummyslave

import apibuilder.RsaModule
import apibuilder.json.Json
import apibuilder.slave.Slave
import databaseclient.action.ConfigAction
import databaseclient.action.SlaveAction
import masternetworkmanager.dummyslave.interfaces.IDummySlave
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

object DummySlave: IDummySlave {
    private val logger: Logger = LoggerFactory.getLogger(DummySlave::class.java)

    @Synchronized
    override fun add(args: Array<String>) {
        try {
            logger.info("Loading dummy slave ...")

            getDummies(args = args).forEach { slave ->
                slave.apply {
                    SlaveAction.addSlave(slave = this)
                    ConfigAction.addConfig(ssid = ssid, ddfFile = ddfFile, ddfHash = ddfHash, show = show)
                }

                logger.info("Dummy slave loaded")
            }
        } catch (ex: Exception) {
            logger.warn("Error while loading dummy slave!\n${ex.message}")
        }
    }

    private fun getDummies(args: Array<String>): MutableList<Slave> {
        logger.info("Reading and parsing dummy slave file ...")
        val slaves = if (args.isNotEmpty()) parseApplicationArguments(args = args) else parseConfigurationFile()
        return parseJsonConfig(slaves = slaves)
    }

    private fun parseJsonConfig(slaves: JSONArray): MutableList<Slave> {
        val dummies = mutableListOf<Slave>()

        slaves.forEach { slave ->
            slave as JSONObject
            val encryptedMessage = RsaModule.encryptApiMessage(message = JSONArray("[$slave]"))
            dummies.add(Slave().parseRead(message = encryptedMessage))
        }

        return dummies
    }

    private fun parseApplicationArguments(args: Array<String>): JSONArray {
        return try {
            logger.info("Parsing external configuration ...")
            val configuration = JSONArray(Json.replaceUnwantedChars(stringObject = args.first()))
            logger.info("Slave configuration: $configuration")
            configuration
        } catch (ex: Exception) {
            throw Exception("Error while starting handler with external configuration. Check your program arguments.\n${ex.message}")
        }
    }

    private fun parseConfigurationFile(): JSONArray {
        val stringBuilder = StringBuilder()
        val inputStream = DummySlave::class.java.classLoader.getResourceAsStream("slaves.txt")

        try {
            inputStream.bufferedReader().useLines { bufferedReader ->
                for (line in bufferedReader) {
                    stringBuilder.append(line)
                    stringBuilder.append('\n')
                }
            }
        } catch (ex: Exception) {
            throw Exception("Error occurred while reading dummy file!\n${ex.message}")
        }

        val configuration = stringBuilder.toString()
        logger.info("Slave configuration: $configuration")
        return JSONArray(configuration)
    }
}