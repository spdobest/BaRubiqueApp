package com.bestdealfinance.bdfpartner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by disha on 13/2/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rubique.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PRODUCT_TYPE = "product_type";
    public static final String PT_ID = "pt_id";
    public static final String PRODUCT_TYPE_ID = "product_type_id";
    public static final String PRODUCT_TYPE_NAME = "product_type_name";

    public static final String TABLE_PRODUCT = "product";
    public static final String P_ID = "p_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_NAME = "product_name";

    private static final String DATABASE_PRODUCT_TYPE = "create table "
            + TABLE_PRODUCT_TYPE + "(" + PT_ID
            + " integer primary key autoincrement, " + PRODUCT_TYPE_ID
            + " text, "+PRODUCT_TYPE_NAME+" text);";

    private static final String DATABASE_PRODUCT = "create table "
            + TABLE_PRODUCT + "(" + P_ID
            + " integer primary key autoincrement, " + PRODUCT_ID
            + " text, "+PRODUCT_NAME+" text, "+PRODUCT_TYPE_ID+" text);";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_PRODUCT_TYPE);
        db.execSQL(DATABASE_PRODUCT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        onCreate(db);
    }

    public void deleteTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        onCreate(db);
    }

    public String insertProductType(String pt_id, String pt_name){
        String  str_id = "";
        SQLiteDatabase database = this.getWritableDatabase();

        String query = "select * from "+TABLE_PRODUCT_TYPE+" where "+PRODUCT_TYPE_NAME+" = '"+pt_name+"'";// & "+PRODUCT_TYPE_ID+" = "+pt_id;
        Cursor cs = database.rawQuery(query, null);

            if (cs.getCount()>0) {
                return "";
            } else {
                ContentValues values = new ContentValues();
                values.put(PRODUCT_TYPE_ID, pt_id);
                values.put(PRODUCT_TYPE_NAME, pt_name);
                long id = database.insert(TABLE_PRODUCT_TYPE, null, values);
                str_id = String.valueOf(id);
                if (!str_id.equals("")) {
                    return "Success";
                } else {
                    return "";
                }
            }
    }


    public String insertProductByProductType(String p_id, String p_name, String pt_id){
        String  str_id = "";
        SQLiteDatabase database = this.getWritableDatabase();

//        if(p_name.contains(""))
        p_name = p_name.replace("'","''");

        String query = "select * from "+TABLE_PRODUCT+" where "+PRODUCT_NAME+" = '"+p_name+"' and "+PRODUCT_TYPE_ID+" = '"+pt_id+"'";
        Cursor cs = database.rawQuery(query, null);

        if (cs.getCount()>0) {
            return "";
        } else {
            ContentValues values = new ContentValues();
            values.put(PRODUCT_ID, p_id);
            values.put(PRODUCT_NAME, p_name);
            values.put(PRODUCT_TYPE_ID, pt_id);
            long id = database.insert(TABLE_PRODUCT, null, values);
            str_id = String.valueOf(id);
            if (!str_id.equals("")) {
                return "Success";
            } else {
                return "";
            }
        }
    }

    public ArrayList<HashMap<String, String>> getAllProductName(){
        ArrayList<HashMap<String, String>> val = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase database = this.getWritableDatabase();

        String query = "select * from "+TABLE_PRODUCT_TYPE;
        Cursor cs = database.rawQuery(query, null);

        while(cs.moveToNext()){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id",cs.getString(1));
            map.put("name", cs.getString(2));
            val.add(map);
        }

        return val;
    }

    public ArrayList<HashMap<String, String>> getProductByProductType(String str_id){
        ArrayList<HashMap<String, String>> val = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase database = this.getWritableDatabase();

        String query = "select * from "+TABLE_PRODUCT+" where "+PRODUCT_TYPE_ID+" = '"+str_id+"'";
        Cursor cs = database.rawQuery(query, null);

        while(cs.moveToNext()){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id",cs.getString(1));
            map.put("name", cs.getString(2));
            val.add(map);
        }

        return val;
    }

    public int getProductTypeID(String str_product_type) {
        String id = "";
        SQLiteDatabase database = this.getWritableDatabase();

        String query = "select "+PRODUCT_TYPE_ID+" from "+TABLE_PRODUCT_TYPE+" where "+PRODUCT_TYPE_NAME+" = '"+str_product_type+"'";
        Cursor cs = database.rawQuery(query, null);
        while(cs.moveToNext()){
            id = cs.getString(0);
        }
        if(!id.equals("")){
            return Integer.parseInt(id);
        } else {
            return 0;
        }
    }

    public int getProductID(String str_product) {
        String id = "";
        SQLiteDatabase database = this.getWritableDatabase();

        String query = "select "+PRODUCT_ID+" from "+TABLE_PRODUCT+" where "+PRODUCT_NAME+" = '"+str_product+"'";
        Cursor cs = database.rawQuery(query, null);
        while(cs.moveToNext()){
            id = cs.getString(0);
        }
        if(!id.equals("")){
            return Integer.parseInt(id);
        } else {
            return 0;
        }
    }
}
