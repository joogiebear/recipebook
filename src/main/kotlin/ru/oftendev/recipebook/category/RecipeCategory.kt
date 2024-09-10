package ru.oftendev.recipebook.category

import com.willfp.eco.core.config.config
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.util.containsIgnoreCase
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.ShapedRecipe
import ru.oftendev.recipebook.gui.CategoryCategoryGUI
import ru.oftendev.recipebook.gui.ItemCategoryGUI

class RecipeCategory(val config: Config) {
    val id = config.getString("id")
    val type = config.getString("type")

    val icon = config.getSubsectionOrNull("icon")?.let { CategoryIcon(it) }

    val namespaces = config.getStrings("namespaces")

    val items = config.getStrings("items").map { Items.lookup(it) }

    val categories = config.getStrings("categories")

    val parsedCategories: List<RecipeCategory>
        get() = categories.mapNotNull { RecipeCategories.getById(it) }

    val gui = if (type == "items") ItemCategoryGUI(config.getSubsection("gui"), this)
        else CategoryCategoryGUI(config.getSubsection("gui"), this)

    fun getMemberItems(): List<ItemStack> {
        return when(type) {
            "items" -> {
                items.map { it.item } + Items.getCustomItems()
                    .filter { namespaces.containsIgnoreCase(it.key.namespace) }.map { it.item }
            }
            else -> {
                categories.mapNotNull { RecipeCategories.getById(it)?.icon?.getItemStack() }
            }
        }
    }

    fun getMemberItemsRecipes(player: Player): List<ItemStack> {
        return getMemberItems().filter { canCraft(player, it) }
    }
}

fun canCraft(player: Player, itemStack: ItemStack): Boolean {
    return Items.getCustomItem(itemStack)?.let { player.hasPermission(Recipes.getRecipe(it.key)?.permission
        ?: return true) } ?: true
}
fun getRecipe(itemStack: ItemStack): List<ItemStack>? {
    return Items.getCustomItem(itemStack)?.let { customItem -> Recipes.getRecipe(customItem.key)
        ?.let { recipe -> recipe.parts.map { if (it is EmptyTestableItem) ItemStack(Material.AIR) else it.item }
            } } ?: run {
        Bukkit.getRecipesFor(itemStack)
            .filterIsInstance<ShapedRecipe>()
            .firstOrNull()?.let {
                val result = mutableListOf<ItemStack>()
                for (s in it.shape) {
                    for (c in s.toCharArray()) {
                        result += it.choiceMap[c]!!.displayIcon
                    }
                }
                result
        }
    }
}

val RecipeChoice.displayIcon
    get() = when(this) {
        is ExactChoice -> this.choices.firstOrNull() ?: ItemStack(Material.AIR)
        is MaterialChoice -> this.choices.firstOrNull()?.let { ItemStack(it) } ?: ItemStack(Material.AIR)
        else -> this.itemStack
    }

class CategoryIcon(private val config: Config) {
    fun getItemStack(): ItemStack {
        return ItemStackBuilder(
            Items.lookup(config.getString("item"))
        )
            .addLoreLines(config.getStrings("lore"))
            .build()
    }
}