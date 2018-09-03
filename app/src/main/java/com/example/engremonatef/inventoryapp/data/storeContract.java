package com.example.engremonatef.inventoryapp.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;

import java.sql.Blob;

/**
 * Created by Eng.Remon Atef on 8/1/2017.
 */

public final class storeContract
{
    public storeContract(){

    }

    public static final String CONTENT_AUTHORITY = "com.example.android.store";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_STORE = "store";

    public static final class storeEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STORE);

        public final static String TABLE_NAME = "store";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_IMAGE = "image";


    }


}
