package ru.oftendev.recipebook

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.event.Listener
import ru.oftendev.recipebook.category.RecipeCategories
import ru.oftendev.recipebook.commands.MainCommand

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