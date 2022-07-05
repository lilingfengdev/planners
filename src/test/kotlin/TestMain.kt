import com.bh.planners.core.skill.effect.Target
import com.bh.planners.util.generatorId

object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val thread1 = Thread() {
            println(generatorId())
            println(generatorId())
            println(generatorId())
            println(generatorId())
            println(generatorId())
        }
        val thread2 = Thread() {
            println(generatorId())
            println(generatorId())
            println(generatorId())
            println(generatorId())
            println(generatorId())
        }
        thread1.start()
        thread2.start()
    }

    class TestTarget : Target {
        override fun toLocal(): String {
            return "test"
        }

    }

}