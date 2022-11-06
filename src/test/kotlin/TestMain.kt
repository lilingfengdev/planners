import com.bh.planners.api.common.Demand
import com.bh.planners.util.eval

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
        val eval = "1%".eval(40.0)
        println(eval)
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