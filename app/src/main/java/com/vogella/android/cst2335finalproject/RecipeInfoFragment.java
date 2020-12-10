package com.vogella.android.cst2335finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
 * Fragment for tablet sizes.
 */
public class RecipeInfoFragment extends Fragment {
    String imageLink;
    public BaseAdapter parentAdapter;
    public Context appcontext;
    public Bundle bundledata;
    private View fragment;

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
            ProgressBar loading = (ProgressBar) fragment.findViewById(R.id.recipeviewprogressbar);
            loading.setVisibility(View.VISIBLE);
        }

        /**
         * Called when the data is loaded. This is when image is loaded, or is null and has errors.
         * @param image Downloaded image or null in case of error.
         */
        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
            if (image != null) {
                ImageView imagePreview = (ImageView) fragment.findViewById(R.id.recipeimage);
                imagePreview.setImageBitmap(image);
            }
            ProgressBar loading = (ProgressBar) fragment.findViewById(R.id.recipeviewprogressbar);
            loading.setVisibility(View.INVISIBLE);
        }

        /**
         * Called when a progress is made.
         * @param values progress
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            ProgressBar loading = (ProgressBar) fragment.findViewById(R.id.recipeviewprogressbar);
            loading.setProgress(values[0]);
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
                File file = appcontext.getFileStreamPath(filename);
                publishProgress(35);
                if (file == null || !file.exists()) {
                    Log.i("Save", "Preview cannot be found, downloading..");
                    URL link = new URL(imageLink);
                    InputStream inputStream = link.openConnection().getInputStream();
                    publishProgress(60);
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    publishProgress(70);
                    FileOutputStream fileOutputStream = appcontext.openFileOutput(filename, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    publishProgress(80);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    Log.i("Save", "Preview download finished.");
                    return image;
                }
                Log.i("Save", "Preview is cached, loading.");
                FileInputStream fis = appcontext.openFileInput(filename);
                publishProgress(90);
                Log.i("Favourites", "Preview load finsihed.");
                return BitmapFactory.decodeStream(fis);
            } catch (Exception ignored) {
            }
            return null;
        }
    }

    /**
     * Used to create a fragment layout for tablets.
     * @param inflater Layout Inflater
     * @param container Container this fragment is inside of it.
     * @param savedInstanceState saved data
     * @return Created/Inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.activity_recipedetails, container, false);
        View navigationView = fragment.findViewById(R.id.recipenavigation);
        navigationView.setVisibility(View.GONE);
        boolean is_favourite_list = bundledata.getBoolean("FavouriteList");
        int id = bundledata.getInt("ItemID");
        View apptoolbar = fragment.findViewById(R.id.recipeapptoolbar);
        apptoolbar.setVisibility(View.GONE);
        RecipeDataHolder item;
        if (is_favourite_list) {
            item = RecipeMain.SavedRecipes.get(id);
        } else {
            item = RecipeMain.RecipeList.get(id);
        }
        TextView recipeId = fragment.findViewById(R.id.recipetitleid);
        recipeId.setText(getString(R.string.recipesearchidextra) + " " + item.ID);
        recipeId.setOnClickListener(view -> {
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(item.ID));
            startActivity(browser);
        });
        TextView recipeTitle = (TextView) fragment.findViewById(R.id.recipeviewtitle);
        recipeTitle.setText(getString(R.string.recipetitle) + ": " + item.Title);
        TextView ingredientText = (TextView) fragment.findViewById(R.id.recipeviewingredients);
        String ingredients = "";
        for (String text : item.Ingredients) {
            ingredients += text + ", ";
        }
        Switch savedButton = (Switch) fragment.findViewById(R.id.recipesavedicon);
        savedButton.setChecked(item.IsSaved);
        savedButton.setOnCheckedChangeListener((compoundButton, b) -> {
            String message;
            if (savedButton.isChecked()) {
                message = getString(R.string.recipesaveadded);
                RecipeMain.store_recipe(appcontext, item);
            }
            else {
                message = getString(R.string.recipesaveremoved);
                RecipeMain.unstore_recipe(appcontext, item);
            }
            Snackbar.make(compoundButton, item.Title + " " + message, Snackbar.LENGTH_LONG).setAction(R.string.recipeundo, view -> compoundButton.toggle()).show();
            parentAdapter.notifyDataSetChanged();
        });
        ingredients = ingredients.substring(0, ingredients.length() - 2);
        ingredientText.setText(getString(R.string.recipeingredients) + ": " + ingredients);
        Button closeButton = fragment.findViewById(R.id.recipeclosebutton);
        closeButton.setOnClickListener(view -> getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit());
        imageLink = item.Thumbnail;
        return fragment;
    }

    /**
     * Called when this fragment starts.
     */
    @Override
    public void onStart() {
        super.onStart();
        RecipeImageLoader executor = new RecipeImageLoader();
        executor.execute("run");
    }
}