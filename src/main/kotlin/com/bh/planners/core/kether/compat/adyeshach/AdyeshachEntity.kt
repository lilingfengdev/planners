package com.bh.planners.core.kether.compat.adyeshach

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.Bukkit
import org.bukkit.EntityEffect
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.block.PistonMoveReaction
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Pose
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.metadata.MetadataValue
import org.bukkit.permissions.PermissibleBase
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.plugin.Plugin
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import taboolib.platform.BukkitPlugin
import java.util.*

class AdyeshachEntity(val entity: EntityInstance) : Entity {
    override fun setMetadata(p0: String, p1: MetadataValue) {
        entity.setMetadata(p0, p1)
    }

    override fun getMetadata(p0: String): MutableList<MetadataValue> {
        return entity.getMetadata(p0)
    }

    override fun hasMetadata(p0: String): Boolean {
        return entity.hasTag(p0)
    }

    override fun removeMetadata(p0: String, p1: Plugin) {

    }

    override fun isOp(): Boolean {
        return false
    }

    override fun setOp(p0: Boolean) {

    }

    override fun isPermissionSet(p0: String): Boolean {
        return false
    }

    override fun isPermissionSet(p0: Permission): Boolean {
        return false
    }

    override fun hasPermission(p0: String): Boolean {
        return false
    }

    override fun hasPermission(p0: Permission): Boolean {
        return false
    }

    override fun addAttachment(p0: Plugin, p1: String, p2: Boolean): PermissionAttachment {
        return PermissionAttachment(BukkitPlugin.getInstance(), PermissibleBase(null))
    }

    override fun addAttachment(p0: Plugin): PermissionAttachment {
        return PermissionAttachment(BukkitPlugin.getInstance(), PermissibleBase(null))
    }

    override fun addAttachment(p0: Plugin, p1: String, p2: Boolean, p3: Int): PermissionAttachment? {
        return PermissionAttachment(BukkitPlugin.getInstance(), PermissibleBase(null))
    }

    override fun addAttachment(p0: Plugin, p1: Int): PermissionAttachment? {
        return PermissionAttachment(BukkitPlugin.getInstance(), PermissibleBase(null))
    }

    override fun removeAttachment(p0: PermissionAttachment) {

    }

    override fun recalculatePermissions() {
    }

    override fun getEffectivePermissions(): MutableSet<PermissionAttachmentInfo> {
        return mutableSetOf()
    }

    override fun sendMessage(p0: String) {

    }

    override fun sendMessage(vararg p0: String?) {

    }

    override fun sendMessage(p0: UUID?, p1: String) {

    }

    override fun sendMessage(p0: UUID?, vararg p1: String?) {

    }

    override fun getServer(): Server {
        return Bukkit.getServer()
    }

    override fun getName(): String {
        return entity.getDisplayName()
    }

    override fun spigot(): Entity.Spigot {
        return Entity.Spigot()
    }

    override fun getCustomName(): String? {
        return entity.getCustomName()
    }

    override fun setCustomName(p0: String?) {
        entity.setCustomName(p0!!)
    }

    override fun getPersistentDataContainer(): PersistentDataContainer {
        TODO("Not yet implemented")
    }

    override fun getLocation(): Location {
        return entity.getLocation()
    }

    override fun getLocation(p0: Location?): Location? {
        return entity.getLocation()
    }

    override fun setVelocity(p0: Vector) {
        return entity.sendVelocity(ink.ptms.adyeshach.taboolib.common.util.Vector(p0.x, p0.y, p0.z))
    }

    override fun getVelocity(): Vector {
        return entity.getLocation().toVector()
    }

    override fun getHeight(): Double {
        return entity.entityType.entitySize.height
    }

    override fun getWidth(): Double {
        return entity.entityType.entitySize.width
    }

    override fun getBoundingBox(): BoundingBox {
        TODO("Not yet implemented")
    }

    override fun isOnGround(): Boolean {
        return entity.isControllerOnGround()
    }

    override fun isInWater(): Boolean {
        return false
    }

    override fun getWorld(): World {
        return entity.getWorld()
    }

    override fun setRotation(p0: Float, p1: Float) {
        TODO("Not yet implemented")
    }

