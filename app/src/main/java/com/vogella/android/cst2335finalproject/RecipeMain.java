package com.vogella.android.cst2335finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe Main, the main list of recipe which loads all the recipes from the API.
 */
public class RecipeMain extends AppCompatActivity {
    public static List<String> IngredientList;
    public static List<RecipeDataHolder> RecipeList;
    public static String preference_file = "recipe_save";
    public static List<RecipeDataHolder> SavedRecipes;
    public static final String _credits = "Recipe app by Zannatul\nStudent Number: 0409777786";
    private static final String _help = "Before you start searching, add some ingredients.\n" +
            "To do so, click the toolbar and click on Ingredients tab.\n" +
            "Once that is done, come to this menu and search for a recipe, then press the search button!\n" +
            "To see a recipe in more details, simply tap on it. To see the database information, long click it.";
    private static final String _title = "Recipe App - Zannatul";
    RecipeAdapter listadapter;
    ProgressBar progressbar;
    RecipeInfoFragment fragment_old;

    /**
     * Saves a recipe in database.
     * @param context Application context
     * @param recipe recipe object
     */
    public static void store_recipe(Context context, RecipeDataHolder recipe) {
        RecipeDatabaseHandler.getDatabaseObject(context).saveRecipe(recipe);
        recipe.IsSaved = true;
        SavedRecipes.add(recipe);
    }

    /**
     * Unstores (deletes) a recipe from database.
     * @param context Application
     * @param recipe Recipe data object.
     */
    public static void unstore_recipe(Context context, RecipeDataHolder recipe) {
        RecipeDatabaseHandler.getDatabaseObject(context).deleteRecipe(recipe);
        recipe.IsSaved = false;
        SavedRecipes.remove(recipe);
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
        return toolbar_click(this, item, _help, _credits);
    }


