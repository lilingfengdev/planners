import com.bh.planners.api.common.Demand
import com.bh.planners.core.effect.Target
import com.bh.planners.util.generatorId

object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        print(exec<Long>(123)::class.java)
    }

    inline fun <reified T> exec(it : Any) : T {
        val value = when (T::class) {
            String::class -> it.toString()
            Int::class -> it.toString().toInt()
            Long::class -> it.toString().toLong()
            Boolean::class -> it.toString().toBoolean()
            Double::class -> it.toString().toDouble()
            else -> it.toString()
        } as T
        return value
    }

}