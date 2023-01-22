import com.bh.planners.api.common.Demand
import kotlinx.coroutines.*
import java.time.Duration

object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val proxy : Any = Proxy("aaa")
        println(proxy == "aaa")
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