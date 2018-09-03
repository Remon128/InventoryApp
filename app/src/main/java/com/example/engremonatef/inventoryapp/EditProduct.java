package com.example.engremonatef.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.engremonatef.inventoryapp.data.storeContract.storeEntry;

import java.util.ArrayList;

public class EditProduct extends AppCompatActivity {

    private Uri currentUri;
    private TextView quant;
    private TextView name;
    private EditText changeNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        changeNum = (EditText) findViewById(R.id.change_num);

        if (getIntent().hasExtra("byteArray")) {
            ImageView _imv = (ImageView) findViewById(R.id.edit_pPicture);
            Bitmap _bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            _imv.setImageBitmap(_bitmap);
        }

        String recvName = getIntent().getStringExtra("name");
        String recvQuant = getIntent().getStringExtra("quan");
        String recvPrice = getIntent().getStringExtra("price");
        String uri = getIntent().getStringExtra("currentURI");
        currentUri = Uri.parse(uri);

        name = (TextView) findViewById(R.id.edit_textView_pName);
        quant = (TextView) findViewById(R.id.edit_textView_pQuant);
        TextView price = (TextView) findViewById(R.id.edit_textView_pPrice);

        name.setText(recvName);
        quant.setText(recvQuant);
        price.setText(recvPrice);
    }

    public void increaseQuant(View v) {

        String currentQuant = quant.getText().toString();
        int quantInt = Integer.parseInt(currentQuant);

        if (!changeNum.getText().toString().matches("")) {
            String stringNum = changeNum.getText().toString();
            int change = Integer.parseInt(stringNum);

            quantInt += change;
            quant.setText(quantInt + "");

            ContentValues values = new ContentValues();
            values.put(storeEntry.COLUMN_PRODUCT_QUANTITY, quantInt + "");

            getContentResolver().update(currentUri, values, null, null);
        } else {
            quantInt++;
            quant.setText(quantInt + "");

            ContentValues values = new ContentValues();
            values.put(storeEntry.COLUMN_PRODUCT_QUANTITY, quantInt + "");

            getContentResolver().update(currentUri, values, null, null);
        }


    }

    public void decreaseQuant(View v) {

        String currentQuant = quant.getText().toString();
        int quantInt = Integer.parseInt(currentQuant);

        if (!changeNum.getText().toString().matches("")) {
            String stringNum = changeNum.getText().toString();
            int change = Integer.parseInt(stringNum);

            if (quantInt > 0)
                quantInt -= change;
            quant.setText(quantInt + "");

            ContentValues values = new ContentValues();
            values.put(storeEntry.COLUMN_PRODUCT_QUANTITY, quantInt + "");

            getContentResolver().update(currentUri, values, null, null);
        } else {
            if (quantInt > 0)
                quantInt--;
            quant.setText(quantInt + "");

            ContentValues values = new ContentValues();
            values.put(storeEntry.COLUMN_PRODUCT_QUANTITY, quantInt + "");

            getContentResolver().update(currentUri, values, null, null);
        }

    }


    public void orderProduct(View v) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT, "body of email");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(EditProduct.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }

    private void makingChoiceDeletion(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deletion_changes_dialog_msg);
        builder.setPositiveButton(R.string.delete, discardButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void deleteRecord(View v) {

        DialogInterface.OnClickListener deleteProduct = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                getContentResolver().delete(currentUri, null, null);
            }
        };
        makingChoiceDeletion(deleteProduct);
    }
}
