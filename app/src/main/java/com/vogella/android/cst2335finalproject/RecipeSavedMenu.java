package com.vogella.android.cst2335finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Saved menu of recipe.
 */
public class RecipeSavedMenu extends AppCompatActivity {
    private static final String _help = "To view an item in details, simply tap on it. To view the database info, long click on the item.";

    /**
     * Called for creating menu design.
     * @param menu Menu to display
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe_tools, menu);
        return true;
    }

    /**
     * Called when a menu item is clicked (Navigation).
     * @param item Navigation item menu.
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return RecipeMain.toolbar_click(this, item, _help, RecipeMain._credits);
    }

    RecipeAdapter recipeAdapter;

    /**
     * Called when activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        recipeAdapter.notifyDataSetChanged();
    }

    /**
     * Adapter that displays recipes in list view.
     */
    class RecipeAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;

        /**
         * Constructor for adapter
         */
        public RecipeAdapter() {
            super();
            layoutInflater = LayoutInflater.from(RecipeSavedMenu.this);
        }

        /**
         * Returns item count of actual list
         * @return size of the list
         */
        @Override
        public int getCount() {
            return RecipeMain.SavedRecipes.size();
        }

        /**
         * Gets item by index.
         * @param i Index
         * @return Recipe Data object
         */
        @Override
        public Object getItem(int i) {
            return RecipeMain.SavedRecipes.get(i);
        }

        /**
         * Returns item ID by index of it on view.
         * @param i Index
         * @return i
         */
        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * Called to create inflated view of adapter.
         * @param i Index of item.
         * @param view View
         * @param viewGroup ViewGroup
         * @return Created adapter item
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            RecipeDataHolder recipeDataHolder = (RecipeDataHolder) getItem(i);
            View view1 = layoutInflater.inflate(R.layout.activity_recipeitemlayout, null);
            TextView itemtitle = (TextView) view1.findViewById(R.id.recipeitemtitle);
            itemtitle.setText(recipeDataHolder.Title);
            return view1;
        }
    }

    /**
     * Called on creation of Saved Activity.
     * @param savedInstanceState data bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipesaves);
        RecipeMain.link_toolbar(this, _help, RecipeMain._credits, "Saved Recipes");
        recipeAdapter = new RecipeAdapter();
        ListView listview = (ListView) findViewById(R.id.recipe_favourites_list);
        listview.setOnItemLongClickListener((adapterView, view, i, l) -> {
            RecipeDataHolder recipeDataHolder = RecipeMain.SavedRecipes.get(i);
            new AlertDialog.Builder(this).setTitle(R.string.recipedatabasetitle)
                    .setMessage(
                            getString(R.string.recipesearchidextra) + " " + recipeDataHolder.ID + "\n" +
                                    "Database Index: " + i + "\n" + getString(R.string.recipeimagepreview) + ": " + recipeDataHolder.Thumbnail + "\n" +
                                    getString(R.string.recipesaved) + ": " + (recipeDataHolder.IsSaved ? getString(R.string.recipeyes) : getString(R.string.recipeno)) + "\n" +
                                    getString(R.string.recipeingredients) + ": " + recipeDataHolder.Ingredients + "\n" +
                                    getString(R.string.recipetitle) + ": " + recipeDataHolder.Title + "\n"
                    )
                    .setNeutralButton(R.string.recipeclose, (dialogInterface, i1) -> dialogInterface.dismiss())
                    .show();
            return true;
        });
        listview.setAdapter(recipeAdapter);
        listview.setOnItemClickListener((adapterView, view, i, l) -> {
            Bundle data = new Bundle();
            data.putInt("ItemID", i);
            data.putBoolean("FavouriteList", true);
            FrameLayout framelayout = (FrameLayout) findViewById(R.id.recipe_favourites_framelayout);
            if (framelayout != null) {
                RecipeInfoFragment fgmt = new RecipeInfoFragment();
                fgmt.parentAdapter = recipeAdapter;
                fgmt.appcontext = getBaseContext();
                fgmt.bundledata = data;
                getSupportFragmentManager().beginTransaction().replace(R.id.recipe_favourites_framelayout, fgmt).commit();
            } else {
                Intent intent = new Intent(this, RecipeInfo.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }
}