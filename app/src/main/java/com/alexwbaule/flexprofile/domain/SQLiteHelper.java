package com.alexwbaule.flexprofile.domain;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.tasks.PopfromAssets;

/**
 * Created by alex on 15/07/14.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "carsdatabase";
    private static final int DATABASE_7_VERSION = 7;
    private static final int DATABASE_8_VERSION = 8;
    private static final int DATABASE_9_VERSION = 9;
    private static final int DATABASE_10_VERSION = 10;
    private static final int DATABASE_11_VERSION = 11;
    private static final int DATABASE_12_VERSION = 12;
    private static final int DATABASE_13_VERSION = 13;
    private static final int DATABASE_14_VERSION = 14;
    private static final int DATABASE_15_VERSION = 15;
    private static final int DATABASE_16_VERSION = 16;
    private static final int DATABASE_17_VERSION = 17;
    private static final int DATABASE_18_VERSION = 18;
    private static final int DATABASE_19_VERSION = 19;
    private static final int DATABASE_20_VERSION = 20;
    private static final int DATABASE_21_VERSION = 20;
    private static final int DATABASE_22_VERSION = 22;
    private static final int DATABASE_23_VERSION = 23;
    private static final int DATABASE_24_VERSION = 24;
    private static final int DATABASE_VERSION = DATABASE_24_VERSION;
    private Context cc;

    public static final String KEY_ROWID = "_id";
    public static final String KEY_ONLY_YEAR = "only_year";
    public static final String KEY_ONLY_MONTH = "only_month";
    public static final String KEY_ONLY_DAY = "only_day";


    public static final String KEY_NAME = "profile_name";
    public static final String KEY_TQ = "tanque_comb";
    public static final String KEY_TQ_GNV = "tanque_gnv";
    public static final String KEY_ICON_ID = "icon_id";
    public static final String KEY_CAR_PHOTO = "photo";

    public static final String KEY_CAR_ID = "car_id";
    public static final String KEY_COMB_ID = "comb_id";
    public static final String KEY_CONSUMO = "consumo";

    public static final String KEY_COMBUSTIVEL = "combustivel";
    public static final String KEY_LETTER = "letter";
    public static final String KEY_COLOR = "color";


    public static final String KEY_GASOLINA = "gasolina";
    public static final String KEY_ETANOL = "etanol";
    public static final String KEY_GNV = "gnv";
    public static final String KEY_DIESEL = "diesel";

    public static final String DATABASE_TABLE = "cars";
    public static final String DATABASE_COMSUMOS = "consumos";
    public static final String DATABASE_COMBUSTIVES = "combustiveis";
    public static final String DATABASE_HISTORY = "history";
    public static final String DATABASE_OIL = "oilhistory";
    public static final String DATABASE_CAR_MODELS = "cars_model";

    public static final String HIST_OIL_KM = "km";
    public static final String HIST_OIL_DATE = "created_date";
    public static final String HIST_OIL_NEXT_KM = "next_km";
    public static final String HIST_OIL_LTS = "litros";
    public static final String HIST_OIL_PRICE = "price_lt";
    public static final String HIST_OIL_FILTER = "has_filter";
    public static final String HIST_OIL_NAME = "oil_name";

    public static final String DATABASE_MAINTENANCE_CATEG = "maintenance_catg";
    public static final String MAINTENANCE_CATEG_NAME = "name";

    public static final String DATABASE_MAINTENANCE = "maintenance";
    public static final String MAINTENANCE_ID = "_id";
    public static final String MAINTENANCE_NAME = "company_name";
    public static final String MAINTENANCE_DATE = "created_date";
    public static final String MAINTENANCE_TIME = "created_time";
    public static final String MAINTENANCE_KM = "km";
    public static final String MAINTENANCE_OBS = "obs";
    public static final String MAINTENANCE_PLACEID = "place_id";
    public static final String MAINTENANCE_CARID = "car_id";


    public static final String DATABASE_MAINTENANCE_PARTS = "maintenance_parts";
    public static final String MAINTENANCE_PARTS_NAME = "name";
    public static final String MAINTENANCE_PARTS_NAME_RAW = "part_name";
    public static final String MAINTENANCE_PARTS_ID = "part_id";
    public static final String MAINTENANCE_PARTS_QTE = "qtde";
    public static final String MAINTENANCE_PARTS_PRICE = "price";
    public static final String MAINTENANCE_PARTS_CATGID = "catg_id";
    public static final String MAINTENANCE_PARTS_CATGNAME = "catg_name";
    public static final String MAINTENANCE_PARTS_MAINID = "maintenance_id";

    public static final String DATABASE_PLACE = "place";
    public static final String PLACE_ID = "place_id";
    public static final String PLACE_LAT = "place_lat";
    public static final String PLACE_LONG = "place_long";
    public static final String PLACE_NAME = "place_name";
    public static final String PLACE_ADDR = "place_addr";
    public static final String PLACE_RAT = "place_rat";
    public static final String PLACE_PRICE = "place_price";
    public static final String PLACE_TYPE = "place_type";


    public static final String HIST_KM = "km";
    public static final String HIST_DATE = "created_date";
    public static final String CREATE_DATE = "create_date";

    public static final String HIST_CAR = "car_id";
    public static final String HIST_LTS = "litros";
    public static final String HIST_PRICE = "price_lt";
    public static final String HIST_PARTIAL = "partial";
    public static final String HIST_PROPORTION = "proportion";


    public static final String KEY_TIME = "created_time";

    public static final String ID_DEFAULT = "default_car_id";
    public static final String LAST_SHA256 = "last_downloaded_sha256";


    public static final String MAX_LIMIT_QUERY = "un_limit_select";

    public static final String KEY_HIDE = "hide";
    public static final String MODEL_CAR = "car";

    public static final String LAST_GAS = "price_gasolina";
    public static final String LAST_ALC = "price_Etanol";
    public static final String LAST_GNV = "price_gnv";
    public static final String LAST_DIE = "price_diesel";

    public class DBandFile {
        public final SQLiteDatabase db;
        public final String file;

        DBandFile(SQLiteDatabase a, String b) {
            this.db = a;
            this.file = b;
        }
    }

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table cars " +
            "(_id integer primary key autoincrement, " +
            "profile_name varchar not null, " +
            "icon_id integer not null," +
            "tanque_comb varchar not null," +
            "hide boolean default false," +
            "tanque_gnv varchar not null)";

    private static final String DATABASE_CREATE_COMBUSTIVES = "create table combustiveis " +
            "(_id integer primary key autoincrement, " +
            "combustivel varchar not null," +
            "letter varchar not null default '#', " +
            "color integer not null default 0)";

    private static final String DATABASE_CREATE_COMSUMOS = "create table consumos " +
            "(_id integer primary key autoincrement, " +
            "car_id integer not null," +
            "comb_id integer not null," +
            "consumo float not null)";

    private static final String DATABASE_INSERT_COMBUSTIVES = "insert into combustiveis (combustivel, letter, color) VALUES ('@@@@' , '#' , 0)";

    private static final String INDEX_CREATE = "CREATE UNIQUE INDEX nameunique ON cars (profile_name);";

    private static final String DATABASE_HIST_CREATE = "CREATE TABLE history " +
            "(_id integer primary key autoincrement, " +
            "created_date date default CURRENT_DATE, " +
            "comb_id integer not null, " +
            "km numeric not null, " +
            "litros numeric not null, " +
            "hide boolean default false," +
            "price_lt numeric not null default 0," +
            "place_id integer not null default 0," +
            "partial boolean not null default false," +
            "proportion integer not null default 0," +
            "car_id int not null)";

    private static final String DATABASE_PLACE_CREATE = "CREATE TABLE place " +
            "(_id integer primary key autoincrement, " +
            "place_id varchar not null," +
            "place_lat numeric not null," +
            "place_long numeric not null," +
            "place_name varchar not null," +
            "place_addr varchar not null," +
            "place_rat numeric not null," +
            "place_price numeric not null," +
            "place_type varchar not null)";

    private static final String DATABASE_OIL_CREATE = "CREATE TABLE oilhistory " +
            "(_id integer primary key autoincrement, " +
            "created_date date default CURRENT_DATE, " +
            "km numeric not null, " +
            "next_km numeric not null, " +
            "litros numeric not null, " +
            "has_filter boolean," +
            "oil_name varchar null," +
            "hide boolean default false," +
            "price_lt numeric not null default 0," +
            "place_id integer not null default 0," +
            "car_id int not null)";

    private static final String DATABASE_MAINTENANCE_CREATE = "CREATE TABLE maintenance " +
            "(_id integer primary key autoincrement, " +
            "company_name varchar not null," +
            "created_date date default CURRENT_DATE, " +
            "km numeric not null, " +
            "obs varchar null," +
            "hide boolean default false," +
            "place_id integer not null default 0," +
            "car_id int not null)";

    private static final String DATABASE_MAINTENANCE_PARTS_CREATE = "CREATE TABLE maintenance_parts " +
            "(_id integer primary key autoincrement, " +
            "name varchar not null," +
            "qtde numeric not null default 1," +
            "price numeric not null default 0," +
            "catg_id integer not null default 0," +
            "maintenance_id int not null)";

    private static final String DATABASE_MAINTENANCE_CATG_CREATE = "CREATE TABLE maintenance_catg " +
            "(_id integer primary key autoincrement, " +
            "name varchar null)";


    private static final String DATABASE_MAINTENANCE_LIST_CREATE = "CREATE TABLE maintenance_parts_list " +
            "(_id integer primary key autoincrement, " +
            "part_name varchar not null)";

    private static final String DATABASE_INSERT_MAINTENANCE_CATG = "insert into maintenance_catg (name) VALUES ('@@@@')";

    private static final String DATABASE_MODELS_CREATE = "CREATE TABLE cars_model " +
            "(_id integer primary key autoincrement, " +
            "car varchar)";

    private static final String DATABASE_OILNAMES_CREATE = "CREATE TABLE oil_name " +
            "(_id integer primary key autoincrement, " +
            "oil varchar)";


    private static final String INDEX_HIST_CREATE = "CREATE UNIQUE INDEX histunique ON history (km, car_id);";
    private static final String INDEX_HIST_DATE_CREATE = "CREATE UNIQUE INDEX dateunique ON history (created_date, car_id);";
    private static final String INDEX_OILHIST_DATE_CREATE = "CREATE UNIQUE INDEX dateuniqueoil ON oilhistory (created_date, car_id);";
    private static final String INDEX_MAINTENANCE_DATE_CREATE = "CREATE UNIQUE INDEX dateuniqueman ON maintenance (created_date, company_name ,car_id);";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.cc = context;
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE_COMBUSTIVES);

        database.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.gasolina)));
        database.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.Etanol)));
        database.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.gnv)));
        database.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.diesel)));

        database.execSQL(DATABASE_CREATE_COMSUMOS);
        database.execSQL(DATABASE_MODELS_CREATE);

        database.execSQL(INDEX_CREATE);
        database.execSQL(DATABASE_HIST_CREATE);
        database.execSQL(DATABASE_PLACE_CREATE);
        database.execSQL(DATABASE_OIL_CREATE);
        database.execSQL(INDEX_HIST_CREATE);
        database.execSQL(DATABASE_OILNAMES_CREATE);

        database.execSQL(INDEX_HIST_DATE_CREATE);
        database.execSQL(INDEX_OILHIST_DATE_CREATE);

        database.execSQL(DATABASE_MAINTENANCE_CREATE);
        database.execSQL(DATABASE_MAINTENANCE_PARTS_CREATE);
        database.execSQL(DATABASE_MAINTENANCE_CATG_CREATE);
        database.execSQL(DATABASE_MAINTENANCE_LIST_CREATE);
        database.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.motor)));
        database.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.suspensao)));
        database.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.escapamento)));
        database.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.rodas_pneu)));
        database.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.estofamento)));
        database.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.eletrico)));
        database.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.eletronico)));
        database.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.lataria)));
        database.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.transmissao)));
        database.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.freios)));
        database.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.direcao)));

        database.execSQL(INDEX_MAINTENANCE_DATE_CREATE);

        new DBandFile(database, "insert_models.sql");

        new PopfromAssets().execute(new DBandFile(database, "insert_models.sql"));
        new PopfromAssets().execute(new DBandFile(database, "insert_oils.sql"));
        new PopfromAssets().execute(new DBandFile(database, "insert_pecas.sql"));
        Log.v(getClass().getPackage().getName(), "############################# DATABASE CREATED ##################################");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(getClass().getPackage().getName(), "############################# UPDATE DATABASE OLD=" + oldVersion + " NEW=" + newVersion);

        if (oldVersion < DATABASE_7_VERSION) {
            db.execSQL("alter table cars rename to old_cars");
            db.execSQL("alter table history rename to old_history");
            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE_COMBUSTIVES);
            db.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.gasolina)));
            db.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.Etanol)));
            db.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.gnv)));
            db.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.diesel)));
            db.execSQL(DATABASE_CREATE_COMSUMOS);
            db.execSQL(DATABASE_MODELS_CREATE);
            db.execSQL(DATABASE_PLACE_CREATE);
            db.execSQL(DATABASE_OIL_CREATE);
            db.execSQL(DATABASE_HIST_CREATE);
            db.execSQL("insert into history select * from old_history");
            db.execSQL("update history set comb_id = 2 where comb_id = 'A'");
            db.execSQL("update history set comb_id = 1 where comb_id = 'G'");
            db.execSQL("insert into cars (_id,profile_name,tanque_comb,tanque_gnv,icon_id) SELECT _id,name,tanque,'0','0' from old_cars");
            db.execSQL("insert into consumos (car_id, comb_id, consumo) select _id, '1', gasolina from old_cars where gasolina not like '0.0'");
            db.execSQL("insert into consumos (car_id, comb_id, consumo) select _id, '2', etanol from old_cars where etanol not like '0.0'");
            db.execSQL("drop table old_cars");
            db.execSQL("drop table old_history");
            db.execSQL("drop index if exists histunique");
            db.execSQL("drop index if exists nameunique");
            db.execSQL(INDEX_CREATE);
            db.execSQL(INDEX_HIST_CREATE);
            new PopfromAssets().execute(new DBandFile(db, "insert_models.sql"));
        }
        if (oldVersion < DATABASE_8_VERSION) {
            db.execSQL("alter table cars add hide boolean default false");
            db.execSQL("alter table history add hide boolean default false");
            db.execSQL("alter table oilhistory add hide boolean default false");
            db.execSQL(DATABASE_OILNAMES_CREATE);
            new PopfromAssets().execute(new DBandFile(db, "insert_oils.sql"));
            db.execSQL("DROP VIEW IF EXISTS view_consumo_history");
        }
        if (oldVersion < DATABASE_9_VERSION) {
            db.execSQL(DATABASE_OILNAMES_CREATE);
            new PopfromAssets().execute(new DBandFile(db, "insert_oils.sql"));
            db.execSQL("DROP VIEW IF EXISTS view_consumo_history");
        }
        if (oldVersion < DATABASE_10_VERSION) {
            Log.d(getClass().getSimpleName(), "##################### Fix Double Values ####################");
            db.execSQL("update consumos set consumo = replace(consumo,',','.')");
            db.execSQL("update history set litros = replace(litros,',','.')");
            db.execSQL("update history set km = replace(km,',','.')");
            db.execSQL("update oilhistory set km = replace(km,',','.')");
            db.execSQL("update oilhistory set next_km = replace(next_km,',','.')");
            db.execSQL("update oilhistory set litros = replace(litros,',','.')");
            db.execSQL("update cars set icon_id = 'ic_spin_none'");
            MeuCarroApplication.getInstance().updatePrices();
        }
        if (oldVersion < DATABASE_11_VERSION) {
            try {
                db.execSQL(INDEX_HIST_DATE_CREATE);
                db.execSQL(INDEX_OILHIST_DATE_CREATE);
            } catch (Exception e) {

            }
        }
        if (oldVersion < DATABASE_12_VERSION) {
            db.execSQL("alter table history add price_lt numeric not null default 0");
            db.execSQL("alter table oilhistory add price_lt numeric not null default 0");
            db.execSQL("DROP TABLE IF EXISTS postos");
            db.execSQL(DATABASE_PLACE_CREATE);
        }
        if (oldVersion < DATABASE_13_VERSION) {
            db.execSQL("update combustiveis  set combustivel = 'Etanol' where combustivel = 'Alcool'");
        }
        if (oldVersion < DATABASE_14_VERSION) {
            Cursor cursor = db.rawQuery("SELECT * FROM history LIMIT 1", null);
            String[] colNames = cursor.getColumnNames();
            boolean rebuild = false;
            for (int i = 0; i < colNames.length; i++) {
                if (colNames[i].equals("place_id")) {
                    Log.d(getClass().getSimpleName(), "TABLE history HAS " + colNames[i] + " DROP and REBUILD");
                    rebuild = true;
                    break;
                }
            }
            if (rebuild) {
                db.execSQL("DROP TABLE history");
                db.execSQL(DATABASE_HIST_CREATE);
            } else {
                db.execSQL("alter table history add place_id integer not null default 0");
            }
            cursor = db.rawQuery("SELECT * FROM oilhistory LIMIT 1", null);
            colNames = cursor.getColumnNames();
            rebuild = false;
            for (int i = 0; i < colNames.length; i++) {
                if (colNames[i].equals("place_id")) {
                    Log.d(getClass().getSimpleName(), "TABLE oilhistory HAS " + colNames[i] + " DROP and REBUILD");
                    rebuild = true;
                    break;
                }
            }
            if (rebuild) {
                db.execSQL("DROP TABLE oilhistory");
                db.execSQL(DATABASE_OIL_CREATE);
            } else {
                db.execSQL("alter table oilhistory add place_id integer not null default 0");
            }
        }
        if (oldVersion < DATABASE_15_VERSION) {
            Log.d(getClass().getSimpleName(), "##################### Fix Double Values AGAIN ####################");
            db.execSQL("update consumos set consumo = replace(consumo,',','.')");
            db.execSQL("update history set litros = replace(litros,',','.')");
            db.execSQL("update history set km = replace(km,',','.')");
            db.execSQL("update oilhistory set km = replace(km,',','.')");
            db.execSQL("update oilhistory set next_km = replace(next_km,',','.')");
            db.execSQL("update oilhistory set litros = replace(litros,',','.')");
        }
        if (oldVersion < DATABASE_16_VERSION) {
            Log.d(getClass().getSimpleName(), "##################### INSERTING MAINTANANCE TABLES ####################");
            db.execSQL(DATABASE_MAINTENANCE_CREATE);
            db.execSQL(DATABASE_MAINTENANCE_PARTS_CREATE);
            db.execSQL(DATABASE_MAINTENANCE_CATG_CREATE);
            db.execSQL(DATABASE_MAINTENANCE_LIST_CREATE);

            db.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.motor)));
            db.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.suspensao)));
            db.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.escapamento)));
            db.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.rodas_pneu)));
            db.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.estofamento)));
            db.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.eletrico)));
            db.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.eletronico)));
            db.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.lataria)));

            db.execSQL("UPDATE combustiveis set combustivel = '" + cc.getApplicationContext().getString(R.string.gasolina) + "' where _id = 1");
            db.execSQL("UPDATE combustiveis set combustivel = '" + cc.getApplicationContext().getString(R.string.Etanol) + "' where _id = 2");
            db.execSQL("UPDATE combustiveis set combustivel = '" + cc.getApplicationContext().getString(R.string.gnv) + "' where _id = 3");
            db.execSQL("UPDATE combustiveis set combustivel = '" + cc.getApplicationContext().getString(R.string.diesel) + "' where _id = 4");

            new PopfromAssets().execute(new DBandFile(db, "insert_pecas.sql"));

            db.execSQL(INDEX_MAINTENANCE_DATE_CREATE);
        }
        if (oldVersion < DATABASE_17_VERSION) {
            db.execSQL("alter table history add partial boolean not null default false");
            db.execSQL("alter table history add proportion integer not null default 0");
        }
        if (oldVersion < DATABASE_18_VERSION) {
            db.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.transmissao)));
        }
        if (oldVersion < DATABASE_19_VERSION) {
            db.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.freios)));
            db.execSQL(DATABASE_INSERT_MAINTENANCE_CATG.replace("@@@@", cc.getApplicationContext().getString(R.string.direcao)));
        }
        if (oldVersion < DATABASE_20_VERSION) {
            db.execSQL("DROP table place");
            db.execSQL(DATABASE_PLACE_CREATE);
        }
        if (oldVersion < DATABASE_22_VERSION) {
            Log.d(getClass().getSimpleName(), "##################### REBUILDING FUEL TABLE ####################");

            db.execSQL("DROP TABLE combustiveis");
            db.execSQL(DATABASE_CREATE_COMBUSTIVES);
            db.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.gasolina)));
            db.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.Etanol)));
            db.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.gnv)));
            db.execSQL(DATABASE_INSERT_COMBUSTIVES.replace("@@@@", cc.getApplicationContext().getString(R.string.diesel)));
        }
        if (oldVersion < DATABASE_23_VERSION) {
            db.execSQL("update place set place_type = 49 WHERE place_type = 41");
            db.execSQL("update place set place_type = 24 WHERE place_type = 19");
            db.execSQL("update place set place_type = 25 WHERE place_type = 20");
        }
    }
}
