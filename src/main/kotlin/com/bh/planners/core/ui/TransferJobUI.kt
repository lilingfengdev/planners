package com.bh.planners.core.ui

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.consumeTo
import com.bh.planners.api.getRoute
import com.bh.planners.api.transfer
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.Condition
import com.bh.planners.core.pojo.Router
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack
import taboolib.module.chat.colored
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.asLangText
import taboolib.platform.util.buildItem
import taboolib.platform.util.sendLang

class TransferJobUI(viewer: Player) : IUI(viewer) {

    companion object {

        val root: ConfigurationSection
            get() = UI.config.getConfigurationSection("transfer")!!

        val title: String
            get() = root.getString("title")!!

        val rows: Int
            get() = root.getInt("rows", 6)

        val slots: List<Int>
            get() = root.getIntegerList("job-slots")

        val jobIcon: ItemStack
            get() = root.getItemStack("job-icon")!!

        val jobIconLore: List<String>
            get() = root.getStringList("job-icon.lore")

        val jobCondition: List<String>
            get() = root.getStringList("job-icon.condition")
    }

    fun getRoute() = profile.getRoute()

    override fun open() {
        val route = getRoute()
        if (route == null) {
            viewer.sendLang("player-job-cannot-transfer")
            return
        }

        viewer.openMenu<Linked<Router.TransferJob>>(title) {
            rows(rows)
            elements { route.transferJobs }
            slots(TransferJobUI.slots)
            onGenerate { _, element, _, _ -> toItem(element) }
            root.getKeys(false).filter { it.startsWith("icon-") }.forEach {
                val itemStack = buildItem(root.getItemStack(it)!!) {
                    flags += ItemFlag.values()
                }
                root.getIntegerList("${it}.slots").forEach { slot ->
                    set(slot, itemStack)
                }
            }

            onClick { event, element ->
                event.isCancelled = true
                val notThroughConditions = check(element)
                if (notThroughConditions.isNotEmpty()) {
                    notThroughConditions.filter { it.placeholder != null }.forEach {
                        val langText = viewer.asLangText("player-condition-send", toPlaceholder(it.placeholder!!))
                        viewer.sendMessage(langText)
                    }
                    return@onClick
                }
                element.conditions.forEach { it.consumeTo(viewer) }
                if (profile.transfer(element.job)) {
                    viewer.sendLang("player-transfer-successful", element.job.option.name)
                    viewer.closeInventory()
                } else {
                    viewer.sendLang("player-transfer-failed")
                }
            }

        }
    }

    val Router.TransferJob.transferIcon: ItemStack?
        get() = this.root.getItemStack("transfer-icon")

    fun toItem(job: Router.TransferJob): ItemStack {
        return buildItem(job.transferIcon ?: jobIcon) {
            name = toLabel(name!!, job)
            lore.clear()
            jobIconLore.forEach {
                if (it.contains("{condition}")) {
                    lore += toConditionText(job).map { toLabel(it, job) }
                } else {
                    lore += toLabel(it, job)
                }
            }
        }
    }

    fun check(job: Router.TransferJob): List<Condition> {
        return job.conditions.filter {
            !PlannersAPI.checkCondition(viewer, it)
        }
    }

    fun toConditionText(job: Router.TransferJob): List<String> {
        val list = check(job)
        return if (list.isNotEmpty() && jobCondition.isNotEmpty()) {
            jobCondition.flatMap { s ->
                if (s.contains("\$entry")) {
                    list.filter { it.placeholder != null }.map { s.replace("\$entry", toPlaceholder(it.placeholder!!)) }
                } else {
                    listOf(s)
                }
            }
        } else emptyList()
    }

    fun toPlaceholder(string: String): String {
        return try {
            KetherFunction.parse(string, sender = adaptPlayer(viewer), namespace = namespaces)
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
            "&cError $string"
        }.colored()
    }

    fun toLabel(string: String, job: Router.TransferJob): String {
        return string.replace("{name}", job.job.option.name).colored()
    }

}
