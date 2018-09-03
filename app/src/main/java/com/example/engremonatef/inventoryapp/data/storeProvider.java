package com.example.engremonatef.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.engremonatef.inventoryapp.data.storeContract.storeEntry;

/**
 * Created by Eng.Remon Atef on 8/1/2017.
 */

public class storeProvider extends ContentProvider {
    private storeDbHelper mDbHelper;
    private static final int STORE = 100;
    private static final int STORE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(storeContract.CONTENT_AUTHORITY, storeContract.PATH_STORE, STORE);
        sUriMatcher.addURI(storeContract.CONTENT_AUTHORITY, storeContract.PATH_STORE + "/#", STORE_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new storeDbHelper(getContext() , 2);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STORE:
                cursor = database.query(storeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

            default:
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STORE:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(storeEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e("TEST", "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        switch (match) {
            case STORE:
                int d = database.delete(storeEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return d;
            case STORE_ID:
                selection = storeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowD = database.delete(storeEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return rowD;
            default:
                throw new IllegalArgumentException("Deletion is not supported " + uri);
        }
    }

    private int updateStore(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        int d = database.update(storeEntry.TABLE_NAME , values , selection , selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return d;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORE:
                return updateStore(uri, contentValues, selection, selectionArgs);
            case STORE_ID:
                selection = storeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
               // Log.d("TEST" , selection + " selec");
                return updateStore(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
}
