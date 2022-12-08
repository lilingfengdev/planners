import com.germ.germplugin.api.dynamic.animation.GermAnimationMove
import org.bukkit.Location

class Test(indexName: String?, val locA: Location, val locB: Location) : GermAnimationMove(indexName) {

    val vectorAB = locB.clone().subtract(locA).toVector()
    val list = mutableListOf<Location>()
    val vectorLength = vectorAB.length()


    init {
        vectorAB.normalize()
        vectorAB.multiply(vectorLength)
        moveX = vectorAB.x.toString()
        moveY = vectorAB.y.toString()
        moveZ = vectorAB.z.toString()
    }


}