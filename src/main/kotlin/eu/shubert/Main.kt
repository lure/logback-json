package eu.shubert

import eu.shubert.log.LogInfoProvider
import eu.shubert.log.json
import org.slf4j.LoggerFactory
import org.slf4j.MDC

val logger2 = LoggerFactory.getLogger(SomeDataClass::class.java)

data class SomeDataClass(val id: Int)

fun main(args: Array<String>) {
    MDC.put("app", "ConsoleRipper")
    MDC.put("app.version", "v1.23")
    logger2.error("Exception test", ArithmeticException(" zero div"))
    logger2.debug("debug msg")
}


class Abc : LogInfoProvider {
    override fun populate(logObject: json) {
        logObject.apply {
            "connection.port" to 0x037A
        }
    }
}

