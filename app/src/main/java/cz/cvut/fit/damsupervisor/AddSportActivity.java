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
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Random;

import cz.cvut.fit.damsupervisor.controllers.AddSportActivity.addSportCtrl;

/**
 * Aktivita přidávání sportů
 */
public class AddSportActivity extends AppCompatActivity {
    private addSportCtrl sportController;
    private String currentQuery="";

    /**
     * Nastaví vše potřebné pro aktivitu
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sport);

        // Zobrazeni reklamy
        Random r = new Random();
        int showAdd=r.nextInt(2);
        if(showAdd==1&&MainActivity.mInterstitialAd2.isLoaded())
            MainActivity.mInterstitialAd2.show();

        // Tlacitko zpet
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception E) {E.printStackTrace();}

        // Nastaveni SearchView
        final SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView search = (SearchView) findViewById(R.id.searchViewSport);
        search.setQueryHint(getString(R.string.searchSport));
        search.setIconified(false);
        search.requestFocusFromTouch();
        sportController = new addSportCtrl(search,this);
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("AddFoodActivity", "Text submit");
                sportController.setSelectedID(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.d("AddFoodActivity", "Text change");
                currentQuery=query;
                sportController.selectedID=-1;
                sportController.findSportInDB(query);
                return true;
            }
        });
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Log.d("AddFoodActivity", "select " + position);
                CursorAdapter cur = search.getSuggestionsAdapter();
                Cursor c = cur.getCursor();
                search.setQuery(c.getString(1), true);
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Log.d("AddFoodActivity", "click " + position);
                CursorAdapter cur = search.getSuggestionsAdapter();
                Cursor c = cur.getCursor();
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
     * Volá se při stisknutí tlačítka, přidává sport do databáze
     * @param view
     * @return
     */
    public boolean onClickAddSportToDB(View view)
    {
        EditText name = (EditText) findViewById(R.id.editTextSportname);
        EditText dif = (EditText) findViewById(R.id.editTextSportdif);
        return sportController.addSportToDB(
                name.getText().toString(),
                dif.getText().toString()
        );
    }

    /**
     * Volá se při stisknutí tlačítka, pridává sport na denní seznam
     * @param view
     * @return
     */
    public boolean onClickAddSportToDay(View view)
    {
        if(sportController.selectedID==-1&&currentQuery.length()>0)
            sportController.setSelectedID(currentQuery);

        EditText value = (EditText) findViewById(R.id.editTextTime);

        return sportController.addSportToDay(
                value.getText().toString(),
                (TextView) findViewById(R.id.textViewAddedSport),
                (TextView) findViewById(R.id.textViewAddedSportsTotal)
        );
    }

    /**
     * Volá se při stisknutí tlačítka, odebírá poslední přidaný sport
     * @param view
     * @return
     */
    public boolean onClickDelSport(View view)
    {
        return sportController.delSport(
                (TextView) findViewById(R.id.textViewAddedSport),
                (TextView) findViewById(R.id.textViewAddedSportsTotal)
        );
    }
}
