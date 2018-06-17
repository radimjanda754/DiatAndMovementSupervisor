package cz.cvut.fit.damsupervisor.controllers.MainActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cz.cvut.fit.damsupervisor.R;
import cz.cvut.fit.damsupervisor.controllers.dateCtrl;
import cz.cvut.fit.damsupervisor.database.localdbService;

/**
 * Controller pro zpracovávání výpisu denních jídel a počítání denních příjmů
 */
public class mealCtrl {
    private ExpandableListView meals;
    private Context ctx;
    private mealAdapter listAdapter;
    private List<String> listDataHeader;
    private localdbService dbController;
    public static double totalProts;
    public static double totalCarbs;
    public static double totalFats;
    public static double totalKcalInput;

    private HashMap<String, List<String>> listDataChild;

    /**
     * Konstruktor, nastaví vše potřebné
     * @param ctx
     * @param meals
     */
    public mealCtrl(Context ctx, ExpandableListView meals) {
        this.ctx = ctx;
        this.meals = meals;
        dbController = new localdbService(ctx);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        totalProts = 0;
        totalCarbs = 0;
        totalFats = 0;
        totalKcalInput = 0;

        listAdapter = new mealAdapter(ctx, listDataChild, listDataHeader);
        meals.setAdapter(listAdapter);
        refreshList();
    }

    /**
     * Obnový list denních příjmů a znovu všechny příjmy přepočte
     */
    public void refreshList(){
        listDataHeader.clear();
        listDataChild.clear();
        totalProts = 0;
        totalCarbs = 0;
        totalFats = 0;
        totalKcalInput = 0;
        double thisTotalProts = 0;
        double thisTotalCarbs = 0;
        double thisTotalFats = 0;
        double thisTotalKcalInput = 0;
        List<String> thisFoodList=new ArrayList<>();
        double currentFooodProts = 0;
        double currentFooodFats = 0;
        double currentFooodCarbs = 0;
        double currentFooodKcalInput = 0;
        int lastMealType=-1;
        String currentMealType="";

        Cursor crs = dbController.getDayInfoFood(dateCtrl.getDateInt());

        for(int i=0;i<crs.getCount();i++)
        {
            Cursor crsFood = dbController.getFoodInfo(Integer.parseInt(crs.getString(dbController.IDCOLUMN_DAYS_MEALID)));

            // Nastaveni typu jidla nasledujicich zaznamu (snidane, obed, ..), v pripade ze nasleuji zaznamy s jinym typem
            if(Integer.parseInt(crs.getString(dbController.IDCOLUMN_DAYS_MEALTYPE))!=lastMealType)
            {
                // Dokonceni konkretniho typu jida
                if(thisFoodList.size()>0)
                {
                    thisFoodList.add(" - "+ctx.getString(R.string.total_input)+": "+
                            (int)thisTotalKcalInput+ctx.getString(R.string.kcal));
                    thisFoodList.add(" - - "+ctx.getString(R.string.overview_prots)+": "+(int)thisTotalProts);
                    thisFoodList.add(" - - "+ctx.getString(R.string.overview_carbs)+": "+(int)thisTotalCarbs);
                    thisFoodList.add(" - - "+ctx.getString(R.string.overview_fats)+": "+(int)thisTotalFats);
                    listDataChild.put(currentMealType, thisFoodList);
                    thisFoodList=new ArrayList<>();
                    thisTotalProts = 0;
                    thisTotalCarbs = 0;
                    thisTotalFats = 0;
                    thisTotalKcalInput = 0;
                }
                // Nastaveni noveho typu jidla
                lastMealType=Integer.parseInt(crs.getString(dbController.IDCOLUMN_DAYS_MEALTYPE));
                currentMealType=getMealType(lastMealType);
                listDataHeader.add(currentMealType);
            }

            if(crsFood.getCount()>0)
            {
                // Vypocitani koeficientu poctu 100g/kus
                double piece = Double.parseDouble(crsFood.getString(dbController.IDCOLUMN_MEALS_GRAMSINPIECE));
                boolean piecesUsed=false;
                if(Integer.parseInt(crs.getString(dbController.IDCOLUMN_DAYS_MEALGRAMSORPIECES))>0&&piece>0) {
                    piecesUsed = true;
                    piece=piece/100;
                }
                else
                {
                    piece=0.01;
                }

                // Nastaveni dalsich dulezitych informaci o jidle
                String foodName = crsFood.getString(dbController.IDCOLUMN_MEALS_MEALNAME);
                int count = Integer.parseInt(crs.getString(dbController.IDCOLUMN_DAYS_MEALCOUNT));
                currentFooodProts = Integer.parseInt(crsFood.getString(dbController.IDCOLUMN_MEALS_PROTS))*piece*count;
                currentFooodCarbs = Integer.parseInt(crsFood.getString(dbController.IDCOLUMN_MEALS_CARBS))*piece*count;
                currentFooodFats = Integer.parseInt(crsFood.getString(dbController.IDCOLUMN_MEALS_FATS))*piece*count;
                currentFooodKcalInput = Integer.parseInt(crsFood.getString(dbController.IDCOLUMN_MEALS_ENERGY))*piece*count;
                thisTotalProts += currentFooodProts;
                thisTotalCarbs += currentFooodCarbs;
                thisTotalFats += currentFooodFats;
                thisTotalKcalInput += currentFooodKcalInput;
                totalCarbs += currentFooodCarbs;
                totalFats += currentFooodFats;
                totalProts += currentFooodProts;
                totalKcalInput += currentFooodKcalInput;
                String unitString="";

                // Zapsani nasteveneho stringu do listu
                if(piecesUsed)
                    unitString=ctx.getString(R.string.food_pieceTwochar);
                else
                    unitString=ctx.getString(R.string.food_gramsOnechar);
                thisFoodList.add(count+unitString+" " + foodName + " ("+
                                ctx.getString(R.string.food_protsOnechar)+(int)currentFooodProts+", "+
                                ctx.getString(R.string.food_carbsOnechar)+(int)currentFooodCarbs+", "+
                                ctx.getString(R.string.food_fatsOnechar)+(int)currentFooodFats+", "+
                                (int)currentFooodKcalInput+ctx.getString(R.string.kcal) + ")"
                );
            }
            crs.moveToNext();
        }
        // Dokonceni konkretniho typu jida
        if(thisFoodList.size()>0)
        {
            thisFoodList.add(" - "+ctx.getString(R.string.total_input)+": "+
                    (int)thisTotalKcalInput+ctx.getString(R.string.kcal));
            thisFoodList.add(" - - "+ctx.getString(R.string.overview_prots)+": "+(int)thisTotalProts);
            thisFoodList.add(" - - "+ctx.getString(R.string.overview_carbs)+": "+(int)thisTotalCarbs);
            thisFoodList.add(" - - "+ctx.getString(R.string.overview_fats)+": "+(int)thisTotalFats);
            listDataChild.put(currentMealType,thisFoodList);
        }

        listAdapter.notifyDataSetChanged();
    }

