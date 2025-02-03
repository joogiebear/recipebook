package rcom.mystipixel.recipebook.gui

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import com.mystipixel.recipebook.category.getRecipe

class RecipeGUI(val config: Config, val stack: ItemStack) {
    fun open(player: Player, parent: Menu?) {
        val items = getRecipe(stack)!!
        val pattern = config.getStrings("mask.pattern")
        val menu = Menu.builder(pattern.size)
            .setTitle(config.getFormattedString("title"))
        var row = 1
        var num = 0
        pattern.forEach {
            var col = 1
            it.toCharArray().forEach {
                    s -> kotlin.run {
                if (s.equals('i', true)) {
                    if (num < items.size) {
                        if (!items[num].type.isAir) {
                            menu.setSlot(row, col, slot(items[num]))
                        }
                    }
                    num++
                }
                if (s.equals('o', true)) {
                    menu.setSlot(row, col, slot(stack))
                }
            }
                col++
            }
            row++
        }
        menu.setMask(
            FillerMask(
                MaskItems.fromItemNames(config.getStrings("mask.items")),
                *pattern.toTypedArray()
            )
        )
        config.getSubsectionOrNull("buttons.back")?.let {
            parent?.let {
                menu.addComponent(
                    config.getInt("buttons.back.row"),
                    config.getInt("buttons.back.column"),
                    backSlot(parent)
                )
            }
        }

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

    private fun slot(item: ItemStack): Slot {
        return Slot.builder(
            ItemStackBuilder(item.clone()).build()
        ).build()
    }
}