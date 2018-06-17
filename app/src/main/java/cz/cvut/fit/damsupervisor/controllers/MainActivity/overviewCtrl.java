package cz.cvut.fit.damsupervisor.controllers.MainActivity;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import cz.cvut.fit.damsupervisor.R;
import cz.cvut.fit.damsupervisor.database.localdbService;

/**
 * Controller slouží ke zpracovávání přehledu
 */
public class overviewCtrl {
    private Context ctx;
    private TextView protTxt;
    private TextView carbTxt;
    private TextView fatTxt;
    private TextView energyInTxt;
    private TextView energyOutTxt;
    private TextView energyTotalTxt;
    private TextView carbInfo;
    private TextView protInfo;
    private TextView fatInfo;
    private TextView energyInfo;
    private ProgressBar protProgress;
    private ProgressBar carbProgress;
    private ProgressBar fatProgress;
    private ProgressBar energyProgress;
    private final int ACCEPTABLE_PROTS_DIV=25;
    private final int ACCEPTABLE_CARBS_DIV=40;
    private final int ACCEPTABLE_FATS_DIV=25;
    private final int ACCEPTABLE_KCAL_DIV=200;
    private int GOODSTAT=Color.parseColor("#00AA00");
    private int BADSTAT=Color.parseColor("#CC0000");
    private int BLACK=Color.parseColor("#000000");
    localdbService dbController;

    /**
     * Konstruktor nastaví vše potřebné
     * @param ctx
     * @param protTxt
     * @param carbTxt
     * @param fatTxt
     * @param energyInTxt
     * @param energyOutTxt
     * @param energyTotalTxt
     * @param protProgress
     * @param carbProgress
     * @param fatProgress
     * @param energyProgress
     */
    public overviewCtrl(Context ctx, TextView protTxt, TextView carbTxt, TextView fatTxt,
                        TextView energyInTxt, TextView energyOutTxt, TextView energyTotalTxt,
                        ProgressBar protProgress, ProgressBar carbProgress, ProgressBar fatProgress,
                        ProgressBar energyProgress, TextView protInfo, TextView carbInfo, TextView fatInfo, TextView energyInfo) {
        this.ctx = ctx;
        this.protTxt = protTxt;
        this.carbTxt = carbTxt;
        this.fatTxt = fatTxt;
        this.energyInTxt = energyInTxt;
        this.energyOutTxt = energyOutTxt;
        this.energyTotalTxt = energyTotalTxt;
        this.protProgress = protProgress;
        this.carbProgress = carbProgress;
        this.fatProgress = fatProgress;
        this.energyProgress = energyProgress;
        this.protInfo=protInfo;
        this.carbInfo=carbInfo;
        this.fatInfo=fatInfo;
        this.energyInfo=energyInfo;
        dbController=new localdbService(ctx);
        refreshStats();
    }

    /**
     * Obnový veškeré výpisy v přehledu
     */
    public void refreshStats()
    {
        // Nastaveni progress baru
        int userProts=dbController.getUserProts();
        int userCarbs=dbController.getUserCarbs();
        int userFats=dbController.getUserFats();
        int userEnergy=dbController.getUserEnergy();
        int userBasicKcalOutput=dbController.getUserKcalOutput();

        protProgress.setMax(userProts);
        protProgress.setProgress((int) mealCtrl.totalProts);
        carbProgress.setMax(userCarbs);
        carbProgress.setProgress((int) mealCtrl.totalCarbs);
        fatProgress.setMax(userFats);
        fatProgress.setProgress((int)mealCtrl.totalFats);

        int total=(int)mealCtrl.totalKcalInput-(int)sportCtrl.totalKcalOutput-userBasicKcalOutput;
        if(dbController.getUserEnergy()>=0)
        {
            if(total>=0)
            {
                energyProgress.setMax(userEnergy);
                energyProgress.setProgress(total);
            }
            else
                energyProgress.setProgress(0);
        }
        else
        {
            if(total<0)
            {
                energyProgress.setMax(-1*userEnergy);
                energyProgress.setProgress(-1*total);
            }
            else
                energyProgress.setProgress(0);
        }

        // Nastaveni textu
        protTxt.setText(ctx.getString(R.string.overview_prots)+" = "+(int)mealCtrl.totalProts+" / "+userProts);
        carbTxt.setText(ctx.getString(R.string.overview_carbs)+" = "+(int)mealCtrl.totalCarbs+" / "+userCarbs);
        fatTxt.setText(ctx.getString(R.string.overview_fats)+" = "+(int)mealCtrl.totalFats+" / "+userFats);
        energyInTxt.setText(ctx.getString(R.string.overview_energyIn)+" = "+(int)mealCtrl.totalKcalInput);
        energyOutTxt.setText(ctx.getString(R.string.overview_energyOut)+" = -"+((int)sportCtrl.totalKcalOutput+userBasicKcalOutput));
        energyTotalTxt.setText(ctx.getString(R.string.overview_energyTotal) + " = " + total + " / " + userEnergy);

        // Nastaveni barev a varovani
        if (Math.abs((mealCtrl.totalProts-userProts)) > ACCEPTABLE_PROTS_DIV)
        {
            protTxt.setTextColor(BLACK);
            protInfo.setTextColor(BADSTAT);
            protInfo.setVisibility(View.VISIBLE);
            if(mealCtrl.totalProts>userProts)
                protInfo.setText(ctx.getString(R.string.warning_prots));
            else
                protInfo.setText(ctx.getString(R.string.warning_protsLow));
        }
        else {
            protTxt.setTextColor(GOODSTAT);
            protInfo.setVisibility(View.GONE);
        }

        if (Math.abs(mealCtrl.totalCarbs-userCarbs) > ACCEPTABLE_CARBS_DIV)
        {
            carbTxt.setTextColor(BLACK);
            carbInfo.setTextColor(BADSTAT);
            carbInfo.setVisibility(View.VISIBLE);
            if(mealCtrl.totalCarbs>userCarbs)
                carbInfo.setText(ctx.getString(R.string.warning_carbs));
            else
                carbInfo.setText(ctx.getString(R.string.warning_carbsLow));
        }
        else {
            carbTxt.setTextColor(GOODSTAT);
            carbInfo.setVisibility(View.GONE);
        }

        if (Math.abs(mealCtrl.totalFats-userFats) > ACCEPTABLE_FATS_DIV)
        {
            fatTxt.setTextColor(BLACK);
            fatInfo.setTextColor(BADSTAT);
            fatInfo.setVisibility(View.VISIBLE);
            if(mealCtrl.totalFats>userFats)
                fatInfo.setText(ctx.getString(R.string.warning_fats));
            else
                fatInfo.setText(ctx.getString(R.string.warning_fatsLow));
        }
        else {
            fatTxt.setTextColor(GOODSTAT);
            fatInfo.setVisibility(View.GONE);
        }

        if (Math.abs(total-userEnergy) > ACCEPTABLE_KCAL_DIV || Math.abs(userEnergy - total)>ACCEPTABLE_KCAL_DIV) {
            energyTotalTxt.setTextColor(BLACK);
            energyInfo.setTextColor(BADSTAT);
            energyInfo.setVisibility(View.VISIBLE);
            if(total>userEnergy)
                energyInfo.setText(ctx.getString(R.string.warning_energy));
            else
                energyInfo.setText(ctx.getString(R.string.warning_energyLow));
        }
        else {
            energyTotalTxt.setTextColor(GOODSTAT);
            energyInfo.setVisibility(View.GONE);
        }
    }
}
