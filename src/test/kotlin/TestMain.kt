import com.bh.planners.api.common.Demand
import kotlinx.coroutines.*
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max
import kotlin.math.min

object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val demand = Demand("")
        println(demand)
    }
    fun random(num1: Int, num2: Int): Int {
        val min = min(num1, num2)
        val max = max(num1, num2)
        return ThreadLocalRandom.current().nextInt(min, max + 1)
    }
    class Proxy(val name: String) {

        override fun hashCode(): Int {
            return name.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true

            other as Proxy

            if (name != other.name) return false

            return true
        }


    }

}