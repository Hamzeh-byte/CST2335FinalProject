package com.vogella.android.cst2335finalproject;

import java.util.LinkedList;
import java.util.Objects;

/**
 * Data holder for recipe, which is used by activities and layouts to display a recipe object.
 */
public class RecipeDataHolder {
    public String ID;
    public String Title;
    public String Thumbnail;
    public boolean IsSaved;

    public LinkedList<String> Ingredients;

    /**
     * Creates a new Recipe Data Holder.
     * @param id URL or ID of the recipe.
     * @param title Title of recipe.
     * @param ingredients Ingredients in Recipe, divided by comma (',').
     * @param thumbnail Image preview of recipe (as a link, string).
     */
    public RecipeDataHolder(String id, String title, String ingredients, String thumbnail) {
        setID(id);
        setTitle(title);
        setIngredients(new LinkedList<>());
        setSaved(false);
        setThumbnail(thumbnail);
        for (String ingredient : ingredients.split(",")) {
            if (ingredient.length() > 1)
                getIngredients().add(ingredient.trim().substring(0, 1).toUpperCase() + ingredient.trim().substring(1));
            else
                getIngredients().add(ingredient.toUpperCase());
        }
    }

    /**
     * Returns the ID of recipe.
     * @return ID of recipe
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets the ID of recipe.
     * @param ID ID to set
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Returns the title of the recipe.
     * @return Title of recipe.
     */
    public String getTitle() {
        return Title;
    }

    /**
     * Sets the title of the recipe.
     * @param title Title to set
     */
    public void setTitle(String title) {
        Title = title;
    }

    /**
     * Gets the thumbnail preview of recipe.
     * @return Thumbnail link
     */
    public String getThumbnail() {
        return Thumbnail;
    }

    /**
     * Sets the thumbnail image link of recipe
     * @param thumbnail Link to set as thumbnail.
     */
    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    /**
     * Returns whether or not this recipe is saved.
     * @return Whether recipe is saved or not.
     */
    public boolean isSaved() {
        return IsSaved;
    }

    /**
     * Sets a recipe save state.
     * @param saved Whether recipe is saved in database or not.
     */
    public void setSaved(boolean saved) {
        IsSaved = saved;
    }

    /**
     * Ingredients list of the recipe in a linked list.
     * @return Ingredients the recipe contains.
     */
    public LinkedList<String> getIngredients() {
        return Ingredients;
    }

    /**
     * Sets the ingredients lit of recipe
     * @param ingredients Ingredient list.
     */
    public void setIngredients(LinkedList<String> ingredients) {
        Ingredients = ingredients;
    }

    /**
     * Checks if two recipes are equal or not, by comparing their ID.
     * @param o Other recipe data handler object
     * @return equality result.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeDataHolder that = (RecipeDataHolder) o;
        return ID.equals(that.ID);
    }

    /**
     * For serialization.
     * @return ID hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
