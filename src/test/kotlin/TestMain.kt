import kotlinx.coroutines.*
import java.time.Duration

object TestMain {

    const val NAME = "KunSs"


    fun String.eval(amount: Double): Double {
        return if (this.last() == '%') {
            println(this.substring(0,this.lastIndex))
            amount * (this.substring(0, this.lastIndex).toDouble() / 100)
        } else {
            this.toDouble()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {


    }

    val FILTER_RULES = listOf(
        Regex("#?+[a-zA-Z0-9]+"),
        Regex("§+[a-zA-Z0-9%]"),
        Regex("[^0-9+--.]"),
    )

    fun getNumber(string: String): String {
        var prey = string
        FILTER_RULES.forEach { prey = prey.replace(it, "") }
        return prey.ifEmpty { "0.0" }
    }
}