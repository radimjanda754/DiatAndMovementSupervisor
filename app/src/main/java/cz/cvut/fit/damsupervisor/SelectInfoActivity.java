package cz.cvut.fit.damsupervisor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cz.cvut.fit.damsupervisor.database.localdbService;

/**
 * Aktivita dotazníku uživatele
 */
public class SelectInfoActivity extends AppCompatActivity {
    public static boolean cancel=false;
    private EditText age;
    private EditText height;
    private EditText weight;
    private int ageInt;
    private int heightInt;
    private int weightInt;
    private int genderInt;
    private int desireInt;
    private int genetics;
    private Spinner gender;
    private Spinner desire;
    private RadioButton endo;
    private RadioButton meso;
    private RadioButton ecto;
    private RadioGroup rGrp;
    private localdbService dbController;
    private final double DAILY_KCAL_CONST_MALE=24.0;
    private final double DAILY_KCAL_CONST_FEMALE=21.7;
    private final double DAILY_PROTS_NORMAL=1.0;
    private final double DAILY_PROTS_REDUCTION=1.2;
    private final double DAILY_PROTS_MUSCLEGAIN=1.5;
    private final double DAILY_CARBS_ENDOMORPH=3.6;
    private final double DAILY_CARBS_MESOMORPH=6.0;
    private final double DAILY_CARBS_ECTOMORPH=7.5;
    private final int DAILY_FATS_MALE=100;
    private final int DAILY_FATS_FEMALE=60;
    private final int DAILY_KCAL_DIFFERENCE=300;

    /**
     * Nastaví vše potřebné pro aktivitu
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(cancel)
        {
            try {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } catch (Exception E) {E.printStackTrace();}
        }
        dbController=new localdbService(this);
        setXML1();
    }

    /**
     * Vypnutí tlačítka zpět
     */
    @Override
    public void onBackPressed() {
        if(cancel)
            finish();
        else
            Toast.makeText(this,R.string.select_noBackButton,Toast.LENGTH_SHORT).show();
    }

    /**
     * Tlacitko zpet
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Tlacitko zpet
        if (id==android.R.id.home) {
            if(cancel)
                finish();
            else
                Toast.makeText(this,R.string.select_noBackButton,Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * Volá se při stisknutí tlačítka další - uloží hodnoty a přepne na druhou obrazovku dotazníku
     * @param view
     */
    public void onClickNext(View view)
    {
        try{
            ageInt=Integer.parseInt(age.getText().toString());
            heightInt=Integer.parseInt(height.getText().toString());
            weightInt=Integer.parseInt(weight.getText().toString());
        } catch (NumberFormatException ex) {
            Toast.makeText(this,R.string.select_errorNoInfoFiled,Toast.LENGTH_SHORT).show();
            return;
        }
        genderInt=gender.getSelectedItemPosition();
        desireInt=desire.getSelectedItemPosition();
        setXML2();
    }

    /**
     * Volá se při stisknutí tlačítka dokončení - uloží hodnoty a spustí vypočítávání živin
     * @param view
     */
    public void onClickFinish(View view)
    {
        int selected=rGrp.getCheckedRadioButtonId();
        if(selected>=0) {
            genetics=0;
            if(selected==R.id.radioButtonEndomorph)
                genetics=0;
            else if(selected==R.id.radioButtonMesomorph)
                genetics=1;
            else if(selected==R.id.radioButtonEctomorph)
                genetics=2;
            recommendStats();
        }
        else {
            Toast.makeText(this, R.string.select_errorNoGeneticsSelected, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Volá se při stisku tlačítka zpět - přepne na první obrazovku dotazníku
     * @param view
     */
    public void onClickBack(View view)
    {
        setXML1();
    }

    /**
     * Doporučí uživateli vhodné živiny na základě předem nastavených parametrů
     */
    private void recommendStats()
    {
        // Spocitani denniho energetickeho vydeje
        int dailyKcal=0;
        if(genderInt==0)
        {
            dailyKcal=(int)(weightInt*DAILY_KCAL_CONST_MALE);
        }
        else
        {
            dailyKcal=(int)(weightInt*DAILY_KCAL_CONST_FEMALE);
        }

        // Spocitani doporuceneho prijmu bilkovin, sacharidu, tuku
        // Proteiny a kcal
        int dailyProts=0;
        int dailyKcalDiff=0;
        if(desireInt==0)
        {
            dailyProts=(int)(weightInt*DAILY_PROTS_REDUCTION);
            dailyKcalDiff=-DAILY_KCAL_DIFFERENCE;
        }
        else if(desireInt==1)
        {
            dailyProts=(int)(weightInt*DAILY_PROTS_MUSCLEGAIN);
            dailyKcalDiff=0;
        }
        else if(desireInt==2)
        {
            dailyProts=(int)(weightInt*DAILY_PROTS_NORMAL);
            dailyKcalDiff=DAILY_KCAL_DIFFERENCE;
        }
        // Sacharidy
        int dailyCarbs=0;
        if(genetics==0)
        {
            dailyCarbs=(int)(weightInt*DAILY_CARBS_ENDOMORPH);}
        else if(genetics==1)
        {
            dailyCarbs=(int)(weightInt*DAILY_CARBS_MESOMORPH);
        }
        else if(genetics==2)
        {
            dailyCarbs=(int)(weightInt*DAILY_CARBS_ECTOMORPH);
        }
        // Tuky
        int dailyFats=0;
        if(genderInt==0)
            dailyFats=DAILY_FATS_MALE;
        if(genderInt==1)
            dailyFats=DAILY_FATS_FEMALE;

        // Vlozeni do databaze a ukonceni
        dbController.setSettings(dailyProts,dailyCarbs,dailyFats,dailyKcalDiff,dailyKcal,weightInt);
        dbController.generateSport();
        dbController.generateFood();
        Toast.makeText(this,R.string.select_recommended,Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Přepne a nastaví první obrazovku dotazníku
     */
    private void setXML1()
    {
        // Nastaveni spinneru
        setContentView(R.layout.activity_select_info);
        gender = (Spinner) findViewById(R.id.spinnerGender);
        String[] genderItems = {getString(R.string.select_genderMale), getString(R.string.select_genderFemale)};
        ArrayAdapter<String> genderAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,genderItems);
        gender.setAdapter(genderAdapter);

        desire = (Spinner) findViewById(R.id.spinnerDesire);
        String[] desireItems = {getString(R.string.select_desireFatDown), getString(R.string.select_desireMuscles), getString(R.string.select_desireKeep)};
        ArrayAdapter<String> desireAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,desireItems);
        desire.setAdapter(desireAdapter);

        // Nastaveni TextEditu
        age=(EditText) findViewById(R.id.editTextSelectAge);
        height=(EditText) findViewById(R.id.editTextSelectHeight);
        weight=(EditText) findViewById(R.id.editTextSelectWeight);
    }

    /**
     * Přepne a nastaví druhou obrazovku dotazníku
     */
    private void setXML2()
    {
        // Nastaveni RadioButtonu
        setContentView(R.layout.activity_select_info_part2);
        endo=(RadioButton) findViewById(R.id.radioButtonEndomorph);
        meso=(RadioButton) findViewById(R.id.radioButtonMesomorph);
        ecto=(RadioButton) findViewById(R.id.radioButtonEctomorph);
        rGrp=(RadioGroup) findViewById(R.id.radioGroupDesire);
    }
}
