package com.mystipixel.recipebook.category

import com.mystipixel.recipebook.recipeBookPlugin

object RecipeCategories {
    val REGISTRY = mutableListOf<RecipeCategory>()

    fun reload() {
        REGISTRY.clear()

        REGISTRY.addAll(
            recipeBookPlugin.configYml.getSubsections("categories").map { RecipeCategory(it) }
        )
    }

    fun getById(id: String?): RecipeCategory? {
        return id?.let { REGISTRY.firstOrNull { it.id.equals(id, true) } }
    }
}