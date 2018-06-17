package cz.cvut.fit.damsupervisor.controllers.AddSportActivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cz.cvut.fit.damsupervisor.R;
import cz.cvut.fit.damsupervisor.controllers.MainActivity.sportCtrl;
import cz.cvut.fit.damsupervisor.controllers.dateCtrl;
import cz.cvut.fit.damsupervisor.database.localdbService;

/**
 * Kontroller pro aktivitu AddSportActivity
 */
public class addSportCtrl {
    private SearchView search;
    private Context context;
    private localdbService dbController;
    private double totalEnergy=0;
    public int selectedID;
    private int ADDED= Color.parseColor("#00AA00");
    private int NOT_ADDED= Color.parseColor("#AA0000");
    ArrayList<sportLogger> sportLog=new ArrayList<>();

    /**
     * Konstruktor, nastaví vše potřebné
     * @param search
     * @param context
     */
    public addSportCtrl(SearchView search, Context context) {
        this.search = search;
        this.context = context;
        dbController=new localdbService(context);
        selectedID=-1;
    }

    /**
     * Hledání sportu v databázi
     * @param query současný vyhledávaný text
     */
    public void findSportInDB(String query) {
        if(query.length()>=3) {
            // Cursor
            Cursor cursor = dbController.searchSport(query);

            // SearchView
            search.setSuggestionsAdapter(new searchSportAdapter(context, cursor));
        }
        selectedID=-1;
    }

    /**
     * Nastaví ID vyhledávaného sportu podle jeho názvu
     * @param text název sportu
     * @return vrátí jestli byla operace úspěšná
     */
    public boolean setSelectedID(String text)
    {
        int id = dbController.getIdSportByString(text);
        if(id>=0)
        {
            selectedID=id;
            Toast.makeText(context, R.string.selected, Toast.LENGTH_SHORT).show();
            return true;
        }
        else
        {
            selectedID=-1;
            Toast.makeText(context, R.string.notfound, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Volá se přo stisknutí tlačítka, přidá sport do databáze
     * @param name název sportu
     * @param dif obtížnost sportu
     * @return vrátí jestli proběhlo úspěšně
     */
    public boolean addSportToDB(String name, String dif)
    {
        int difInt=0;
        try {
            difInt=Integer.parseInt(dif);
        } catch (NumberFormatException e) {
            Toast.makeText(context, R.string.sport_wrongDiff, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(difInt>10 || difInt < 0)
        {
            Toast.makeText(context, R.string.sport_wrongDiff, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(name.length()<3)
        {
            Toast.makeText(context, R.string.nameTooShort, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!dbController.insertSport(name,difInt))
        {
            Toast.makeText(context, R.string.sport_addToDBNameConflict, Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(context, R.string.addedtoDB, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Volá se při stisknutí tlačítka - přidá sport na dení seznam
     * @param time čas sportu
     * @return vrátí, jestli proběhlo úspěšně
     */
    public boolean addSportToDay(String time, TextView addedSport, TextView addedSportsTotal)
    {
        int timeInt=0;
        if(selectedID<0)
        {
            Toast.makeText(context, R.string.sport_addToDayNotSelected, Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            timeInt=Integer.parseInt(time);
        } catch (NumberFormatException e) {
            Toast.makeText(context, R.string.sport_addToDayNoTime, Toast.LENGTH_LONG).show();
            return false;
        }
        // Informace o uspesnem zaznamenani
        Cursor SportInfo = dbController.getSportInfo(selectedID);
        String lastString=addedSport.getText().toString();
        addedSport.setText(context.getString(R.string.sport_food_added)+" "+timeInt+"min "+SportInfo.getString(dbController.IDCOLUMN_SPORTS_SPORTNAME));
        double thisEnergy=timeInt*Integer.parseInt(SportInfo.getString(dbController.IDCOLUMN_SPORTS_SPORTDIFF))*
                sportCtrl.ENERGY_PER_DIFFICULT_KCALMIN*dbController.getUserWeight();
        totalEnergy+=thisEnergy;
        addedSportsTotal.setText(context.getString(R.string.sport_food_total)+" "+(int)totalEnergy+context.getString(R.string.kcal));
        addedSportsTotal.setTextColor(ADDED);
        addedSport.setTextColor(ADDED);
        Toast.makeText(context, R.string.addedtoDay, Toast.LENGTH_SHORT).show();
        // Vlozeni do db
        long id=dbController.insertDay(dateCtrl.getDateInt(), -1, selectedID, timeInt, -1, -1);
        if(id>(-1))
            sportLog.add(new sportLogger(id,thisEnergy,lastString));
        return true;
    }

    /**
     * Odstrani posledni sport
     * @param addedSport
     * @param addedSportsTotal
     * @return
     */
    public boolean delSport(TextView addedSport, TextView addedSportsTotal)
    {
        if(sportLog.size()<1)
            return false;
        if(sportLog.size()==1)
        {
            addedSportsTotal.setTextColor(NOT_ADDED);
            addedSport.setTextColor(NOT_ADDED);
        }
        Toast.makeText(context, R.string.deletedfromDay, Toast.LENGTH_SHORT).show();
        totalEnergy-=sportLog.get(sportLog.size()-1).totalEnergy;
        addedSportsTotal.setText(context.getString(R.string.sport_food_total)+" "+(int)totalEnergy+context.getString(R.string.kcal));
        addedSport.setText(sportLog.get(sportLog.size() - 1).lastString);
        dbController.deleteDayID(sportLog.get(sportLog.size() - 1).id);
        sportLog.remove(sportLog.size()-1);
        return true;
    }
}

/**
 * Adapter sloužící k vyhledávání sportů (nastaví vlastnosti vypisovaných předmětů)
 */
class searchSportAdapter extends CursorAdapter {
    private TextView text;

    public searchSportAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        text.setText(cursor.getString(1));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.searchview_item, parent, false);
        text = (TextView) view.findViewById(R.id.item);
        return view;
    }
}

/**
 * Pomocna struktura pro zapisovani pridanych sportu
 */
class sportLogger{
    long id;
    double totalEnergy;
    String lastString;

    public sportLogger(long id, double totalEnergy, String lastString) {
        this.id = id;
        this.totalEnergy = totalEnergy;
        this.lastString = lastString;
    }
}
