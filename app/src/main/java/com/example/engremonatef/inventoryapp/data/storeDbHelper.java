package com.example.engremonatef.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.engremonatef.inventoryapp.data.storeContract.storeEntry;

/**
 * Created by Eng.Remon Atef on 8/1/2017.
 */

public class storeDbHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;
    private int choice;

    public storeDbHelper(Context context , int choice) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.choice = choice;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_STORE_TABLE = "CREATE TABLE IF NOT EXISTS " + storeEntry.TABLE_NAME + "("
                + storeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + storeEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + storeEntry.COLUMN_PRODUCT_PRICE + " TEXT, "
                + storeEntry.COLUMN_PRODUCT_QUANTITY + " TEXT, "
                + storeEntry.COLUMN_PRODUCT_IMAGE + " TEXT )";
        // Log.d("TEST" ,CREATE_STORE_TABLE );

        String Drop_Table = "DROP TABLE " + storeEntry.TABLE_NAME;

        if(choice==1){
            db.execSQL(CREATE_STORE_TABLE);
            Log.d("TEST" , "Table is Created");
        }
        else if(choice==2){
            db.execSQL(Drop_Table);
            Log.d("TEST" , "Table is Deleted");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
