package cz.cvut.fit.damsupervisor.controllers.AddFoodActivity;

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

import org.w3c.dom.Text;

import java.util.ArrayList;

import cz.cvut.fit.damsupervisor.R;
import cz.cvut.fit.damsupervisor.controllers.dateCtrl;
import cz.cvut.fit.damsupervisor.database.localdbService;

/**
 * Kontroller pro aktivitu AddFoodActivity
 */
public class addFoodCtrl {
    private SearchView search;
    private Context context;
    private localdbService dbController;
    public int selectedID;
    private double totalEnergy=0;
    private double totalCarbs=0;
    private double totalProts=0;
    private double totalFats=0;
    private int ADDED= Color.parseColor("#00AA00");
    private int NOT_ADDED= Color.parseColor("#AA0000");
    ArrayList<foodLogger> foodLog=new ArrayList<>();

    /**
     * Konstruktor, nastaví vše potřebné
     * @param search
     * @param context
     */
    public addFoodCtrl(SearchView search, Context context) {
        this.search = search;
        this.context = context;
        dbController=new localdbService(context);
        selectedID=-1;
    }

    /**
     * Hledání jídla v databázi
     * @param query současný vyhledávaný text
     */
    public void findFoodInDB(String query) {
        if(query.length()>=3) {
            // Cursor
            Cursor cursor = dbController.searchFood(query);

            // SearchView
            search.setSuggestionsAdapter(new searchFoodAdapter(context, cursor));
        }
        selectedID=-1;
    }

