package com.mystipixel.recipebook.gui

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.player
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import com.mystipixel.recipebook.category.RecipeCategory
import com.mystipixel.recipebook.recipeBookPlugin

class ItemCategoryGUI(val config: Config, val parent: RecipeCategory): CategoryGUI {
    override fun open(player: Player, page: Int, prevMenu: Menu?) {
        val items = parent.getMemberItemsRecipes(player)
        val pattern = config.getStrings("mask.pattern")
        val menu = Menu.builder(pattern.size)
            .setTitle(config.getFormattedString("title")
                .replace("%page%", page.toString()))
        var row = 1
        var num = ((page-1)*getPerPage())
        pattern.forEach {
            var col = 1
            it.toCharArray().forEach {
                    s -> kotlin.run {
                if (s.equals('i', true)) {
                    if (num < items.size) {
                        menu.setSlot(row, col, slot(items[num]))
                    }
                    num++
                }
            }
                col++
            }
            row++
        }
        config.getSubsectionOrNull("buttons.back")?.let {
            prevMenu?.let {
                menu.addComponent(
                    config.getInt("buttons.back.row"),
                    config.getInt("buttons.back.column"),
                    backSlot(prevMenu)
                )
            }
        }
        menu.setMask(
            FillerMask(
                MaskItems.fromItemNames(config.getStrings("mask.items")),
                *pattern.toTypedArray()
            )
        )
        menu.setSlot(
            config.getInt("buttons.next-page.row"),
            config.getInt("buttons.next-page.column"),
            nextSlot(page, prevMenu)
        )
        menu.setSlot(
            config.getInt("buttons.prev-page.row"),
            config.getInt("buttons.prev-page.column"),
            prevSlot(page, prevMenu)
        )
        for (config in config.getSubsections("custom-slots")) {
            menu.setSlot(
                config.getInt("row"),
                config.getInt("column"),
                ConfigSlot(config)
            )
        }
        menu.build().open(player)
    }

    private fun backSlot(menu: Menu): Slot {
        return Slot.builder(
            ItemStackBuilder(Items.lookup(config.getString("buttons.back.item")))
                .addLoreLines(config.getFormattedStrings("buttons.back.lore"))
                .build()
        )
            .onLeftClick { t, _ ->
                menu.open(t.whoClicked as Player)
            }
            .build()
    }

    private fun getPerPage(): Int {
        return config.getStrings("mask.pattern")
            .sumOf {
                it.toCharArray().filter { it1 -> it1.equals('i', true) }.size
            }
    }

    private fun getMaxPages(): Int {
        val total = parent.getMemberItems().size
        return total/getPerPage() + if (total % getPerPage() > 0) 1 else 0
    }

    private fun nextSlot(page: Int, prevMenu: Menu?): Slot {
        val nextActive = page < getMaxPages()
        val builder = Slot.builder(
            ItemStackBuilder(
                Items.lookup(config.getString("buttons.next-page.item.${getActive(nextActive)}"))
            ).addLoreLines(
                config.getFormattedStrings("buttons.next-page.lore.${getActive(nextActive)}")
            ).build()
        )
        if (nextActive) {
            builder.onLeftClick { event, _ -> open(event.player, page + 1, prevMenu) }
        }
        return builder.build()
    }

    private fun prevSlot(page: Int, prevMenu: Menu?): Slot {
        val prevActive = page > 1
        val builder = Slot.builder(
            ItemStackBuilder(
                Items.lookup(config.getString("buttons.prev-page.item.${getActive(prevActive)}"))
            ).addLoreLines(
                config.getFormattedStrings("buttons.prev-page.lore.${getActive(prevActive)}")
            ).build()
        )
        if (prevActive) {
            builder.onLeftClick { event, _ -> open(event.player, page - 1, prevMenu) }
        }
        return builder.build()
    }

    private fun getActive(active: Boolean): String {
        return if (active) "active" else "inactive"
    }

    private fun slot(item: ItemStack): Slot {
        return Slot.builder(
            ItemStackBuilder(item.clone())
                .addLoreLines(config.getFormattedStrings("buttons.slot.lore"))
                .build()
        )
            .onLeftClick { event, _, menu ->
                RecipeGUI(recipeBookPlugin.configYml.getSubsection("craft-gui"), item)
                    .open(event.whoClicked as Player, menu)
            }
            .build()
    }
}