import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class CompletableQueueFuture : CompletableFuture<Void>() {

    val map = mutableMapOf<Int, CompletableFuture<*>>()

    companion object {
        val executor = Executors.newSingleThreadExecutor()
    }


}