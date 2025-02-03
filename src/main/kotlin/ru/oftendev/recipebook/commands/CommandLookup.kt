package com.mystipixel.recipebook.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import org.bukkit.entity.Player
import com.mystipixel.recipebook.category.getRecipe
import com.mystipixel.recipebook.gui.RecipeGUI
import com.mystipixel.recipebook.recipeBookPlugin

class CommandLookup(plugin: EcoPlugin) : Subcommand(plugin, "lookup",
    "recipebook.command.lookup", true) {
    override fun onExecute(sender: Player, args: List<String>) {
        val item = Items.lookup(args.joinToString(" "))
        if (item is EmptyTestableItem) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-item"))
            return
        }
        getRecipe(item.item) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("no-recipe"))
            return
        }
        RecipeGUI(recipeBookPlugin.configYml.getSubsection("craft-gui"), item.item).open(sender, null)
    }
}
