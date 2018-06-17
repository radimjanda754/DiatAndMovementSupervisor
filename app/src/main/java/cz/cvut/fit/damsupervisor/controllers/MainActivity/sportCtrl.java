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
import android.widget.CursorAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.cvut.fit.damsupervisor.R;
import cz.cvut.fit.damsupervisor.controllers.dateCtrl;
import cz.cvut.fit.damsupervisor.database.localdbService;

/**
 * Controller pro zpracovávání výpisu denních sportů a počítání denních výdajů
 */
public class sportCtrl {
    private ExpandableListView sports;
    private Context ctx;
    private sportAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private localdbService dbController;
    private int userWeight;
    public static double totalKcalOutput;
    public static final double ENERGY_PER_DIFFICULT_KCALMIN = 0.0311904761904762;
    public static final double KJ_TO_KCAL = 0.238095238095238;
    public static final double KCAL_TO_KJ = 4.2;

    /**
     * Konstruktor, nastaví vše potřebné
     * @param ctx
     * @param sports
     */
    public sportCtrl(Context ctx, ExpandableListView sports) {
        this.ctx = ctx;
        this.sports = sports;
        dbController = new localdbService(ctx);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        totalKcalOutput=0;
        listAdapter = new sportAdapter(ctx, listDataChild, listDataHeader);
        sports.setAdapter(listAdapter);
        refreshWeight();
        refreshList();
    }

    /**
     * Znovu načte váhu uživatele
     */
    public void refreshWeight()
    {
        userWeight=dbController.getUserWeight();
    }

    /**
     * Obnoví denní list sportů a znovu spočítá denní výdej energií
     */
    public void refreshList(){
        listDataHeader.clear();
        listDataChild.clear();
        totalKcalOutput=0;
        Cursor crs = dbController.getDayInfoSport(dateCtrl.getDateInt());

        for(int i=0;i<crs.getCount();i++)
        {
            Cursor crsSport = dbController.getSportInfo(Integer.parseInt(crs.getString(dbController.IDCOLUMN_DAYS_SPORTID)));

            if(crsSport.getCount()>0)
            {
                // Vypocitani klicovich parametru
                String sportName = crsSport.getString(dbController.IDCOLUMN_SPORTS_SPORTNAME);
                int sportTime = Integer.parseInt(crs.getString(dbController.IDCOLUMN_DAYS_SPORTTIME));
                int sportDifficult = Integer.parseInt(crsSport.getString(dbController.IDCOLUMN_SPORTS_SPORTDIFF));
                double totalBurned = sportTime*sportDifficult*ENERGY_PER_DIFFICULT_KCALMIN*userWeight;
                totalKcalOutput+=totalBurned;

                // V pripade ze uz je za den sport se stejnym nazvem
                String sportNameDuplicator = sportName;
                int numberOfSport = 2;
                while(listDataHeader.contains(sportName))
                {
                    sportName = sportNameDuplicator + " " + numberOfSport;
                    numberOfSport++;
                }

                // Pridani zjistenych informaci do datasetu
                listDataHeader.add(sportName);
                List<String> tmpList = new ArrayList<>();
                tmpList.add(" - " + sportTime + " " + ctx.getString(R.string.minutes));
                tmpList.add(" - "+ ctx.getString(R.string.total_output) + ": " + (int)totalBurned + " " + ctx.getString(R.string.kcal));
                listDataChild.put(sportName, tmpList);
            }
            crs.moveToNext();
        }

        listAdapter.notifyDataSetChanged();
    }


}

/**
 * Adaptér sloužící k výpisu denních sportů v ExtendedListu
 */
class sportAdapter extends BaseExpandableListAdapter {
    private Context ctx;
    private HashMap<String, List<String>> sportTime;
    private List<String> sportMap;

    public sportAdapter(Context ctx, HashMap<String, List<String>> sportTime, List<String> sportMap )
    {
        this.ctx = ctx;
        this.sportTime = sportTime;
        this.sportMap = sportMap;

    }

    @Override
    public Object getChild(int parent, int child) {
        // TODO Auto-generated method stub
        return sportTime.get(sportMap.get(parent)).get(child);
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

        return sportTime.get(sportMap.get(arg0)).size();
    }

    @Override
    public Object getGroup(int arg0) {
        // TODO Auto-generated method stub
        return sportMap.get(arg0);
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return sportMap.size();
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

