package com.vogella.android.cst2335finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Handles the database of recipe app.
 */
public class RecipeDatabaseHandler extends SQLiteOpenHelper {
    final static int VERSION = 1;
    final static String SAVES_TABLE = "recipesaves";
    final static String SAVES_INGREDIENTS = "recipeingredients";
    public final static String COL_ID = "href";
    public final static String COL_Title = "title";
    public final static String COL_Ingredients = "ingredient";
    public final static String COL_Image = "image";
    private static RecipeDatabaseHandler recipeDatabaseHandler;
    public RecipeDatabaseHandler(Context context) {
        super(context, SAVES_TABLE, null, VERSION);
    }

    /**
     * Called when the database is created.
     * @param db Database instance.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SAVES_INGREDIENTS + " (" + COL_Title + " TEXT NOT NULL" + ");");
        db.execSQL("CREATE TABLE " + SAVES_TABLE + "(" + COL_ID + " TEXT PRIMARY KEY NOT NULL," + COL_Title + " TEXT NOT NULL," + COL_Ingredients + " TEXT," + COL_Image + " TEXT" + ");");
    }

    /**
     * Called when the database is about to be upgraded.
     * @param db Database instance
     * @param i previous version
     * @param i1 new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + SAVES_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + SAVES_INGREDIENTS + ";");
        onCreate(db);
    }

    /**
     * Called on downgrade of the database
     * @param db Database instance
     * @param oldVersion Old version
     * @param newVersion New version
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SAVES_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + SAVES_INGREDIENTS + ";");
        onCreate(db);
    }


    /**
     * Deletes a recipe from database
     * @param recipe Recipe object
     * @return number of deleted recipes.
     */
    public int deleteRecipe(RecipeDataHolder recipe) {
        SQLiteDatabase database = getWritableDatabase();
        String sql_where = COL_ID + " = ?";
        String[] args = {recipe.ID};
        return database.delete(SAVES_TABLE, sql_where, args);
    }

    /**
     * Saves a recipe in database.
     * @param recipe Recipe data object.
     */
    public void saveRecipe(RecipeDataHolder recipe) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues valuepair = new ContentValues();
        valuepair.put(COL_ID, recipe.ID);
        valuepair.put(COL_Title, recipe.Title);
        valuepair.put(COL_Image, recipe.Thumbnail);
        String ingredientText = "";
        for (String ingredient : recipe.Ingredients) {
            ingredientText += ingredient + ",";
        }
        ingredientText = ingredientText.substring(0, ingredientText.length() - 1);
        valuepair.put(COL_Ingredients, ingredientText);
        database.insert(SAVES_TABLE, null, valuepair);
    }

    /**
     * Loads recipes from database into recipe save list.
     */
    public void loadSavedRecipes() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = readableDatabase.query(SAVES_TABLE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndexID = cursor.getColumnIndex(COL_ID);
            int columnIndexIngredient = cursor.getColumnIndex(COL_Ingredients);
            int columnThumbnail = cursor.getColumnIndex(COL_Image);
            while (!cursor.isAfterLast()) {
                String ID = cursor.getString(columnIndexID);
                String Title = cursor.getString(columnIndexIngredient).trim();
                String Ingredients = cursor.getString(columnThumbnail);
                String Thumbnail = cursor.getString(columnThumbnail);
                RecipeDataHolder recipe = new RecipeDataHolder(ID, Title, Ingredients, Thumbnail);
                recipe.IsSaved = true;
                RecipeMain.SavedRecipes.add(recipe);
                cursor.moveToNext();
            }
        }
    }

    /**
     * Reloads all the ingredients; Removes the ingredients first. Then inserts all ingredients into the database.
     */
    public void refreshIngredients() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.execSQL("DELETE FROM " + SAVES_INGREDIENTS + ";");
        writableDatabase.beginTransaction();
        for (String ingredient : RecipeMain.IngredientList) {
            ContentValues valuepair = new ContentValues();
            valuepair.put(COL_Title, ingredient);
            writableDatabase.insert(SAVES_INGREDIENTS, null, valuepair);
        }
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
    }

    /**
     * Loads all ingredients from the database.
     */
    public void loadIngredients() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = readableDatabase.query(SAVES_INGREDIENTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int c_title = cursor.getColumnIndex(COL_Title);
            while (!cursor.isAfterLast()) {
                RecipeMain.IngredientList.add(cursor.getString(c_title));
                cursor.moveToNext();
            }
        }
    }

    /**
     * Gets a instance of database from the context
     * @param context application context
     * @return Database handler
     */
    public static synchronized RecipeDatabaseHandler getDatabaseObject(Context context) {
        if (recipeDatabaseHandler == null) {
            recipeDatabaseHandler = new RecipeDatabaseHandler(context.getApplicationContext());
        }
        return recipeDatabaseHandler;
    }
}