    /**
     * Vrati typ jidla ve stringu
     * @param lastMealType ID typu jidla
     * @return Typ jidla ve stringu
     */
    public String getMealType(int lastMealType)
    {
        String currentMealType="";
        if(lastMealType==0)
            currentMealType=ctx.getString(R.string.food_breakfest);
        else if(lastMealType==1)
            currentMealType=ctx.getString(R.string.food_snack1);
        else if(lastMealType==2)
            currentMealType=ctx.getString(R.string.food_lunch);
        else if(lastMealType==3)
            currentMealType=ctx.getString(R.string.food_snack2);
        else if(lastMealType==4)
            currentMealType=ctx.getString(R.string.food_dinner);
        else if(lastMealType==5)
            currentMealType=ctx.getString(R.string.food_snack3);
        return currentMealType;
    }
}

/**
 * Adaptér sloužící k výpisu denních jídel v ExtendedListu
 */
class mealAdapter extends BaseExpandableListAdapter {
    private Context ctx;
    private HashMap<String, List<String>> mealTime;
    private List<String> mealMap;

    public mealAdapter(Context ctx, HashMap<String, List<String>> mealTime, List<String> mealMap )
    {
        this.ctx = ctx;
        this.mealTime = mealTime;
        this.mealMap = mealMap;

    }

    @Override
    public Object getChild(int parent, int child) {
        // TODO Auto-generated method stub
        return mealTime.get(mealMap.get(parent)).get(child);
    }

    @Override
    public long getChildId(int parent, int child) {
        // TODO Auto-generated method stub
        return child;
    }

    @Override
    public View getChildView(int parent, int child, boolean lastChild, View convertview,
                             ViewGroup parentview)
    {
        String child_title =  (String) getChild(parent, child);
        if(convertview == null)
        {
            LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertview = inflator.inflate(R.layout.expandablelistview_child, parentview,false);
        }
        TextView child_textview = (TextView) convertview.findViewById(R.id.child_txt);
        child_textview.setText(child_title);


        return convertview;
    }

    @Override
    public int getChildrenCount(int arg0) {

        return mealTime.get(mealMap.get(arg0)).size();
    }

    @Override
    public Object getGroup(int arg0) {
        // TODO Auto-generated method stub
        return mealMap.get(arg0);
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return mealMap.size();
    }

    @Override
    public long getGroupId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getGroupView(int parent, boolean isExpanded, View convertview, ViewGroup parentview) {
        // TODO Auto-generated method stub
        String group_title = (String) getGroup(parent);
        if(convertview == null)
        {
            LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertview = inflator.inflate(R.layout.expandablelistview_parent, parentview,false);
        }
        TextView parent_textview = (TextView) convertview.findViewById(R.id.parent_txt);
        parent_textview.setTypeface(null, Typeface.BOLD);
        parent_textview.setText(group_title);
        return convertview;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return false;
    }

}


