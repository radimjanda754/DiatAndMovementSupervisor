package cz.cvut.fit.damsupervisor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import cz.cvut.fit.damsupervisor.R;
import cz.cvut.fit.damsupervisor.database.localdbService;

/**
 * Aktivita dotazníku uživatele
 */
public class ChangeRecommendedActivity extends AppCompatActivity {
    localdbService dbController;
    EditText prots;
    EditText carbs;
    EditText fats;
    EditText energy;
    EditText kcaloutput;
    EditText weight;
    /**
     * Nastaví vše potřebné pro aktivitu
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_rec);

        // Tlacitko zpet
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception E) {E.printStackTrace();}

        // Nastaveni promenych
        dbController=new localdbService(this);
        prots=(EditText) findViewById(R.id.editTextProtsRec);
        carbs=(EditText) findViewById(R.id.editTextCarbsRec);
        fats=(EditText) findViewById(R.id.editTextFatsRec);
        energy=(EditText) findViewById(R.id.editTextEnergyRec);
        kcaloutput=(EditText) findViewById(R.id.editTextKcalOutput);
        weight=(EditText) findViewById(R.id.editTextWeight);
        prots.setText(Integer.toString(dbController.getUserProts()));
        carbs.setText(Integer.toString(dbController.getUserCarbs()));
        fats.setText(Integer.toString(dbController.getUserFats()));
        energy.setText(Integer.toString(dbController.getUserEnergy()));
        kcaloutput.setText(Integer.toString(dbController.getUserKcalOutput()));
        weight.setText(Integer.toString(dbController.getUserWeight()));
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
     * Volá se při stisknutí tlačítka uložení - zavede změny do databáze
     * @param view
     */
    public void onClickSave(View view)
    {
        int protsInt=0;
        int fatsInt=0;
        int carbsInt=0;
        int energyInt=0;
        int kcalOutInt=0;
        int weightInt=0;
        try{
            protsInt=Integer.parseInt(prots.getText().toString());
            carbsInt=Integer.parseInt(carbs.getText().toString());
            fatsInt=Integer.parseInt(fats.getText().toString());
            energyInt=Integer.parseInt(energy.getText().toString());
            kcalOutInt=Integer.parseInt(kcaloutput.getText().toString());
            weightInt=Integer.parseInt(weight.getText().toString());
        }
        catch (Exception e)
        {
            Toast.makeText(this, R.string.select_errorNoGeneticsSelected, Toast.LENGTH_SHORT).show();
            return;
        }
        dbController.setSettings(protsInt,carbsInt,fatsInt,energyInt,kcalOutInt,weightInt);
        Toast.makeText(this,R.string.changed,Toast.LENGTH_SHORT).show();
        finish();
    }
}
