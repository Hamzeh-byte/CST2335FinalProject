package com.vogella.android.cst2335finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Recipe Ingredients Tab.
 */
public class RecipeIngredientsTab extends AppCompatActivity {
    private ListView listIngredients;
    private Button addButton;
    private EditText editText;
    private static final String _help = "Add an ingredient by clicking it the add button.\n" +
            "Hold on item to see database id.\nClick an item to remove it, or click remove button, then confirm the dialog.";
    IngredientsAdapterMenu ingredientsAdapterMenu;

    /**
     * Ingredients adapter that populates the ingredients list view.
     */
    class IngredientsAdapterMenu extends BaseAdapter {
        private final List<String> itemlist;
        private final LayoutInflater layoutInflater;
        /**
         * Constructor for adapter
         */
        public IngredientsAdapterMenu() {
            super();
            itemlist = RecipeMain.IngredientList;
            layoutInflater = LayoutInflater.from(RecipeIngredientsTab.this);
        }

        /**
         * Returns item count of actual list
         * @return size of the list
         */
        @Override
        public int getCount() {
            return itemlist.size();
        }

        /**
         * Gets item by index.
         * @param i Index
         * @return Recipe Data object
         */
        @Override
        public Object getItem(int i) {
            return itemlist.get(i);
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
            String ingredientitem = (String) getItem(i);
            View view1 = layoutInflater.inflate(R.layout.activity_recipeingredient_layout, null);
            TextView ingredientText = (TextView) view1.findViewById(R.id.recipeingredientitemtitle);
            ingredientText.setText(ingredientitem);
            Button removeButton = view1.findViewById(R.id.recipeingredientitemremove);
            removeButton.setOnClickListener((vs) -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(RecipeIngredientsTab.this);
                alert.setTitle(R.string.recipedeletetitle);
                alert.setMessage(getString(R.string.recipedeleteconfirm) + " \"" + RecipeMain.IngredientList.get(i) + "\"?");
                alert.setPositiveButton(R.string.recipeyes, (dialogInterface, i1) -> {
                    String title = RecipeMain.IngredientList.get(i);
                    RecipeMain.unstore_ingredient(RecipeIngredientsTab.this, i);
                    ingredientsAdapterMenu.notifyDataSetChanged();
                    Toast.makeText(RecipeIngredientsTab.this, (getString(R.string.reciperemoved) + " " + title + "."), Toast.LENGTH_SHORT).show();
                });
                alert.setNegativeButton(R.string.recipeno, (dialogInterface, i1) -> dialogInterface.dismiss());
                alert.create();
                alert.show();
            });
            return view1;
        }
    }

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


    /**
     * Called on creation of Ingredients tab.
     * @param savedInstanceState data bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipeingredientstab);
        RecipeMain.link_toolbar(this, _help, RecipeMain._credits, "Recipe Ingredients Tab");
        ingredientsAdapterMenu = new IngredientsAdapterMenu();
        listIngredients = findViewById(R.id.recipeingredientlist);
        editText = findViewById(R.id.recipeingredientbox);
        addButton = findViewById(R.id.recipeingredientadd);
        listIngredients.setAdapter(ingredientsAdapterMenu);
        addButton.setOnClickListener((View v) -> {
            String ingredientText = editText.getText().toString();
            if (!ingredientText.isEmpty()) {
                RecipeMain.store_ingredient(this, ingredientText);
                editText.setText("");
                Toast.makeText(this, getString(R.string.recipeingredientadded) + " \"" + ingredientText + "\".", Toast.LENGTH_SHORT).show();
                ingredientsAdapterMenu.notifyDataSetChanged();
            }
        });
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        listIngredients.setOnItemLongClickListener((adapterView, view, i, l) -> {
            alert.setTitle(R.string.recipedeletetitle);
            alert.setMessage(getString(R.string.recipedeleteconfirm) + " \"" + RecipeMain.IngredientList.get(i) + "\"?");
            alert.setPositiveButton(R.string.recipeyes, (dialogInterface, i1) -> {
                String title = RecipeMain.IngredientList.get(i);
                RecipeMain.unstore_ingredient(this, i);
                ingredientsAdapterMenu.notifyDataSetChanged();
                Toast.makeText(this, (CharSequence) (getString(R.string.reciperemoved) + " " + title + "."), Toast.LENGTH_SHORT).show();
            });
            alert.setNegativeButton(R.string.recipeno, (dialogInterface, i1) -> dialogInterface.dismiss());
            alert.create();
            alert.show();
            return true;
        });
        listIngredients.setOnItemClickListener((adapterView, view, i, l) -> {
            alert.setTitle(RecipeMain.IngredientList.get(i));
            alert.setNeutralButton(R.string.recipeclose, (dialogInterface, i1) -> dialogInterface.dismiss());
            alert.create();
            alert.show();
        });
        editText.setText(getSharedPreferences(RecipeMain.preference_file, MODE_PRIVATE).getString("save_ingredients", ""));
    }

    /**
     * Called when activity gets paused or move to another activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        getSharedPreferences(RecipeMain.preference_file, MODE_PRIVATE).edit().putString("save_ingredients", editText.getText().toString()).apply();
    }
}