    /**
     * Nastaví ID vyhledávaného jídla podle jeho názvu
     * @param text název jídla
     * @return vrátí jestli byla operace úspěšná
     */
    public boolean setSelectedID(String text)
    {
        int id = dbController.getIdMealByString(text);
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
     * Volá se při stisknutí tlačítka, přidá jídlo do databáze
     * @param name název jídla
     * @param prots bílkoviny
     * @param carbs sacharidy
     * @param fats tuky
     * @param energy energie
     * @param gramspiece gramy na kus
     * @return
     */
    public boolean addFoodToDB(String name, String prots, String carbs, String fats, String energy ,String gramspiece)
    {
        int protsInt=0;
        int carbsInt=0;
        int fatsInt=0;
        int gramsInt=0;
        int energyInt=0;
        try {
            protsInt=Integer.parseInt(prots);
            carbsInt=Integer.parseInt(carbs);
            fatsInt=Integer.parseInt(fats);
            energyInt=Integer.parseInt(energy);
        } catch (NumberFormatException e) {
            Toast.makeText(context, R.string.food_addToDBWarning, Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            gramsInt=Integer.parseInt(gramspiece);
        } catch (NumberFormatException e){
            gramsInt=-1;
        }
        if(name.length()<3)
        {
            Toast.makeText(context, R.string.nameTooShort, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!dbController.insertMeal(name,protsInt,carbsInt,fatsInt,energyInt,gramsInt))
        {
            Toast.makeText(context, R.string.food_addToDBNameConflict, Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(context, R.string.addedtoDB, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Volá se při stisknutí tlačítka, přidá jídlo na dení seznam
     * @param value množství
     * @param gramsOrPiece určuje zda je množství uvedeno v gramech nebo kusech
     * @param type typ jídla (např snídaně)
     * @return
     */
    public boolean addFoodToDay(String value, int gramsOrPiece, int type, TextView addedFood, TextView addedFoodTotal)
    {
        int valueInt=0;
        if(selectedID<0)
        {
            Toast.makeText(context, R.string.food_addToDayNotSelected, Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            valueInt=Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Toast.makeText(context, R.string.food_addToDayNoValue, Toast.LENGTH_SHORT).show();
            return false;
        }
        // Informace o uspesnem zaznamenani
        Toast.makeText(context, R.string.addedtoDay, Toast.LENGTH_SHORT).show();
        Cursor FoodInfo = dbController.getFoodInfo(selectedID);
        double piece=Double.parseDouble(FoodInfo.getString(dbController.IDCOLUMN_MEALS_GRAMSINPIECE));
        String lastText=addedFood.getText().toString();
        if(gramsOrPiece>0&&piece>0) {
            addedFood.setText(context.getString(R.string.sport_food_added) +" "+ valueInt + context.getString(R.string.food_pieceTwochar) +" "+ FoodInfo.getString(dbController.IDCOLUMN_MEALS_MEALNAME) + " ("
                    +(int)piece*valueInt+context.getString(R.string.food_gramsOnechar)+") ");
            piece=piece/100;
        }
        else {
            addedFood.setText(context.getString(R.string.sport_food_added) +" "+ valueInt + context.getString(R.string.food_gramsOnechar) +" "+ FoodInfo.getString(dbController.IDCOLUMN_MEALS_MEALNAME));
            piece=0.01;
        }

        double thisCarbs=Integer.parseInt(FoodInfo.getString(dbController.IDCOLUMN_MEALS_CARBS))*piece*valueInt;
        double thisProts=Integer.parseInt(FoodInfo.getString(dbController.IDCOLUMN_MEALS_PROTS))*piece*valueInt;
        double thisFats=Integer.parseInt(FoodInfo.getString(dbController.IDCOLUMN_MEALS_FATS))*piece*valueInt;
        double thisEnery=Integer.parseInt(FoodInfo.getString(dbController.IDCOLUMN_MEALS_ENERGY))*piece*valueInt;
        totalCarbs += thisCarbs;
        totalProts += thisProts;
        totalFats += thisFats;
        totalEnergy += thisEnery;
        addedFoodTotal.setText(context.getString(R.string.sport_food_total)+" "+context.getString(R.string.food_protsOnechar)+(int)totalProts+", "+context.getString(R.string.food_carbsOnechar)+(int)totalCarbs
        +", "+context.getString(R.string.food_fatsOnechar)+(int)totalFats+", "+(int)totalEnergy+context.getString(R.string.kcal));
        addedFoodTotal.setTextColor(ADDED);
        addedFood.setTextColor(ADDED);
        // Vlozeni do db
        long id=dbController.insertDay(dateCtrl.getDateInt(),selectedID,-1,valueInt,gramsOrPiece,type);
        if(id>(-1))
            foodLog.add(new foodLogger(thisProts,thisCarbs,thisFats,thisEnery,id,lastText));
        return true;
    }

    /**
     * Odstrani posledni jidlo
     * @param addedFood
     * @param addedFoodTotal
     * @return
     */
    public boolean delFood(TextView addedFood, TextView addedFoodTotal)
    {
        if(foodLog.size()<1)
            return false;
        if(foodLog.size()==1)
        {
            addedFoodTotal.setTextColor(NOT_ADDED);
            addedFood.setTextColor(NOT_ADDED);
        }
        Toast.makeText(context, R.string.deletedfromDay, Toast.LENGTH_SHORT).show();
        totalCarbs -= foodLog.get(foodLog.size()-1).carbs;
        totalProts -= foodLog.get(foodLog.size()-1).prots;
        totalFats -= foodLog.get(foodLog.size()-1).fats;
        totalEnergy -= foodLog.get(foodLog.size()-1).energy;
        addedFood.setText(foodLog.get(foodLog.size()-1).lastTxt);
        addedFoodTotal.setText(context.getString(R.string.sport_food_total) + " " + context.getString(R.string.food_protsOnechar) + (int)totalProts + ", " + context.getString(R.string.food_carbsOnechar) + (int)totalCarbs
                + ", " + context.getString(R.string.food_fatsOnechar) + (int)totalFats + ", " + (int)totalEnergy + context.getString(R.string.kcal));
        dbController.deleteDayID(foodLog.get(foodLog.size() - 1).id);
        foodLog.remove(foodLog.size()-1);
        return true;
    }
}

/**
 * Adapter sloužící k vyhledávání jídel (nastaví vlastnosti vypisovaných předmětů)
 */
class searchFoodAdapter extends CursorAdapter {
    private TextView text;

    public searchFoodAdapter(Context context, Cursor cursor) {
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
 * Pomocna struktura pro zapisovani pridanych jidel
 */
class foodLogger{
    double prots;
    double carbs;
    double fats;
    double energy;
    long id;
    String lastTxt;

    public foodLogger(double prots, double carbs, double fats, double energy, long id, String lastTxt) {
        this.prots = prots;
        this.carbs = carbs;
        this.fats = fats;
        this.energy = energy;
        this.id = id;
        this.lastTxt=lastTxt;
    }
}