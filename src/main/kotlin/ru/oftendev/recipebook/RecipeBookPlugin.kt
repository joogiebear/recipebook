package com.mystipixel.recipebook

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.event.Listener
import com.mystipixel.recipebook.category.RecipeCategories
import com.mystipixel.recipebook.commands.MainCommand

lateinit var recipeBookPlugin: RecipeBookPlugin
    private set

class RecipeBookPlugin: EcoPlugin() {
    init {
        recipeBookPlugin = this
    }

    override fun handleEnable() {
        RecipeCategories.reload()
    }

    override fun handleReload() {
        RecipeCategories.reload()
    }

    override fun loadPluginCommands(): MutableList<PluginCommand> {
        return mutableListOf(
            MainCommand(this)
        )
    }
}