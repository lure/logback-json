package eu.shubert.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import ch.qos.logback.core.LayoutBase
import ch.qos.logback.core.util.Loader
import org.json.JSONObject
import java.lang.reflect.Constructor


//==============================================================================================================//
interface LogInfoProvider {
    fun populate(logObject: json)
}

@Suppress("ClassName")
class json() : JSONObject() {
    constructor(build: json.() -> Any) : this() {
        this.build()
    }

    infix fun <T> String.to(value: T) {
        put(this, value)
    }
}

@Suppress("unused")
class JsonLayout : LayoutBase<ILoggingEvent>() {
    private val filterMDC: MutableSet<String> = mutableSetOf()
    private val providers: MutableSet<LogInfoProvider> = mutableSetOf()

    override fun getContentType(): String = "application/json"

    override fun doLayout(event: ILoggingEvent): String {
        val j = json {
            "@timestamp" to event.timeStamp
            "level" to event.level
            "loggerName" to event.loggerName
            "message" to event.formattedMessage

            if (event.throwableProxy != null) {
                "exception" to json {
                    "exceptionClass" to event.throwableProxy.className
                    "exceptionMessage" to event.throwableProxy.message
                    "stacktrace" to ThrowableProxyUtil.asString(event.throwableProxy)
                }
            }

            // filter mdc if filter is not empty
            val mdc = event.mdcPropertyMap
            if (filterMDC.isNotEmpty()) {
                filterMDC.forEach { filter ->
                    mdc.getOrDefault(filter, null)?.let { mdcval ->
                        filter to mdcval
                    }
                }
            } else {
                mdc.forEach { it.key to it.value }
            }

            providers.forEach { it.populate(this@json) }
        }.toString()
        return j + "\n"
    }


    /**
     * Set comma-separated list of the only values permitted to be added from the MDC.
     * Usage scenario is to filter out some sensible information.
     */
    fun setFilterMDCFields(value: String) {
        value.split(',').forEach {
            filterMDC.add(it.trim())
        }
    }

    /**
     * Register additional log information providers.
     * As an example think of application version, node name, cloud region etc.
     * The information should be readily available and not require the recalculation to not harm overall performance.
     */
    fun setAdditionalProvider(providerClass: String) {
        try {
            val clazz: Class<*> = Loader.loadClass(providerClass)
            val cons: Constructor<*> = clazz.getConstructor()
            val instance = cons.newInstance() as LogInfoProvider
            providers.add(instance)
        } catch (e: Throwable) {
            addWarn("Unable to instantiate [$providerClass] for property [additionalProvider]: ${e.message}")
        }
    }
}
