package cz.cvut.fit.damsupervisor;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Random;

import cz.cvut.fit.damsupervisor.controllers.AddFoodActivity.addFoodCtrl;

/**
 * Aktivita přidávání jídel
 */
public class AddFoodActivity extends AppCompatActivity {
    private addFoodCtrl foodController;
    private String currentQuery="";

    /**
     * Nastaví vše potřebné pro aktivitu
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        // Zobrazeni reklamy
        Random r = new Random();
        int showAdd=r.nextInt(2);
        if(showAdd==1&&MainActivity.mInterstitialAd1.isLoaded())
            MainActivity.mInterstitialAd1.show();

        // Tlacitko zpet
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception E) {E.printStackTrace();}

        // Nastaveni spinneru
        Spinner mealType = (Spinner) findViewById(R.id.spinnerFoodType);
        String[] mealTypes = {getString(R.string.food_breakfest), getString(R.string.food_snack1),getString(R.string.food_lunch
        ), getString(R.string.food_snack2), getString(R.string.food_dinner), getString(R.string.food_snack3)};
        ArrayAdapter<String> mTypeAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,mealTypes);
        mealType.setAdapter(mTypeAdapter);
        Spinner gramsOrPieces = (Spinner) findViewById(R.id.spinnerGramsOrPieces);
        String[] gopItems = {getString(R.string.food_grams), getString(R.string.food_pieces)};
        ArrayAdapter<String> gopTypeAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,gopItems);
        gramsOrPieces.setAdapter(gopTypeAdapter);

        // Nastaveni SearchView
        final SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView search = (SearchView) findViewById(R.id.searchViewFood);
        search.setIconified(false);
        search.requestFocusFromTouch();
        foodController = new addFoodCtrl(search,this);
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("AddFoodActivity", "Text submit");
                foodController.setSelectedID(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                Log.d("AddFoodActivity", "Text change");
                currentQuery=query;
                foodController.selectedID=-1;
                foodController.findFoodInDB(query);
                return true;
            }
        });
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Log.d("AddFoodActivity", "select " + position);
                CursorAdapter cur = search.getSuggestionsAdapter();
                Cursor c=cur.getCursor();
                search.setQuery(c.getString(1), true);
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Log.d("AddFoodActivity","click "+position);
                CursorAdapter cur = search.getSuggestionsAdapter();
                Cursor c=cur.getCursor();
                search.setQuery(c.getString(1), true);
                return true;
            }
        });
    }

    /**
     * Určuje co se má stát, při stisknutí nějakého menu tlačítka
     * @param item stisknuté tlačítko v menu
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Tlacitko zpet
        if (id==android.R.id.home) {
            finish();
        }
        return true;
    }

    /**
     *
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Volá se při stisknutí tlačítka, pridává jídlo do databáze
     * @param view
     * @return
     */
    public boolean onClickAddFoodToDB(View view)
    {
        EditText name = (EditText) findViewById(R.id.editTextFoodName);
        EditText prots = (EditText) findViewById(R.id.editTextProts);
        EditText carbs = (EditText) findViewById(R.id.editTextCarbs);
        EditText fats = (EditText) findViewById(R.id.editTextFats);
        EditText energy = (EditText) findViewById(R.id.editTextEnergy);
        EditText grams = (EditText) findViewById(R.id.editTextGramsInPiece);

        return foodController.addFoodToDB(
                name.getText().toString(),
                prots.getText().toString(),
                carbs.getText().toString(),
                fats.getText().toString(),
                energy.getText().toString(),
                grams.getText().toString()
        );
    }

    /**
     * Volá se při stisknutí tlačítka, přidává jídlo na denní seznam
     * @param view
     * @return
     */
    public boolean onClickAddFoodToDay(View view)
    {
        if(foodController.selectedID==-1&&currentQuery.length()>0)
            foodController.setSelectedID(currentQuery);

        Spinner type = (Spinner) findViewById(R.id.spinnerFoodType);
        Spinner gramOrPiece = (Spinner) findViewById(R.id.spinnerGramsOrPieces);
        EditText value = (EditText) findViewById(R.id.editTextGOP);

        return foodController.addFoodToDay(
                value.getText().toString(),
                gramOrPiece.getSelectedItemPosition(),
                type.getSelectedItemPosition(),
                (TextView) findViewById(R.id.textViewAddedFood),
                (TextView) findViewById(R.id.textViewAddedFoodsTotal)
        );
    }

    /**
     * Volá se při stisknutí tlačítka, odebírá poslední přidané jídlo
     * @param view
     * @return
     */
    public boolean onClickDelFood(View view)
    {
        return foodController.delFood(
                (TextView) findViewById(R.id.textViewAddedFood),
                (TextView) findViewById(R.id.textViewAddedFoodsTotal)
        );
    }
}
