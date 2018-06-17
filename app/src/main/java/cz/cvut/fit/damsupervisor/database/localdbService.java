package cz.cvut.fit.damsupervisor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.Normalizer;
import java.util.Locale;

import cz.cvut.fit.damsupervisor.MainActivity;


/**
 *  Zajištujě veškerou práci s lokální databází
 */
public class localdbService extends SQLiteOpenHelper {
    private Context ctx;
    public static final String DATABASE_NAME = "DAMsupervisor.db";
    public static final int DATABASE_VERSION = 13;
    public static final String SPORTS_TABLE_NAME = "sports";
    public static final String SPORTS_COLUMN_NAME = "sportname";
    public static final String SPORTS_COLUMN_NAMEASCII = "sportascii";
    public static final String SPORTS_COLUMN_ENERGY = "energy";
    public static final String MEALS_TABLE_NAME = "meals";
    public static final String MEALS_COLUMN_NAME = "mealname";
    public static final String MEALS_COLUMN_NAMEASCII = "mealascii";
    public static final String MEALS_COLUMN_PROTEINS = "proteins";
    public static final String MEALS_COLUMN_CARBOHYDRATES = "carbs";
    public static final String MEALS_COLUMN_FATS = "fats";
    public static final String MEALS_COLUMN_ENERGY_KCAL = "energykcal";
    public static final String MEALS_COLUMN_GRAMSPIECE = "gramspiece";
    public static final String DAYS_TABLE_NAME = "days";
    public static final String DAYS_COLUMN_DATE = "date";
    public static final String DAYS_COLUMN_MEALS = "id_meal";
    public static final String DAYS_COLUMN_SPORTS = "id_sport";
    public static final String DAYS_COLUMN_MEALCOUNT_OR_TIME = "mealcounttime";
    public static final String DAYS_COLUMN_MEALGRAMSORPIECES = "mealgorpiece";
    public static final String DAYS_COLUMN_MEALTYPE = "mealtype";
    public static final String SETTINGS_TABLE_NAME = "settings";
    public static final String SETTINGS_COLUMN_PROTS = "proteins";
    public static final String SETTINGS_COLUMN_CARBS = "carbs";
    public static final String SETTINGS_COLUMN_FATS = "fats";
    public static final String SETTINGS_COLUMN_ENERGY = "energy";
    public static final String SETTINGS_COLUMN_WEIGHT = "weight";
    public static final String SETTINGS_COLUMN_DAILY_KCAl = "kcaldaily";
    public static final int IDCOLUMN_DAYS_SPORTID = 3;
    public static final int IDCOLUMN_DAYS_SPORTTIME = 4;
    public static final int IDCOLUMN_SPORTS_SPORTNAME = 1;
    public static final int IDCOLUMN_SPORTS_SPORTDIFF = 2;
    public static final int IDCOLUMN_DAYS_MEALID = 2;
    public static final int IDCOLUMN_DAYS_MEALCOUNT = 4;
    public static final int IDCOLUMN_DAYS_MEALGRAMSORPIECES = 5;
    public static final int IDCOLUMN_DAYS_MEALTYPE = 6;
    public static final int IDCOLUMN_MEALS_MEALNAME = 1;
    public static final int IDCOLUMN_MEALS_PROTS = 2;
    public static final int IDCOLUMN_MEALS_CARBS = 3;
    public static final int IDCOLUMN_MEALS_FATS = 4;
    public static final int IDCOLUMN_MEALS_ENERGY = 5;
    public static final int IDCOLUMN_MEALS_GRAMSINPIECE = 6;

