package cz.cvut.fit.damsupervisor.controllers;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import cz.cvut.fit.damsupervisor.R;

/**
 * Ovládání datumu
 */
public class dateCtrl {
    public static int day;
    public static int month;
    public static int year;
    private static TextView textToShow;
    private static Calendar cal;
    private static String TODAY;
    private static String TOMORROW;

    /**
     * Konstruktor, nastaví vše potřebné
     * @param ctx
     * @param toChange
     */
    public dateCtrl(Context ctx,TextView toChange) {
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        TODAY=ctx.getString(R.string.today);
        TOMORROW=ctx.getString(R.string.tomorrow);
        textToShow=toChange;
        refreshText();
    }

    /**
     * Nastaví datum a obnový text
     * @param y rok
     * @param m měsíc
     * @param d den
     */
    public static void setDate(int y, int m, int d)
    {
        year=y;
        month=m;
        day=d;
        refreshText();
    }

    /**
     * Obnový text datumu
     */
    public static void refreshText(){
        textToShow.setText(getStringDate());
    }

    /**
     * Vrátí datum ve formátu int
     * @return datum ve formátu int
     */
    public static int getDateInt()
    {
        return (year*10000+month*100+day);

    }

    /**
     * Přepíše datum do stringu
     * @return datum ve formátu string
     */
    public static String getStringDate() {
        if(day == cal.get(Calendar.DAY_OF_MONTH) &&
           month ==  cal.get(Calendar.MONTH) &&
           year == cal.get(Calendar.YEAR))
            return TODAY;

        if(day - 1 == cal.get(Calendar.DAY_OF_MONTH) &&
                month ==  cal.get(Calendar.MONTH) &&
                year == cal.get(Calendar.YEAR))
            return TOMORROW;

        return(day+". "+(month+1)+". "+year);
    }
}
