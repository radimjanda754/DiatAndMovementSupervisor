package cz.cvut.fit.damsupervisor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import cz.cvut.fit.damsupervisor.controllers.dateCtrl;
import cz.cvut.fit.damsupervisor.controllers.MainActivity.mealCtrl;
import cz.cvut.fit.damsupervisor.controllers.MainActivity.overviewCtrl;
import cz.cvut.fit.damsupervisor.controllers.MainActivity.sportCtrl;
import cz.cvut.fit.damsupervisor.database.localdbService;

/**
 * Hlavní aktivita
 */
public class MainActivity extends AppCompatActivity {
    public static boolean updateDb=false;
    private mealCtrl mealController;
    private sportCtrl sportController;
    private overviewCtrl overviewController;
    private dateCtrl dateController;
    private localdbService dbController;
    private Context ctx;
    final private int DATE_DIALOG_ID=12;
    static InterstitialAd mInterstitialAd1;
    static InterstitialAd mInterstitialAd2;
    private static final int REQUEST_WRITE_STORAGE = 112;

    /**
     * Nastaví všechny prvky ve hlavní aktivitě
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx=this;

        // Nastaveni reklam
        // JidloADD
        mInterstitialAd1 = new InterstitialAd(this);
        mInterstitialAd1.setAdUnitId("ca-app-pub-2265273170373705/8866554878");
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("4A6958A57986B492E9B80F158731BDBC").build();
        mInterstitialAd1.loadAd(adRequest);
        mInterstitialAd1.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                AdRequest adRequest = new AdRequest.Builder().addTestDevice("4A6958A57986B492E9B80F158731BDBC").build();
                mInterstitialAd1.loadAd(adRequest);
            }
        });
        // Sporty ADD
        mInterstitialAd2 = new InterstitialAd(this);
        mInterstitialAd2.setAdUnitId("ca-app-pub-2265273170373705/6412616073");
        AdRequest adRequest2 = new AdRequest.Builder().addTestDevice("4A6958A57986B492E9B80F158731BDBC").build();
        mInterstitialAd2.loadAd(adRequest2);
        mInterstitialAd2.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                AdRequest adRequest2 = new AdRequest.Builder().addTestDevice("4A6958A57986B492E9B80F158731BDBC").build();
                mInterstitialAd2.loadAd(adRequest2);
            }
        });
        // BANNER ADDS
        AdView mAdView1 = (AdView) findViewById(R.id.adViewMeal);
        AdRequest adRequest3 = new AdRequest.Builder().addTestDevice("4A6958A57986B492E9B80F158731BDBC").build();
        mAdView1.loadAd(adRequest3);
        AdView mAdView2 = (AdView) findViewById(R.id.adViewSport);
        AdRequest adRequest4 = new AdRequest.Builder().addTestDevice("4A6958A57986B492E9B80F158731BDBC").build();
        mAdView2.loadAd(adRequest4);

        // Pripraveni tabHostu (rozcleneni na overviewCtrl, mealCtrl sportCtrl)
        TabHost tabs = (TabHost) findViewById(R.id.tabHost);
        tabs.setup();
        TabHost.TabSpec tabOverview = tabs.newTabSpec("Prehled");
        tabOverview.setIndicator(getString(R.string.title_activity_overview));
        tabOverview.setContent(R.id.overview);
        TabHost.TabSpec tabMeal = tabs.newTabSpec("Jidla");
        tabMeal.setIndicator(getString(R.string.title_activity_meal));
        tabMeal.setContent(R.id.meal);
        TabHost.TabSpec tabSport = tabs.newTabSpec("Sporty");
        tabSport.setIndicator(getString(R.string.title_activity_sport));
        tabSport.setContent(R.id.sport);
        tabs.addTab(tabOverview);
        tabs.addTab(tabMeal);
        tabs.addTab(tabSport);

        // Pripraveni mealCtrl, sportCtrl listu a dateCtrl controlleru
        dbController = new localdbService(this);
        dateController = new dateCtrl(this,(TextView) findViewById(R.id.dateText));
        mealController = new mealCtrl(this,(ExpandableListView)findViewById(R.id.expandableListViewMeals));
        sportController = new sportCtrl(this,(ExpandableListView)findViewById(R.id.expandableListViewSports));




        //Nastaveni overview
        TextView protTxt = (TextView) findViewById(R.id.textViewOverviewProts);
        TextView carbTxt = (TextView) findViewById(R.id.textViewOverviewCarbs);
        TextView fatTxt = (TextView) findViewById(R.id.textViewOverviewFats);
        TextView energyInTxt = (TextView) findViewById(R.id.textViewEnergyIn);
        TextView energyOutTxt = (TextView) findViewById(R.id.textViewEnergyOut);
        TextView energyTotalTxt = (TextView) findViewById(R.id.textViewEnergyTotal);
        ProgressBar protProgress = (ProgressBar) findViewById(R.id.progressBarProts);
        ProgressBar carbProgress = (ProgressBar) findViewById(R.id.progressBarCarbs);
        ProgressBar fatProgress = (ProgressBar) findViewById(R.id.progressBarFats);
        ProgressBar energyProgress = (ProgressBar) findViewById(R.id.progressBarEnergy);
        TextView protInfo = (TextView) findViewById(R.id.textViewProtInfo);
        TextView carbInfo = (TextView) findViewById(R.id.textViewCarbInfo);
        TextView fatInfo = (TextView) findViewById(R.id.textViewFatInfo);
        TextView energyInfo = (TextView) findViewById(R.id.textViewEnergyInfo);
        overviewController = new overviewCtrl(this,protTxt,carbTxt,fatTxt,energyInTxt,energyOutTxt,energyTotalTxt,
                protProgress,carbProgress,fatProgress,energyProgress,protInfo,carbInfo,fatInfo,energyInfo);

        //Nejsou nastavene uzivatelske udaje
        if(dbController.getUserProts()<0) {
            Intent i = new Intent(MainActivity.this, SelectInfoActivity.class);
            startActivity(i);
        }

        // Pripadny update database
        if(updateDb)
        {
            Log.i("MainActivity","Database update initiated");
            dbController.generateFood();
            dbController.generateSport();
            updateDb=false;
        }
    }


    /**
     * Obnovení aktivity, zároven obnový všechny seznamy a přehledy
     */
    @Override
    protected void onResume() {
        super.onResume();
        sportController.refreshWeight();
        sportController.refreshList();
        mealController.refreshList();
        overviewController.refreshStats();

        //Nejsou nastavene uzivatelske udaje
        /*if(dbController.getUserProts()<0) {
            Intent i = new Intent(MainActivity.this, SelectInfoActivity.class);
            startActivity(i);
        }*/
    }