    override fun teleport(p0: Location): Boolean {
        entity.teleport(p0)
        return true
    }

    override fun teleport(p0: Location, p1: PlayerTeleportEvent.TeleportCause): Boolean {
        teleport(p0)
        return true
    }

    override fun teleport(p0: Entity): Boolean {
        teleport(p0.location)
        return true

    }

    override fun teleport(p0: Entity, p1: PlayerTeleportEvent.TeleportCause): Boolean {
        teleport(p0)
        return true
    }

    override fun getNearbyEntities(p0: Double, p1: Double, p2: Double): MutableList<Entity> {
        return world.getNearbyEntities(location,p0, p1, p2).toMutableList()
    }

    override fun getEntityId(): Int {
        return entity.index
    }

    override fun getFireTicks(): Int {
        return 0
    }

    override fun getMaxFireTicks(): Int {
        return 0
    }

    override fun setFireTicks(p0: Int) {

    }

    override fun setVisualFire(p0: Boolean) {
    }

    override fun isVisualFire(): Boolean {
        return false
    }

    override fun getFreezeTicks(): Int {
        return 0
    }

    override fun getMaxFreezeTicks(): Int {
        return 0
    }

    override fun setFreezeTicks(p0: Int) {

    }

    override fun isFrozen(): Boolean {
        return false
    }

    override fun remove() {
        entity.remove()
    }

    override fun isDead(): Boolean {
        return entity.isDeleted
    }

    override fun isValid(): Boolean {
        return !entity.isDeleted
    }

    override fun isPersistent(): Boolean {
        return false
    }

    override fun setPersistent(p0: Boolean) {
    }

    override fun getPassenger(): Entity? {
        return null
    }

    override fun setPassenger(p0: Entity): Boolean {
        return false
    }

    override fun getPassengers(): MutableList<Entity> {
        return mutableListOf()
    }

    override fun addPassenger(p0: Entity): Boolean {
        return false
    }

    override fun removePassenger(p0: Entity): Boolean {
        return false
    }

    override fun isEmpty(): Boolean {
        return false
    }

    override fun eject(): Boolean {
        return false
    }

    override fun getFallDistance(): Float {
        return 0f
    }

    override fun setFallDistance(p0: Float) {

    }

    override fun setLastDamageCause(p0: EntityDamageEvent?) {

    }

    override fun getLastDamageCause(): EntityDamageEvent? {
        return null
    }

    override fun getUniqueId(): UUID {
        return entity.normalizeUniqueId
    }

    override fun getTicksLived(): Int {
        return 0
    }

    override fun setTicksLived(p0: Int) {

    }

    override fun playEffect(p0: EntityEffect) {

    }

    override fun getType(): EntityType {
        return EntityType.ARMOR_STAND
    }

    override fun isInsideVehicle(): Boolean {
        return false
    }

    override fun leaveVehicle(): Boolean {
        return false
    }

    override fun getVehicle(): Entity? {
        return null
    }

    override fun setCustomNameVisible(p0: Boolean) {
        entity.setCustomNameVisible(p0)
    }

    override fun isCustomNameVisible(): Boolean {
        return entity.isCustomNameVisible()
    }

    override fun setGlowing(p0: Boolean) {
        entity.setGlowing(p0)
    }

    override fun isGlowing(): Boolean {
        return entity.isGlowing()
    }

    override fun setInvulnerable(p0: Boolean) {
    }

    override fun isInvulnerable(): Boolean {
        return false
    }

    override fun isSilent(): Boolean {
        return false
    }

    override fun setSilent(p0: Boolean) {

    }

    override fun hasGravity(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setGravity(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getPortalCooldown(): Int {
        TODO("Not yet implemented")
    }

    override fun setPortalCooldown(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getScoreboardTags(): MutableSet<String> {
        TODO("Not yet implemented")
    }

    override fun addScoreboardTag(p0: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeScoreboardTag(p0: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPistonMoveReaction(): PistonMoveReaction {
        TODO("Not yet implemented")
    }

    override fun getFacing(): BlockFace {
        return BlockFace.EAST
    }

    override fun getPose(): Pose {
        return Pose.STANDING
    }

}