    /**
     * Sets support toolbar, title and also binds the lambda functions.
     * @param activity Application
     * @param help Menu help
     * @param developer Developer of app
     * @param title Title of the toolbar
     */
    public static void link_toolbar(AppCompatActivity activity, String help, String developer, String title) {
        Toolbar toolbartop = (Toolbar) activity.findViewById(R.id.toolbar_top);
        activity.setSupportActionBar(toolbartop);
        activity.getSupportActionBar().setTitle(title);
        RelativeLayout toolbarLayout = (RelativeLayout) activity.findViewById(R.id.recipeapptoolbar);
        Toolbar toolbar = toolbarLayout.findViewById(R.id.toolbar_top);
        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.recipedrawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.recipeopen, R.string.recipeclose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.recipenavigation);
        navigationView.setNavigationItemSelectedListener(item -> toolbar_click(activity, item, help, developer));
    }


    /**
     * Called on creation of Main Activity.
     * @param savedInstanceState data bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipemain);
        RecipeMain.link_toolbar(this, _help, _credits, _title);
        SavedRecipes = new ArrayList<>();
        listadapter = new RecipeAdapter();
        RecipeList = new ArrayList<>();
        IngredientList = new ArrayList<>();
        RecipeDatabaseHandler recipeDatabaseHandler = RecipeDatabaseHandler.getDatabaseObject(this);
        recipeDatabaseHandler.loadSavedRecipes();
        recipeDatabaseHandler.loadIngredients();
        View favouriteButton = findViewById(R.id.recipesavesbutton);
        favouriteButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, RecipeSavedMenu.class);
            startActivity(intent);
        });
        EditText recipeSearchtextbox = findViewById(R.id.recipesearchtextbox);
        recipeSearchtextbox.setText(getSharedPreferences(preference_file, MODE_PRIVATE).getString("save_search", ""));
        ListView listView = (ListView) findViewById(R.id.recipemainlist);
        listView.setAdapter(listadapter);
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            RecipeDataHolder recipeDataHolder = RecipeList.get(i);
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
        progressbar = findViewById(R.id.recipemainprogress);
        ImageButton searchButton = findViewById(R.id.recipesearchbutton);
        searchButton.setOnClickListener((View v) -> {
            String text = recipeSearchtextbox.getText().toString();
            if (!text.isEmpty()) {
                getSharedPreferences(preference_file, MODE_PRIVATE).edit().putString("save_search", recipeSearchtextbox.getText().toString()).apply();
                RecipeList.clear();
                listadapter.notifyDataSetChanged();
                RecipeItemLoader loader = new RecipeItemLoader();
                loader.execute("new");
            }
        });
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Bundle data = new Bundle();
            data.putInt("ItemID", i);
            data.putBoolean("FavouriteList", false);
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.recipefragmentlayout);
            if (frameLayout != null) {
                RecipeInfoFragment recipeInfoFragment = new RecipeInfoFragment();
                recipeInfoFragment.parentAdapter = listadapter;
                recipeInfoFragment.bundledata = data;
                fragment_old = recipeInfoFragment;
                recipeInfoFragment.appcontext = getBaseContext();
                FragmentManager supportFragmentManager = getSupportFragmentManager();
                supportFragmentManager.beginTransaction().replace(R.id.recipefragmentlayout, recipeInfoFragment).commit();
            } else {
                Intent intent = new Intent(this, RecipeInfo.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }


    /**
     * Saves ingredient into database.
     * @param context Application context
     * @param ingredient Ingredient to add
     */
    public static void store_ingredient(Context context, String ingredient) {
        IngredientList.add(ingredient);
        refresh_ingredients(context);
    }

    /**
     * Removes an ingredient from the database.
     * @param context application context.
     * @param index index of item in list.
     */
    public static void unstore_ingredient(Context context, int index) {
        IngredientList.remove(index);
        refresh_ingredients(context);
    }

    /**
     * Reloads all the ingredients and puts them in the database from the list.
     * @param context Application.
     */
    private static void refresh_ingredients(Context context) {
        RecipeDatabaseHandler.getDatabaseObject(context).refreshIngredients();
    }


    /**
     * Called when toolbar and navbar are clicked
     * @param context Application
     * @param menu Menu item that was clicked
     * @param help the UI help
     * @param developer Developer
     * @return true
     */
    public static boolean toolbar_click(AppCompatActivity context, MenuItem menu, String help, String developer) {
        AlertDialog.Builder alertbuilder = new AlertDialog.Builder(context);
        switch (menu.getItemId()) {
            case R.id.recipetools_ingredients:
                Intent ingredients = new Intent(context, RecipeIngredientsTab.class);
                context.startActivity(ingredients);
                break;
            case R.id.recipenav_audioapp:
            case R.id.recipetools_audioapp:
                /*Intent i1 = new Intent(context, AudioActivity.class);
                context.startActivity(i1);*/
                break;
            case R.id.recipenav_recipeapp:
            case R.id.recipetools_recipeapp:
                Intent i3 = new Intent(context, RecipeMain.class);
                context.startActivity(i3);
                break;
            case R.id.recipenav_ticketapp:
            case R.id.recipetools_ticketapp:
                /*Intent i4 = new Intent(context, TicketActivity.class);
                context.startActivity(i4);*/
                break;
            case R.id.recipenav_covidapp:
            case R.id.recipetools_covidapp:
                Intent i2 = new Intent(context, CovidMain.class);
                context.startActivity(i2);
                break;
            case R.id.recipenav_help:
                alertbuilder.setTitle(R.string.recipehelp).setMessage(help).setNeutralButton(R.string.recipeclose, (dialogInterface, i) -> dialogInterface.dismiss()).show();
                break;
            case R.id.recipenav_credit:
                alertbuilder.setTitle(R.string.recipedeveloper).setMessage(developer).setNeutralButton(R.string.recipeclose, (dialogInterface, i) -> dialogInterface.dismiss()).show();
                break;
            case R.id.recipenav_exit:
            default:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                context.startActivity(intent);
                System.exit(0);
                break;
        }
        return true;
    }


    /**
     * Called when activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        RecipeItemLoader taskloader = new RecipeItemLoader();
        taskloader.execute("old");
    }

    /**
     * Called when activity gets paused or move to another activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        EditText searchTextbox = findViewById(R.id.recipesearchtextbox);
        getSharedPreferences(preference_file, MODE_PRIVATE).edit().putString("save_search", searchTextbox.getText().toString()).apply();
    }

    /**
     * Adapter that displays recipes in list view.
     */
    class RecipeAdapter extends BaseAdapter {
        private final LayoutInflater layoutInflater;

        /**
         * Constructor for adapter
         */
        public RecipeAdapter() {
            super();
            layoutInflater = LayoutInflater.from(RecipeMain.this);
        }

        /**
         * Returns item count of actual list
         * @return size of the list
         */
        @Override
        public int getCount() {
            return RecipeList.size();
        }

        /**
         * Gets item by index.
         * @param i Index
         * @return Recipe Data object
         */
        @Override
        public Object getItem(int i) {
            return RecipeList.get(i);
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
            TextView title = (TextView) view1.findViewById(R.id.recipeitemtitle);
            title.setText(recipeDataHolder.Title);
            return view1;
        }
    }

    /**
     * Loads recipes from the API.
     */
    private class RecipeItemLoader extends AsyncTask<String, Integer, Boolean> {
        /**
         * Called before this background thread is executed.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (fragment_old != null)
                getSupportFragmentManager().beginTransaction().remove(fragment_old).commit();
            RecipeList.clear();
            progressbar.setVisibility(View.VISIBLE);
        }

        /**
         * Called when a progress is made.
         * @param progress progress
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressbar.setProgress(progress[0]);
        }

        /**
         * Called when the data is loaded.
         * @param result result
         */
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            listadapter.notifyDataSetChanged();
            progressbar.setVisibility(View.INVISIBLE);
        }

        /**
         * Loads data from api, or runs cached json.
         * @param objective new to fetch new data, and use "old" to receive the saved preference data.
         * @return true
         */
        @Override
        protected Boolean doInBackground(String... objective) {
            String apiResult = "";
            String taskLoader = objective[0];
            publishProgress(20);
            if (!taskLoader.equalsIgnoreCase("old")) {
                try {
                    String ingredientsInfo = "";
                    for (String val : IngredientList) {
                        ingredientsInfo += val + ",";
                    }
                    ingredientsInfo = ingredientsInfo.substring(0, ingredientsInfo.length() - 1);
                    ingredientsInfo = URLEncoder.encode(ingredientsInfo,"UTF-8").toLowerCase();
                    EditText seachTextbox = (EditText) RecipeMain.this.findViewById(R.id.recipesearchtextbox);
                    String searchText = URLEncoder.encode(seachTextbox.getText().toString().toLowerCase(), "UTF-8");
                    if (ingredientsInfo.length() >= 0)
                        ingredientsInfo = "i=" + ingredientsInfo;
                    URL link = new URL("http://www.recipepuppy.com/api/?" + ingredientsInfo + "&q=" + searchText + "&format=json");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(link.openStream()));
                    publishProgress(70);
                    String loadline;
                    while((loadline = bufferedReader.readLine()) != null)
                        apiResult += loadline + "\n";
                } catch (Exception e) { }
            } else{
                apiResult = getSharedPreferences(preference_file, MODE_PRIVATE).getString("save_result", "");
            }

            try {
                JSONObject recipeObject = new JSONObject(apiResult);
                publishProgress(90);
                JSONArray data = recipeObject.getJSONArray("results");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject results = data.getJSONObject(i);
                    String title = results.getString("title").trim();
                    String ingredients = results.getString("ingredients");
                    String url = results.getString("href");
                    String thumbnail = results.getString("thumbnail");
                    RecipeDataHolder obj = new RecipeDataHolder(url, title, ingredients, thumbnail);
                    if (SavedRecipes.contains(obj)) {
                        obj.IsSaved = true;
                    }
                    RecipeList.add(obj);
                }
                publishProgress(100);
            } catch (JSONException exception) { }
            getSharedPreferences(preference_file, MODE_PRIVATE).edit().putString("save_result", apiResult).apply();
            return true;
        }
    }
}