    // Meal Controller Methods
    /**
     * Volá se při stisknutí tlačítka pro přidání jídla - přechod do aktivity AddFoodActivity
     * @param view
     */
    public void addMeal(View view) {
        Intent i = new Intent(MainActivity.this,AddFoodActivity.class);
        startActivity(i);
    }

    // Sport Controller Methods
    /**
     * Volá se při stisknutí tlačítka pro přidání sportu - přechod do aktivity AddSportActivity
     * @param view
     */
    public void addSport(View view) {
        Intent i = new Intent(MainActivity.this,AddSportActivity.class);
        startActivity(i);
    }

    // Date Controller Methods

    /**
     * Volá se při stisknutí tlačítka změny datumu, vytvoří dialog
     * @param view
     */
    public void changeDate(View view) {
        showDialog(DATE_DIALOG_ID);
    }

    /**
     * Slouží k vytvoření dialogu
     * @param id id dialogu
     * @return vrací nový vytvořený dialog
     */
    @Override
    protected Dialog onCreateDialog(int id){
        if(id==DATE_DIALOG_ID)
        {
            return new DatePickerDialog(this,dpickerlistener,dateController.year,dateController.month,dateController.day);
        }
        return null;
    }

    /**
     * Slouží k vytvoření dialogu pro změnu datumu
     */
    private DatePickerDialog.OnDateSetListener dpickerlistener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    dateController.setDate(year,monthOfYear,dayOfMonth);
                    sportController.refreshList();
                    mealController.refreshList();
                    overviewController.refreshStats();
                }
            };

    /**
     * Volá se při stisknutí tlačítka pro znovudoporučení příjmů - přepne do aktivity doporučování příjmů
     * @param view
     */
    public void onClickRecommend(View view)
    {
        SelectInfoActivity.cancel=true;
        Intent i = new Intent(MainActivity.this, SelectInfoActivity.class);
        startActivity(i);
    }

    /**
     * Volá se při stisknutí tlačítka pro změnu příjmů - přepne do aktivity změny příjmů
     * @param view
     */
    public void onClickChange(View view)
    {
        SelectInfoActivity.cancel=true;
        Intent i = new Intent(MainActivity.this, ChangeRecommendedActivity.class);
        startActivity(i);
    }

    /**
     * Volá se při stisknutí tlačítka pro smazání sportů - smaže sporty za vybraný den
     * @param view
     */
    public void onClickDeleteSport(View view)
    {
        // Nastaveni potrebnych veci
        final ArrayList<Integer> mSelectedItems = new ArrayList();  // Where we track the selected items
        ArrayList<String> listedItems=new ArrayList<>();
        final HashMap<Integer,Integer> listedItemsID=new HashMap();
        listedItems.add(getString(R.string.dialog_deleteAll));
        // Vyhledani polozek v DB
        Cursor Sports=dbController.getDayInfoSport(dateCtrl.getDateInt());
        // Sestavi vsechno potrebne pro mazani
        if(!Sports.moveToFirst()) {
            Toast.makeText(this,R.string.dialog_emptyList, Toast.LENGTH_SHORT).show();
            return;
        }
        int counter=1;
        do {
            listedItemsID.put(counter,Sports.getInt(Sports.getColumnIndex("_id")));
            counter++;
            Cursor sportNameCrs=dbController.getSportInfo(Sports.getInt(Sports.getColumnIndex(dbController.DAYS_COLUMN_SPORTS)));
            String sportName=sportNameCrs.getString(dbController.IDCOLUMN_SPORTS_SPORTNAME);
            String sportNameOrig=sportName;
            int sportNameCounter=2;
            while(listedItems.contains(sportName))
            {
                sportName=sportNameOrig+" "+sportNameCounter;
                sportNameCounter++;
            }
            listedItems.add(sportName);
        } while(Sports.moveToNext());

        // Vztvoreni dialogu
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.dialog_confirm_sport)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(listedItems.toArray(new String[0]), null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        if (mSelectedItems.contains(0)) {
                            // Smazat vse
                            dbController.deleteDaySports(dateCtrl.getDateInt());
                        } else {
                            // Smazat jen neco
                            for (int i : mSelectedItems) {
                                dbController.deleteDayID(listedItemsID.get(i));
                            }
                        }
                        sportController.refreshList();
                        overviewController.refreshStats();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Volá se při stisknutí tlačítka doporučování jídel - smaže jídla za vybraný den
     * @param view
     */
    public void onClickDeleteFood(View view)
    {
        // Nastaveni potrebnych veci
        final ArrayList<Integer> mSelectedItems = new ArrayList();  // Where we track the selected items
        ArrayList<String> listedItems=new ArrayList<>();
        final HashMap<Integer,ArrayList<Integer>> listedItemsID=new HashMap();
        listedItems.add(getString(R.string.dialog_deleteAll));
        // Vyhledani polozek v DB
        Cursor Foods=dbController.getDayInfoFood(dateCtrl.getDateInt());
        // Sestavi vsechno potrebne pro mazani
        if(!Foods.moveToFirst()) {
            Toast.makeText(this,R.string.dialog_emptyList, Toast.LENGTH_SHORT).show();
            return;
        }
        int lastFoodType=Foods.getInt(Foods.getColumnIndex(dbController.DAYS_COLUMN_MEALTYPE));
        int counter=1;
        ArrayList<Integer> insertToMap=new ArrayList<>();
        do {
            if(lastFoodType!=Foods.getInt(Foods.getColumnIndex(dbController.DAYS_COLUMN_MEALTYPE)))
            {
                listedItems.add(mealController.getMealType(lastFoodType));
                listedItemsID.put(counter, new ArrayList<>(insertToMap));
                insertToMap.clear();
                counter++;
                lastFoodType=Foods.getInt(Foods.getColumnIndex(dbController.DAYS_COLUMN_MEALTYPE));
            }
            insertToMap.add(Foods.getInt(Foods.getColumnIndex("_id")));
        } while(Foods.moveToNext());
        listedItems.add(mealController.getMealType(lastFoodType));
        listedItemsID.put(counter, new ArrayList<>(insertToMap));

        // Vztvoreni dialogu
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.dialog_confirm_food)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(listedItems.toArray(new String[0]), null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        if (mSelectedItems.contains(0)) {
                            // Smazat vse
                            dbController.deleteDayFoods(dateCtrl.getDateInt());
                        } else {
                            // Smazat jen neco
                            for (int i : mSelectedItems) {
                                for (int deleteID : listedItemsID.get(i)) {
                                    dbController.deleteDayID(deleteID);
                                }
                            }
                        }
                        mealController.refreshList();
                        overviewController.refreshStats();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Volá se pro stisknotí tlačítka zálohy databáze
     * @param view
     */
    public void onClickBackup(View view)
    {
        final String out= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/DAMsupervisor.db";
        final String in=getDatabasePath("DAMsupervisor.db").toString();
        Log.i("DBs","in:"+in+" out:"+out);

        // Android 6.0 permission
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

        // Vztvoreni dialogu
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.dialog_exportTitle)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMessage(getString(R.string.dialog_exportBody)+" "+out)
                .setPositiveButton(R.string.dialog_export, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        try {
                            copy(new File(in), new File(out));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx,R.string.toast_error,Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                        Toast.makeText(ctx,R.string.toast_successfull,Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Volá se pro stisknotí tlačítka obnovení databáze
     * @param view
     */
    public void onClickRestore(View view)
    {
        final String in= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/DAMsupervisor.db";
        final String out=getDatabasePath("DAMsupervisor.db").toString();
        Log.i("DBs","in:"+in+" out:"+out);

        // Vztvoreni dialogu
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.dialog_importTitle)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMessage(getString(R.string.dialog_importBody)+" "+in)
                .setPositiveButton(R.string.dialog_import, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        // Checks if backup exists
                        File f=new File(in);
                        if (!f.exists())
                        {
                            Toast.makeText(ctx,R.string.toast_notfound,Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                        try {
                            copy(f, new File(out));
                        } catch (IOException e) {
                            Toast.makeText(ctx,R.string.toast_error,Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                        Toast.makeText(ctx,R.string.toast_successfull,Toast.LENGTH_SHORT).show();
                        sportController.refreshWeight();
                        sportController.refreshList();
                        mealController.refreshList();
                        overviewController.refreshStats();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