    /**
     * Konstruktor
     * @param context
     */
    public localdbService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx=context;
    }

    /**
     * Vytvoří databázi
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + MEALS_TABLE_NAME + " (_id integer primary key, " +
                        MEALS_COLUMN_NAME + " text unique, " +
                        MEALS_COLUMN_PROTEINS + " integer, " +
                        MEALS_COLUMN_CARBOHYDRATES + " integer, " +
                        MEALS_COLUMN_FATS + " integer, " +
                        MEALS_COLUMN_ENERGY_KCAL + " integer, " +
                        MEALS_COLUMN_GRAMSPIECE + " integer, "+
                        MEALS_COLUMN_NAMEASCII + " text)"
        );
        db.execSQL(
                "create table " + SPORTS_TABLE_NAME + " (_id integer primary key, " +
                        SPORTS_COLUMN_NAME + " text unique, " +
                        SPORTS_COLUMN_ENERGY + " integer, " +
                        SPORTS_COLUMN_NAMEASCII + " text)"
        );
        db.execSQL(
                "create table " + DAYS_TABLE_NAME + " (_id integer primary key, " +
                        DAYS_COLUMN_DATE + " integer, " +
                        DAYS_COLUMN_MEALS + " integer, " +
                        DAYS_COLUMN_SPORTS + " integer, " +
                        DAYS_COLUMN_MEALCOUNT_OR_TIME + " integer, " +
                        DAYS_COLUMN_MEALGRAMSORPIECES + " integer, " +
                        DAYS_COLUMN_MEALTYPE + " integer)"
        );
        db.execSQL(
                "create table " + SETTINGS_TABLE_NAME + " (_id integer primary key, " +
                        SETTINGS_COLUMN_PROTS + " integer, " +
                        SETTINGS_COLUMN_FATS + " integer, " +
                        SETTINGS_COLUMN_CARBS + " integer, " +
                        SETTINGS_COLUMN_ENERGY + " integer, " +
                        SETTINGS_COLUMN_WEIGHT + " integer, " +
                        SETTINGS_COLUMN_DAILY_KCAl + " integer)"
        );
    }

    /**
     * Smaže a znovu vytvoří databázi při updatu na novější verzi
     * @param db databáze
     * @param oldVersion id staré verze
     * @param newVersion id nové verze
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        //db.execSQL("DROP TABLE IF EXISTS " + MEALS_TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + SPORTS_TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + DAYS_TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE_NAME);
        //onCreate(db);
        MainActivity.updateDb=true;
    }

    /**
     * Resetuje tabulku settings
     */
    public void resetSettings() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE_NAME);
        db.execSQL(
                "create table " + SETTINGS_TABLE_NAME + " (_id integer primary key, " +
                        SETTINGS_COLUMN_PROTS + " integer, " +
                        SETTINGS_COLUMN_FATS + " integer, " +
                        SETTINGS_COLUMN_CARBS + " integer, " +
                        SETTINGS_COLUMN_ENERGY + " integer, " +
                        SETTINGS_COLUMN_WEIGHT + " integer, " +
                        SETTINGS_COLUMN_DAILY_KCAl + " integer)"
        );
    }

    /**
     * Vloží jídlo do databáze
     * @param name  název jídla
     * @param prots bílkoviny
     * @param carbs sacharidy
     * @param fats tuky
     * @param energy energie
     * @param gramspiece gramy na kus
     * @return vrátí jestli bylo přidání úspěšné
     */
    public boolean insertMeal(String name, int prots, int carbs, int fats, int energy, int gramspiece) {
        SQLiteDatabase db = this.getWritableDatabase();

        String asciiName = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        ContentValues contentValues = new ContentValues();
        contentValues.put(MEALS_COLUMN_NAME, name);
        contentValues.put(MEALS_COLUMN_PROTEINS, prots);
        contentValues.put(MEALS_COLUMN_CARBOHYDRATES, carbs);
        contentValues.put(MEALS_COLUMN_FATS, fats);
        contentValues.put(MEALS_COLUMN_ENERGY_KCAL, energy);
        contentValues.put(MEALS_COLUMN_GRAMSPIECE, gramspiece);
        contentValues.put(MEALS_COLUMN_NAMEASCII, asciiName);
        try {
            db.insertOrThrow(MEALS_TABLE_NAME, null, contentValues);
        } catch (Exception ex) {
            //ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Vloží sport do databáze
     * @param name název sportu
     * @param energy obtížnost sportu (1-10)
     * @return vrátí jestli bylo přidání úspěšné
     */
    public boolean insertSport(String name, int energy) {
        SQLiteDatabase db = this.getWritableDatabase();

        String asciiName = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        ContentValues contentValues = new ContentValues();
        contentValues.put(SPORTS_COLUMN_NAME, name);
        contentValues.put(SPORTS_COLUMN_ENERGY, energy);
        contentValues.put(SPORTS_COLUMN_NAMEASCII, asciiName);
        try {
            db.insertOrThrow(SPORTS_TABLE_NAME, null, contentValues);
        } catch (Exception ex) {
            //ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Vloží nějaký denní záznam jídla nebo aktivity
     * @param date datum
     * @param mealid id jídla (-1 pokud se jedná o sport)
     * @param sportid id sportu (-1 pokud se jedná o jídlo)
     * @param count počet jídla nebo minuty sportu
     * @param kgOrGrams určuje jestli je jídlo uvedeno v gramech nebo kusech
     * @param mealtype určuje typ jídla (Snídaně,Večeře,..)
     * @return Cursor s informacemi o záznamu
     */
    public long insertDay(int date, int mealid, int sportid, int count, int kgOrGrams, int mealtype) {
        long id=-1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DAYS_COLUMN_DATE, date);
        contentValues.put(DAYS_COLUMN_MEALS, mealid);
        contentValues.put(DAYS_COLUMN_SPORTS, sportid);
        contentValues.put(DAYS_COLUMN_MEALCOUNT_OR_TIME, count);
        contentValues.put(DAYS_COLUMN_MEALGRAMSORPIECES, kgOrGrams);
        contentValues.put(DAYS_COLUMN_MEALTYPE, mealtype);
        try {
            id=db.insertOrThrow(DAYS_TABLE_NAME, null, contentValues);
        } catch (SQLiteException ex) {
            ex.printStackTrace();
            return -1;
        }
        return id;
    }

    /**
     * Smaze denni zaznam s odpovidajicim ID
     * @param id id zaznamu
     */
    public void deleteDayID(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(DAYS_TABLE_NAME, "_id" + "=" + id, null);
    }

    /**
     * Získá veškeré info o konrétním jídle
     * @param id id jídla
     * @return Cursor s informacemi o jídle
     */
    public Cursor getFoodInfo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + MEALS_TABLE_NAME + " where _id = " + id, null);
        res.moveToFirst();
        return res;
    }

    /**
     * Získá veškeré info o konkrétním sportu
     * @param id id sportu
     * @return Cursor s informacemi o sportu
     */
    public Cursor getSportInfo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + SPORTS_TABLE_NAME + " where _id = " + id, null);
        res.moveToFirst();
        return res;
    }

    /**
     * Získá veškeré záznamy o jídlech za určitý den
     * @param date datum
     * @return Cursor se záznamy
     */
    public Cursor getDayInfoFood(int date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DAYS_TABLE_NAME +
                " where " + DAYS_COLUMN_DATE + " = " + date +
                " and " + DAYS_COLUMN_MEALS + " >= 0" +
                " order by " + DAYS_COLUMN_MEALTYPE, null);
        res.moveToFirst();
        return res;
    }

    /**
     * Získá veškeré záznamy o sportu za určitý den
     * @param date datum
     * @return Cursor se záznamy
     */
    public Cursor getDayInfoSport(int date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DAYS_TABLE_NAME +
                " where " + DAYS_COLUMN_DATE + " = " + date +
                " and " + DAYS_COLUMN_SPORTS + " >= 0" +
                " order by _id", null);
        res.moveToFirst();
        return res;
    }

    /**
     * Smaže sporty za určitý den
     * @param date datum
     */
    public void deleteDaySports(int date) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(DAYS_TABLE_NAME, DAYS_COLUMN_DATE + "=" + date + " and " + DAYS_COLUMN_SPORTS + ">=" + 0, null);
    }

    /**
     * Smaže jídla za určitý den
     * @param date datum
     */
    public void deleteDayFoods(int date) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(DAYS_TABLE_NAME, DAYS_COLUMN_DATE + "=" + date + " and " + DAYS_COLUMN_MEALS + ">=" + 0, null);
    }

    /**
     * Smaze zaznam s odpovidajicim ID
     * @param id ID zaznamu, ktery bude smazan
     */
    public void deleteDayID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(DAYS_TABLE_NAME, "_id = " + id, null);
    }

    /**
     * Získá id jídla podle názvu
     * @param text název
     * @return id jídla
     */
    public int getIdMealByString(String text) {
        SQLiteDatabase db = this.getReadableDatabase();

        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        Cursor res = db.rawQuery("select _id from " + MEALS_TABLE_NAME + " where "
                + MEALS_COLUMN_NAMEASCII + " like \'" + text + "\'", null);
        res.moveToFirst();
        if (res.getCount() >= 1)
            return Integer.parseInt(res.getString(0));
        return -1;
    }

    /**
     * Získá id sportu podle názvu
     * @param text název
     * @return id sportu
     */
    public int getIdSportByString(String text) {
        SQLiteDatabase db = this.getReadableDatabase();

        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        Cursor res = db.rawQuery("select _id from " + SPORTS_TABLE_NAME + " where "
                + SPORTS_COLUMN_NAMEASCII + " like \'" + text + "\'", null);
        res.moveToFirst();
        if (res.getCount() >= 1)
            return Integer.parseInt(res.getString(0));
        return -1;
    }

    /**
     * Vyhledání jídla podle názvu
     * @param text název
     * @return Cursor nalezených jídel
     */
    public Cursor searchFood(String text) {
        SQLiteDatabase db = this.getReadableDatabase();

        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        Cursor res = db.rawQuery("select * from " + MEALS_TABLE_NAME + " where " +
                MEALS_COLUMN_NAMEASCII + " like \'%" + text + "%\'", null);
        res.moveToFirst();
        return res;
    }

    /**
     * Vyhledání sportu podle názvu
     * @param text název
     * @return Cursor nalezených sportů
     */
    public Cursor searchSport(String text) {
        SQLiteDatabase db = this.getReadableDatabase();

        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        Cursor res = db.rawQuery("select * from " + SPORTS_TABLE_NAME + " where " +
                SPORTS_COLUMN_NAMEASCII + " like \'%" + text + "%\'", null);
        res.moveToFirst();
        return res;
    }

    /**
     * Zjistí doporučené proteiny
     * @return doporučené proteiny
     */
    public int getUserProts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + SETTINGS_COLUMN_PROTS + " from " + SETTINGS_TABLE_NAME, null);
        res.moveToFirst();
        if (res.getCount() > 0)
            return (Integer.parseInt(res.getString(0)));
        return -1;
    }

    /**
     * Zjistí doporučené sacharidy
     * @return doporučené sacharidy
     */
    public int getUserCarbs()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + SETTINGS_COLUMN_CARBS + " from " + SETTINGS_TABLE_NAME, null);
        res.moveToFirst();
        if(res.getCount()>0)
                return(Integer.parseInt(res.getString(0)));
        return-1;
    }

    /**
     * Zjistí doporučené tuky
     * @return doporučené tuky
     */
    public int getUserFats()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + SETTINGS_COLUMN_FATS + " from " + SETTINGS_TABLE_NAME, null);
        res.moveToFirst();
        if(res.getCount()>0)
            return(Integer.parseInt(res.getString(0)));
        return-1;
    }

    /**
     * Zjistí váhu uživatele
     * @return váha uživatele
     */
    public int getUserWeight()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + SETTINGS_COLUMN_WEIGHT + " from " + SETTINGS_TABLE_NAME, null);
        res.moveToFirst();
        if(res.getCount()>0)
            return(Integer.parseInt(res.getString(0)));
        return-1;
    }

    /**
     * Zjistí denní energetický výdej uživatele
     * @return denní energetický výdej uživatele
     */
    public int getUserKcalOutput()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + SETTINGS_COLUMN_DAILY_KCAl + " from " + SETTINGS_TABLE_NAME, null);
        res.moveToFirst();
        if(res.getCount()>0)
            return(Integer.parseInt(res.getString(0)));
        return-1;
    }

    /**
     * Zjistí denní cílový poměr energetických výdajů a příjmů uživatele
     * @return denní cílový poměr energetických výdajů a příjmů uživatele
     */
    public int getUserEnergy()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + SETTINGS_COLUMN_ENERGY + " from " + SETTINGS_TABLE_NAME, null);
        res.moveToFirst();
        if(res.getCount()>0)
            return(Integer.parseInt(res.getString(0)));
        return 0;
    }

    /**
     * Nastaví tabulku settings, převážně denní doporučené příjmy
     * @param prots Proteiny
     * @param carbs Sacharidy
     * @param fats Tuky
     * @param energy Cílový energetický poměr
     * @param dailykcal Denní energetický výdej
     * @param weight Váha
     * @return Vrátí jestli proběhlo úspěšně
     */
    public boolean setSettings(int prots, int carbs, int fats, int energy, int dailykcal, int weight)
    {
        resetSettings();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTINGS_COLUMN_PROTS, prots);
        contentValues.put(SETTINGS_COLUMN_CARBS, carbs);
        contentValues.put(SETTINGS_COLUMN_FATS, fats);
        contentValues.put(SETTINGS_COLUMN_ENERGY, energy);
        contentValues.put(SETTINGS_COLUMN_DAILY_KCAl, dailykcal);
        contentValues.put(SETTINGS_COLUMN_WEIGHT, weight);
        try {
            db.insertOrThrow(SETTINGS_TABLE_NAME, null, contentValues);
        } catch (SQLiteException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Vygeneruje základní databázi jídel
     */
    public void generateFood()
    {
        if(Locale.getDefault().toString().equals("cs_CZ")) {
            insertMeal("Rohlík", 9, 57, 2, 290, 45);
            insertMeal("Chléb", 8, 45, 1, 243, 50);
            insertMeal("Sýr", 27, 2, 16, 262, 16);
            insertMeal("Šunka", 17, 0, 10, 166, 10);
            insertMeal("Bramborák", 4, 19, 3, 116, 40);
            insertMeal("Knedlík houskový", 7, 42, 2, 212, 37);
            insertMeal("Knedlík bramborový", 5, 37, 0, 172, 37);
            insertMeal("Maso kuřecí", 13, 0, 6, 113, 150);
            insertMeal("Maso hovězí", 24, 0, 8, 170, 150);
            insertMeal("Maso vepřové", 18, 0, 18, 237, 150);
            insertMeal("Řízek smažený", 22, 30, 18, 371, 150);
            insertMeal("Steak", 22, 30, 18, 371, 150);
            insertMeal("Tlačenka drůbeží", 18, 0, 12, 181, 150);
            insertMeal("Párek", 13, 2, 24, 263, 35);
            insertMeal("Rýže vařená", 3, 28, 0, 129, 100);
            insertMeal("Rýže dušená", 5, 39, 4, 210, 100);
            insertMeal("Těstoviny", 3, 24, 2, 133, 100);
            insertMeal("Těstoviny s omáčkou", 10, 30, 3, 208, 100);
            insertMeal("Špagety", 3, 28, 1, 143, 100);
            insertMeal("Špagety s omáčkou", 10, 30, 3, 208, 100);
            insertMeal("Brambory", 1, 15, 0, 66, 100);
            insertMeal("Hranolky", 3, 34, 14, 279, 100);
            insertMeal("Krokety bramborové", 2, 23, 18, 262, 100);
            insertMeal("Houska", 8, 60, 1, 143, 50);
            insertMeal("Arašídy", 26, 18, 42, 616, 100);
            insertMeal("Pistácie", 19, 25, 50, 625, 100);
            insertMeal("Mandle", 25, 6, 55, 644, 100);
            insertMeal("Vlašské ořechy", 15, 14, 63, 691, 100);
            insertMeal("Ořechy obecně", 20, 15, 50, 630, 100);
            insertMeal("Paštika", 8, 11, 23, 197, 48);
            insertMeal("Marmeláda", 0, 63, 0, 262, 48);
            insertMeal("Nutela", 6, 57, 31, 540, 15);
            insertMeal("Čokoláda", 6, 58, 30, 532, 100);
            insertMeal("Tatranka", 6, 58, 28, 517, 50);
            insertMeal("Tyčinka", 6, 58, 28, 517, 50);
            insertMeal("Sušenka", 6, 58, 28, 517, 25);
            insertMeal("Cereálie", 10, 64, 14, 436, 100);
            insertMeal("Piškot", 11, 75, 5, 396, 2);
            insertMeal("Tvaroh odtučněný", 12, 4, 0, 67, 250);
            insertMeal("Tvaroh polotučný", 10, 4, 4, 109, 250);
            insertMeal("Tvaroh tučný", 10, 4, 8, 131, 250);
            insertMeal("Mléko nízkotučné", 3, 5, 0, 34, 250);
            insertMeal("Mléko polotučné", 3, 5, 1, 47, 250);
            insertMeal("Mléko plnotučné", 3, 5, 3, 63, 250);
            insertMeal("Vejce", 12, 0, 11, 151, 50);
            insertMeal("Jablko", 0, 13, 0, 57, 100);
            insertMeal("Hruška", 0, 13, 0, 58, 140);
            insertMeal("Banán", 1, 22, 0, 94, 90);
            insertMeal("Jahody", 1, 9, 0, 41, 12);
            insertMeal("Maliny", 1, 9, 0, 41, 6);
            insertMeal("Borůvky", 1, 9, 0, 41, 50);
            insertMeal("Pomeranč", 1, 11, 0, 50, 150);
            insertMeal("Mandarinka", 1, 10, 0, 50, 80);
            insertMeal("Broskev", 1, 12, 0, 52, 120);
            insertMeal("Meruňka", 1, 10, 0, 42, 150);
            insertMeal("Nektarinka", 1, 10, 0, 42, 40);
            insertMeal("Třešně", 1, 14, 0, 64, 7);
            insertMeal("Višně", 1, 14, 0, 64, 6);
            insertMeal("Švestky", 1, 14, 0, 64, 20);
            insertMeal("Jogurt ochucený", 4, 13, 3, 91, 150);
            insertMeal("Jogurt bílý", 5, 4, 3, 62, 150);
            insertMeal("Rajče", 1, 4, 0, 21, 80);
            insertMeal("Paprika", 1, 5, 0, 28, 130);
            insertMeal("Zelí", 1, 5, 0, 28, 100);
            insertMeal("Salát", 1, 5, 0, 28, 100);
            insertMeal("Mrkev", 1, 7, 0, 35, 60);
            insertMeal("Tuňák konzerva", 1, 7, 0, 185, 120);
            insertMeal("Ryba obecně", 14, 0, 11, 154, 150);
            insertMeal("Ovesná kaše voda", 6, 22, 2, 136, 100);
            insertMeal("Ovesná kaše mléko", 13, 58, 6, 366, 100);
            insertMeal("Slanina", 10, 0, 40, 402, 10);
            insertMeal("Hamburger", 13, 31, 10, 300, 110);
            insertMeal("Cheeseburger", 14, 30, 10, 300, 110);
            insertMeal("Tortilla", 6, 58, 6, 315, 125);
            insertMeal("Tortilla wrap", 8, 12, 6, 138, 200);
            insertMeal("Chicken nugget", 17, 19, 14, 271, 15);
            insertMeal("Kuřecí nugety", 17, 19, 14, 271, 15);
            insertMeal("Brambůrky", 7, 55, 29, 500, 160);
            insertMeal("Kola", 0, 10, 0, 42, 330);
            insertMeal("Coca cola", 0, 10, 0, 42, 330);
            insertMeal("Sladký nápoj", 0, 10, 0, 42, 330);
            insertMeal("Džus", 0, 10, 0, 42, 330);
            insertMeal("Horká čokoláda", 1, 10, 0, 80, 250);
            insertMeal("Kafe", 2, 8, 0, 60, 250);
            insertMeal("Zmrzlina", 2, 25, 5, 180, 120);
            insertMeal("Smetana", 3, 3, 15, 160, 100);
            insertMeal("Puding", 3, 16, 3, 83, 125);
            insertMeal("Pivo", 0, 5, 1, 41, 500);
            insertMeal("Víno", 0, 7, 1, 92, 200);
            insertMeal("Čaj sladký", 0, 3, 0, 10, 300);
            insertMeal("Okurka", 1, 5, 0, 28, 100);
            insertMeal("Vodka", 0, 0, 0, 221, 20);
            insertMeal("Rum", 0, 0, 0, 221, 20);
            insertMeal("Omáčka obecně", 5, 20, 5, 147, 100);
        }
        else
        {
            insertMeal("Roll", 9, 57, 2, 290, 45);
            insertMeal("Bread", 8, 45, 1, 243, 50);
            insertMeal("Cheese", 27, 2, 16, 262, 16);
            insertMeal("Ham", 17, 0, 10, 166, 10);
            insertMeal("Potato pancake", 4, 19, 3, 116, 40);
            insertMeal("Dumpling bun", 7, 42, 2, 212, 37);
            insertMeal("Dumpling potato", 5, 37, 0, 172, 37);
            insertMeal("Chicken meat", 13, 0, 6, 113, 150);
            insertMeal("Beef meat", 24, 0, 8, 170, 150);
            insertMeal("Pork meat", 18, 0, 18, 237, 150);
            insertMeal("Steak", 22, 30, 18, 371, 150);
            insertMeal("Brawn", 18, 0, 12, 181, 150);
            insertMeal("Sausage", 13, 2, 24, 263, 35);
            insertMeal("Rice boiled", 3, 28, 0, 129, 100);
            insertMeal("Rice steamed", 5, 39, 4, 210, 100);
            insertMeal("Pasta", 3, 24, 2, 133, 100);
            insertMeal("Pasta with sauce", 10, 30, 3, 208, 100);
            insertMeal("Spaghetti", 3, 28, 1, 143, 100);
            insertMeal("Spaghetti with sauce", 10, 30, 3, 208, 100);
            insertMeal("Potatoes", 1, 15, 0, 66, 100);
            insertMeal("French fries", 3, 34, 14, 279, 100);
            insertMeal("Croquettes", 2, 23, 18, 262, 100);
            insertMeal("Bun", 8, 60, 1, 143, 50);
            insertMeal("Peanuts", 26, 18, 42, 616, 100);
            insertMeal("Pistachio", 19, 25, 50, 625, 100);
            insertMeal("Almond", 25, 6, 55, 644, 100);
            insertMeal("Walnuts", 15, 14, 63, 691, 100);
            insertMeal("Nuts", 20, 15, 50, 630, 100);
            insertMeal("Pate", 8, 11, 23, 197, 48);
            insertMeal("Jam", 0, 63, 0, 262, 48);
            insertMeal("Nutela", 6, 57, 31, 540, 15);
            insertMeal("Chocolate", 6, 58, 30, 532, 100);
            insertMeal("Chocolate wafer", 6, 58, 28, 517, 50);
            insertMeal("Stamen", 6, 58, 28, 517, 50);
            insertMeal("Cereals", 10, 64, 14, 436, 100);
            insertMeal("Squeak", 11, 75, 5, 396, 2);
            insertMeal("Cottage cheese nonfat", 12, 4, 0, 67, 250);
            insertMeal("Cottage cheese", 10, 4, 4, 109, 250);
            insertMeal("Cottage cheese fat", 10, 4, 8, 131, 250);
            insertMeal("Milk nonfat", 3, 5, 0, 34, 250);
            insertMeal("Milk ", 3, 5, 1, 47, 250);
            insertMeal("Milk fat", 3, 5, 3, 63, 250);
            insertMeal("Eggs", 12, 0, 11, 151, 50);
            insertMeal("Apple", 0, 13, 0, 57, 100);
            insertMeal("Pear", 0, 13, 0, 58, 140);
            insertMeal("Banana", 1, 22, 0, 94, 90);
            insertMeal("Strawberries", 1, 9, 0, 41, 12);
            insertMeal("Raspberries", 1, 9, 0, 41, 6);
            insertMeal("Blueberries", 1, 9, 0, 41, 50);
            insertMeal("Orange", 1, 11, 0, 50, 150);
            insertMeal("Tangerine", 1, 10, 0, 50, 80);
            insertMeal("Peach", 1, 12, 0, 52, 120);
            insertMeal("Apricot", 1, 10, 0, 42, 150);
            insertMeal("Nectarine", 1, 10, 0, 42, 40);
            insertMeal("Cherries", 1, 14, 0, 64, 7);
            insertMeal("Plums", 1, 14, 0, 64, 20);
            insertMeal("Yogurt flavored", 4, 13, 3, 91, 150);
            insertMeal("Yogurt white", 5, 4, 3, 62, 150);
            insertMeal("Tomatoes", 1, 4, 0, 21, 80);
            insertMeal("Pepper", 1, 5, 0, 28, 130);
            insertMeal("Cabbage", 1, 5, 0, 28, 100);
            insertMeal("Salad", 1, 5, 0, 28, 100);
            insertMeal("Carrot", 1, 7, 0, 35, 60);
            insertMeal("Tuna", 1, 7, 0, 185, 120);
            insertMeal("Fish", 14, 0, 11, 154, 150);
            insertMeal("Salmon", 14, 0, 11, 154, 150);
            insertMeal("Mackerel", 14, 0, 11, 154, 150);
            insertMeal("Porridge with water", 6, 22, 2, 136, 100);
            insertMeal("Porridge with milk", 13, 58, 6, 366, 100);
            insertMeal("Bacon", 10, 0, 40, 402, 10);
            insertMeal("Hamburger", 13, 31, 10, 300, 110);
            insertMeal("Cheeseburger", 14, 30, 10, 300, 110);
            insertMeal("Tortilla", 6, 58, 6, 315, 125);
            insertMeal("Tortilla wrap", 8, 12, 6, 138, 200);
            insertMeal("Chicken nugget", 17, 19, 14, 271, 15);
            insertMeal("Chips", 7, 55, 29, 500, 160);
            insertMeal("Cola", 0, 10, 0, 42, 330);
            insertMeal("Sweet drink", 0, 10, 0, 42, 330);
            insertMeal("Juice", 0, 10, 0, 42, 330);
            insertMeal("Hot chocolate", 1, 10, 0, 80, 250);
            insertMeal("Coffee", 2, 8, 0, 60, 250);
            insertMeal("Cookie", 6, 58, 28, 517, 25);
            insertMeal("Ice cream", 2, 25, 5, 180, 120);
            insertMeal("Cream", 3, 3, 15, 160, 100);
            insertMeal("Pudding", 3, 16, 3, 83, 125);
            insertMeal("Beer", 0, 5, 1, 41, 500);
            insertMeal("Wine", 0, 7, 1, 92, 200);
            insertMeal("Sweet tea", 0, 3, 0, 10, 300);
            insertMeal("Okurka", 1, 5, 0, 28, 100);
            insertMeal("Vodka", 0, 0, 0, 221, 20);
            insertMeal("Rum", 0, 0, 0, 221, 20);
            insertMeal("Sauce in general", 5, 20, 5, 147, 100);
        }
    }

    /**
     * Vygeneruje základní databázi sportů
     */
    public void generateSport()
    {
        if(Locale.getDefault().toString().equals("cs_CZ")) {
            insertSport("Chůze pomalá", 1);
            insertSport("Fotbalový zápas", 5);
            insertSport("Plavání", 5);
            insertSport("Maratón", 10);
            insertSport("Běh, průměrný 8km/h", 4);
            insertSport("Běh, rychlý 12km/h", 7);
            insertSport("Hokej", 8);
            insertSport("Posilování - intenzívní", 8);
            insertSport("Posilování - přestávky", 4);
            insertSport("Hokejbal", 7);
            insertSport("Florbal", 5);
            insertSport("Bruslení", 4);
            insertSport("Jízda na kole", 4);
            insertSport("Tenis", 4);
            insertSport("Stolní tenis", 3);
        }
        else
        {
            insertSport("Slow walk", 1);
            insertSport("Fotball match", 5);
            insertSport("Swimming", 5);
            insertSport("Marathon", 10);
            insertSport("Run, slow 8km/h", 4);
            insertSport("Run, fast 12km/h", 7);
            insertSport("Hockey", 8);
            insertSport("Strengthening - intensive", 8);
            insertSport("Strengthening - breaks", 4);
            insertSport("Florbal", 5);
            insertSport("Skating", 4);
            insertSport("Cycling", 4);
            insertSport("Tenis", 4);
            insertSport("Table tennis", 3);
        }
    }
}