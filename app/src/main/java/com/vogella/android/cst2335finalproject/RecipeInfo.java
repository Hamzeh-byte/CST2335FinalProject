package com.vogella.android.cst2335finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Shows all the information about the recipe as well as image.
 */
public class RecipeInfo extends AppCompatActivity {
    private static final String _help = "To save a recipe, click on the save switch and toggle it.\n" +
            "To open recipe in your web browser, click the ID of it. (Link)";

    String imageLink;

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
     * Called on creation of Recipe Information Activity.
     * @param savedInstanceState data bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipedetails);
        RecipeMain.link_toolbar(this, _help, RecipeMain._credits, "Recipe Info");
        Bundle data = getIntent().getExtras();
        boolean isSavedListContainer = data.getBoolean("FavouriteList");
        int id = data.getInt("ItemID");
        RecipeDataHolder item;
        if (isSavedListContainer) {
            item = RecipeMain.SavedRecipes.get(id);
        } else {
            item = RecipeMain.RecipeList.get(id);
        }
        TextView recipeTextView = (TextView) findViewById(R.id.recipeviewtitle);
        recipeTextView.setText(getString(R.string.recipetitle) + ": " + item.Title);
        Switch isSavedCheckbox = (Switch) findViewById(R.id.recipesavedicon);
        isSavedCheckbox.setChecked(item.IsSaved);
        isSavedCheckbox.setOnCheckedChangeListener((compoundButton, b) -> {
            String message;
            if (isSavedCheckbox.isChecked()) {
                RecipeMain.store_recipe(this, item);
                message = getString(R.string.recipesaveadded);
            }
            else {
                RecipeMain.unstore_recipe(this, item);
                message = getString(R.string.recipesaveremoved);
            }
            Snackbar.make(compoundButton, item.Title + " " + message, Snackbar.LENGTH_LONG).setAction(R.string.recipeundo, view -> compoundButton.toggle()).show();
        });
        TextView recipe_id = findViewById(R.id.recipetitleid);
        recipe_id.setText(getString(R.string.recipesearchidextra) + " " + item.ID);
        recipe_id.setOnClickListener(view -> {
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(item.ID));
            startActivity(browser);
        });
        String textIngredients = "";
        for (String text : item.Ingredients) {
            textIngredients += text + ", ";
        }
        textIngredients = textIngredients.substring(0, textIngredients.length() - 2);
        TextView ingredientsTextView = (TextView) findViewById(R.id.recipeviewingredients);
        ingredientsTextView.setText(getString(R.string.recipeingredients) + ": " + textIngredients);
        Button buttonClose = findViewById(R.id.recipeclosebutton);
        buttonClose.setOnClickListener(view -> finish());
        imageLink = item.Thumbnail;
    }

    /**
     * Called when this activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        RecipeImageLoader backgroundExecutor = new RecipeImageLoader();
        backgroundExecutor.execute("run");
    }


    /**
     * Loads recipe preview images from the internet.
     */
    private class RecipeImageLoader extends AsyncTask<String, Integer, Bitmap> {
        /**
         * Called before this background thread is executed.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar loading = (ProgressBar) findViewById(R.id.recipeviewprogressbar);
            loading.setVisibility(View.VISIBLE);
        }

        /**
         * Called when the data is loaded. This is when image is loaded, or is null and has errors.
         * @param image Downloaded image or null in case of error.
         */
        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
            ProgressBar loading = (ProgressBar) findViewById(R.id.recipeviewprogressbar);
            loading.setVisibility(View.INVISIBLE);
            if (image == null)
                return;
            ImageView thumbnail_box = (ImageView) findViewById(R.id.recipeimage);
            thumbnail_box.setImageBitmap(image);
        }

        /**
         * Called when a progress is made.
         * @param values progress
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            ProgressBar progressbar = (ProgressBar) findViewById(R.id.recipeviewprogressbar);
            progressbar.setProgress(values[0]);
        }

        /**
         * Loads image bitmap from the internet.
         * @param action action
         * @return true
         */
        @Override
        protected Bitmap doInBackground(String... action) {
            publishProgress(0);
            try {
                String filename = URLEncoder.encode(imageLink, "UTF-8");
                publishProgress(25);
                File file = getBaseContext().getFileStreamPath(filename);
                publishProgress(35);
                if (file == null || !file.exists()) {
                    Log.i("Save", "Preview cannot be found, downloading..");
                    URL link = new URL(imageLink);
                    InputStream inputStream = link.openConnection().getInputStream();
                    publishProgress(60);
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    publishProgress(70);
                    FileOutputStream fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    publishProgress(80);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    Log.i("Save", "Preview download finished.");
                    return image;
                }
                FileInputStream fis = openFileInput(filename);
                publishProgress(90);
                Log.i("Favourites", "Image is cache.");
                return BitmapFactory.decodeStream(fis);
            } catch (Exception ignored) {
            }
            return null;
        }
    }
}