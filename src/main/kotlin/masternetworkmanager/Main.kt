package masternetworkmanager

import masternetworkmanager.dummyslave.DummySlave
import org.apache.log4j.BasicConfigurator

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    DummySlave.add(args = args)
    MasterNetworkManagerRunner.start()
}