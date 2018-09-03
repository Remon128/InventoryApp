package com.example.engremonatef.inventoryapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.engremonatef.inventoryapp.data.storeContract;
import com.example.engremonatef.inventoryapp.data.storeContract.storeEntry;
import com.example.engremonatef.inventoryapp.data.storeDbHelper;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    StoreCursorAdapter storeCursorAdapter;
    private ArrayList<Integer> cursordata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDataBase();

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        final Intent intent = new Intent(this, AddNewProduct.class);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        ListView productsList = (ListView) findViewById(R.id.list_view_products);
        productsList.setEmptyView(findViewById(R.id.empty_view));

        storeCursorAdapter = new StoreCursorAdapter(this, null);
        cursordata = new ArrayList<>();

        getSupportLoaderManager().initLoader(1, null, this);

        productsList.setAdapter(storeCursorAdapter);


        final Intent toEditActivity = new Intent(this, EditProduct.class);

        productsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImageView imageView = (ImageView) view.findViewById(R.id.list_item_pPicture);
                Bitmap myImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream _bs = new ByteArrayOutputStream();
                myImage.compress(Bitmap.CompressFormat.PNG, 50, _bs);
                toEditActivity.putExtra("byteArray", _bs.toByteArray());

                TextView name = (TextView) view.findViewById(R.id.textView_pName);
                TextView quan = (TextView) view.findViewById(R.id.textView_pQuant);
                TextView price = (TextView) view.findViewById(R.id.textView_pPrice);

                toEditActivity.putExtra("name", name.getText().toString());
                toEditActivity.putExtra("quan", quan.getText().toString());
                toEditActivity.putExtra("price", price.getText().toString());
                Uri uri = storeEntry.CONTENT_URI;
                Uri uriPlusID = ContentUris.withAppendedId(uri, cursordata.get(position));
                toEditActivity.putExtra("currentURI", uriPlusID.toString());
                Log.d("Cursor", cursordata.get(position) + "  ListView current Position");
                startActivity(toEditActivity);
            }
        });

    }

    private void dropDataBase(){
        //Drop table
        storeDbHelper mDbHelper2 = new storeDbHelper(this, 2);
        SQLiteDatabase database2 = mDbHelper2.getReadableDatabase();
        mDbHelper2.onCreate(database2);
    }

    private void createDataBase(){
        //Create new Table
        storeDbHelper mDbHelper1 = new storeDbHelper(this, 1);
        SQLiteDatabase database1 = mDbHelper1.getReadableDatabase();
        mDbHelper1.onCreate(database1);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        cursordata.clear();
        Log.d("TEST", " Requery");
        String[] projection = {
                storeEntry._ID,
                storeEntry.COLUMN_PRODUCT_NAME,
                storeEntry.COLUMN_PRODUCT_PRICE,
                storeEntry.COLUMN_PRODUCT_QUANTITY,
                storeEntry.COLUMN_PRODUCT_IMAGE
        };

        return new CursorLoader(this, storeEntry.CONTENT_URI, projection, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        cursordata.clear();
        Log.d("TEST", cursor.getCount() + "");
        if (cursor.getCount() < 1)
            dropDataBase();
            createDataBase();
        storeCursorAdapter.swapCursor(cursor);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int colID = cursor.getColumnIndex(storeEntry._ID);
            int ID = cursor.getInt(colID);
            Log.d("TEST", ID + " is ID in cursor");
            cursordata.add(ID);
            cursor.moveToNext();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        storeCursorAdapter.swapCursor(null);
    }


    public class StoreCursorAdapter extends CursorAdapter {


        public StoreCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            final View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            int pos = cursor.getPosition();

            return view;
        }


        @Override
        public void bindView(final View view, Context context, Cursor cursor) {

            Button sale = (Button) view.findViewById(R.id.btn_sale);
            sale.setTag(cursor.getPosition());
            Log.d("TEST3", cursor.getPosition() + 1 + "  cursor positions");

            sale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("TEST", "Sale Button is pressed");
                    TextView quantity = (TextView) view.findViewById(R.id.textView_pQuant);
                    String currentQuant = quantity.getText().toString();

                    int quan = Integer.parseInt(currentQuant);
                    if (quan > 0) {
                        quan--;
                    }
                    quantity.setText(quan + "");

                    ContentValues values = new ContentValues();
                    values.put(storeContract.storeEntry.COLUMN_PRODUCT_QUANTITY, quan + "");

                    int id = (int) v.getTag();
                    Log.d("TEST3", id + 1 + "  THis button Tag");

                    Uri uri = storeEntry.CONTENT_URI;
                    Uri uriPlusID = ContentUris.withAppendedId(uri, cursordata.get(id));

                    int x = getContentResolver().update(uriPlusID, values, null, null);

                }
            });

            TextView pName = (TextView) view.findViewById(R.id.textView_pName);
            TextView pQuant = (TextView) view.findViewById(R.id.textView_pQuant);
            TextView pPrice = (TextView) view.findViewById(R.id.textView_pPrice);
            ImageView pImage = (ImageView) view.findViewById(R.id.list_item_pPicture);

            int nameColumnIndex = cursor.getColumnIndex(storeEntry.COLUMN_PRODUCT_NAME);
            int quantColumnIndex = cursor.getColumnIndex(storeEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(storeEntry.COLUMN_PRODUCT_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(storeEntry.COLUMN_PRODUCT_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String quant = cursor.getString(quantColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String image = cursor.getString(imageColumnIndex);

            pName.setText(name);
            pQuant.setText(quant);
            pPrice.setText(price);
            pImage.setImageBitmap(BitmapFactory.decodeFile(image));

        }
    }
}
