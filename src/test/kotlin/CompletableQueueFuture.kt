import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class CompletableQueueFuture : CompletableFuture<Void>() {

    val map = mutableMapOf<Int, CompletableFuture<*>>()

    companion object {
        val executor = Executors.newSingleThreadExecutor()
    }

    fun add(future: CompletableFuture<*>) {
        map[map.size] = future
    }

    fun check(): CompletableQueueFuture {
        map.forEach { it.value.get() }
        complete(null)
        return this
    }

}