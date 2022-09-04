package com.bh.planners.core.kether.compat.adyeshach

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.PistonMoveReaction
import org.bukkit.entity.*
import org.bukkit.entity.memory.MemoryKey
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.EntityEquipment
import org.bukkit.metadata.MetadataValue
import org.bukkit.permissions.PermissibleBase
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.BoundingBox
import org.bukkit.util.RayTraceResult
import org.bukkit.util.Vector
import taboolib.platform.BukkitPlugin
import java.util.*

class AdyeshachEntity(val entity: EntityInstance) : LivingEntity {

    val id: String
        get() = entity.id

    override fun getAttribute(p0: Attribute): AttributeInstance? {
        return null
    }

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
        val location = location.clone().add(p0)
        teleport(location)
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
        return world.getNearbyEntities(location, p0, p1, p2).toMutableList()
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
        return !entity.isNoGravity()
    }

    override fun setGravity(p0: Boolean) {
        entity.setNoGravity(!p0)
    }

    override fun getPortalCooldown(): Int {
        return 0
    }

    override fun setPortalCooldown(p0: Int) {

    }

    override fun getScoreboardTags(): MutableSet<String> {
        return mutableSetOf()
    }

    override fun addScoreboardTag(p0: String): Boolean {
        return false
    }

    override fun removeScoreboardTag(p0: String): Boolean {
        return false
    }

    override fun getPistonMoveReaction(): PistonMoveReaction {
        return PistonMoveReaction.MOVE
    }

    override fun getFacing(): BlockFace {
        return BlockFace.EAST
    }

    override fun getPose(): Pose {
        return Pose.STANDING
    }

    override fun getSpawnCategory(): SpawnCategory {
        return SpawnCategory.ANIMAL
    }

    override fun damage(p0: Double) {

    }

    override fun damage(p0: Double, p1: Entity?) {

    }

    override fun getHealth(): Double {
        return 1.0
    }

    override fun setHealth(p0: Double) {

    }

    override fun getAbsorptionAmount(): Double {
        return 0.0
    }

    override fun setAbsorptionAmount(p0: Double) {

    }

    override fun getMaxHealth(): Double {
        return 1.0
    }

    override fun setMaxHealth(p0: Double) {
    }

    override fun resetMaxHealth() {
    }

    override fun <T : Projectile?> launchProjectile(p0: Class<out T>): T {
        return p0.newInstance()
    }

    override fun <T : Projectile?> launchProjectile(p0: Class<out T>, p1: Vector?): T {
        return p0.newInstance()
    }

    override fun getEyeHeight(): Double {
        return entity.entityType.entitySize.height
    }

    override fun getEyeHeight(p0: Boolean): Double {
        return entity.entityType.entitySize.height
    }

    override fun getEyeLocation(): Location {
        return entity.getLocation()
    }

    override fun getLineOfSight(p0: MutableSet<Material>?, p1: Int): MutableList<Block> {
        return mutableListOf()
    }

    override fun getTargetBlock(p0: MutableSet<Material>?, p1: Int): Block {
        return entity.getLocation().block
    }

    override fun getLastTwoTargetBlocks(p0: MutableSet<Material>?, p1: Int): MutableList<Block> {
        return mutableListOf()
    }

    override fun getTargetBlockExact(p0: Int): Block? {
        return null
    }

    override fun getTargetBlockExact(p0: Int, p1: FluidCollisionMode): Block? {
        return null
    }

    override fun rayTraceBlocks(p0: Double): RayTraceResult? {
        return null
    }

    override fun rayTraceBlocks(p0: Double, p1: FluidCollisionMode): RayTraceResult? {
        return null
    }

    override fun getRemainingAir(): Int {
        return 0
    }

    override fun setRemainingAir(p0: Int) {

    }

    override fun getMaximumAir(): Int {
        return 0
    }

    override fun setMaximumAir(p0: Int) {

    }

    override fun getArrowCooldown(): Int {
        return 0
    }

    override fun setArrowCooldown(p0: Int) {

    }

    override fun getArrowsInBody(): Int {
        return 0
    }

    override fun setArrowsInBody(p0: Int) {

    }

    override fun getMaximumNoDamageTicks(): Int {
        return 0
    }

    override fun setMaximumNoDamageTicks(p0: Int) {

    }

    override fun getLastDamage(): Double {
        return 0.0
    }

    override fun setLastDamage(p0: Double) {
    }

    override fun getNoDamageTicks(): Int {
        return 0
    }

    override fun setNoDamageTicks(p0: Int) {
    }

    override fun getKiller(): Player? {
        return null
    }

    override fun addPotionEffect(p0: PotionEffect): Boolean {
        return false
    }

    override fun addPotionEffect(p0: PotionEffect, p1: Boolean): Boolean {
        return false
    }

    override fun addPotionEffects(p0: MutableCollection<PotionEffect>): Boolean {
        return false
    }

    override fun hasPotionEffect(p0: PotionEffectType): Boolean {
        return false
    }

    override fun getPotionEffect(p0: PotionEffectType): PotionEffect? {
        return null
    }

    override fun removePotionEffect(p0: PotionEffectType) {

    }

    override fun getActivePotionEffects(): MutableCollection<PotionEffect> {
        return mutableListOf()
    }

    override fun hasLineOfSight(p0: Entity): Boolean {
        return false
    }

    override fun getRemoveWhenFarAway(): Boolean {
        return false
    }

    override fun setRemoveWhenFarAway(p0: Boolean) {
    }

    override fun getEquipment(): EntityEquipment? {
        return null
    }

    override fun setCanPickupItems(p0: Boolean) {
    }

    override fun getCanPickupItems(): Boolean {
        return false
    }

    override fun isLeashed(): Boolean {
        return false
    }

    override fun getLeashHolder(): Entity {
        error("Entity not leashed")
    }

    override fun setLeashHolder(p0: Entity?): Boolean {
        return false
    }

    override fun isGliding(): Boolean {
        return false
    }

    override fun setGliding(p0: Boolean) {
    }

    override fun isSwimming(): Boolean {
        return false
    }

    override fun setSwimming(p0: Boolean) {
    }

    override fun isRiptiding(): Boolean {
        return false
    }

    override fun isSleeping(): Boolean {
        return false
    }

    override fun isClimbing(): Boolean {
        return false
    }

    override fun setAI(p0: Boolean) {
    }

    override fun hasAI(): Boolean {
        return false
    }

    override fun attack(p0: Entity) {
    }

    override fun swingMainHand() {
    }

    override fun swingOffHand() {
    }

    override fun setCollidable(p0: Boolean) {
    }

    override fun isCollidable(): Boolean {
        return false
    }

    override fun getCollidableExemptions(): MutableSet<UUID> {
        return mutableSetOf()
    }

    override fun <T : Any?> getMemory(p0: MemoryKey<T>): T? {
        return null
    }

    override fun <T : Any?> setMemory(p0: MemoryKey<T>, p1: T?) {
    }

    override fun getCategory(): EntityCategory {
        return EntityCategory.NONE
    }

    override fun setInvisible(p0: Boolean) {
    }

    override fun isInvisible(): Boolean {
        return true
    }

}
