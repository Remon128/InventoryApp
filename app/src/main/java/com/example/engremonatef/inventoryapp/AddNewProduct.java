package com.example.engremonatef.inventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.engremonatef.inventoryapp.data.storeContract;

public class AddNewProduct extends AppCompatActivity {

    private String pName;
    private String pQuantity;
    private String pPrice;
    private boolean mProductHasChanged = false;

    private EditText productName;
    private EditText productQuan;
    private EditText productPrice;
    private ImageView productImage;
    private static int RESULT_LOAD_IMAGE = 1;
    private String picturePath = "";
    private Button browse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        productName = (EditText) findViewById(R.id.editT_pName);
        productQuan = (EditText) findViewById(R.id.editT_pQuant);
        productPrice = (EditText) findViewById(R.id.editT_pPrice);
        browse = (Button) findViewById(R.id.btn_browse);
        productImage = (ImageView) findViewById(R.id.product_image);

        productImage.setOnTouchListener(mTouchListener);
        productName.setOnTouchListener(mTouchListener);
        productQuan.setOnTouchListener(mTouchListener);
        productPrice.setOnTouchListener(mTouchListener);
        browse.setOnTouchListener(mTouchListener);


    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            //  Log.d("TEST" , "Da5el fl cond of OnActv Result");
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = (ImageView) findViewById(R.id.product_image);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 225: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, RESULT_LOAD_IMAGE);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, " Sorry,you need to Allow access to your media files",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    private boolean insertProduct() {
        boolean ifDone = false;

        ContentValues values = new ContentValues();
        pName = productName.getText().toString();
        pQuantity = productQuan.getText().toString();
        pPrice = productPrice.getText().toString();

        if (pName.matches("") || pQuantity.matches("") || pPrice.matches("") || picturePath.matches("")) {
            Toast.makeText(this, "Sorry Empty input is not valid!", Toast.LENGTH_LONG).show();
        } else {
            ifDone = true;
            values.put(storeContract.storeEntry.COLUMN_PRODUCT_NAME, pName);
            values.put(storeContract.storeEntry.COLUMN_PRODUCT_PRICE, pPrice);
            values.put(storeContract.storeEntry.COLUMN_PRODUCT_QUANTITY, pQuantity);
            values.put(storeContract.storeEntry.COLUMN_PRODUCT_IMAGE, picturePath);

            Uri newUri = getContentResolver().insert(storeContract.storeEntry.CONTENT_URI, values);

        }
        return ifDone;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Intent intent = new Intent(this, MainActivity.class);
        switch (item.getItemId()) {
            case R.id.save:
                boolean check = insertProduct();
                if (check) {
                    startActivity(intent);
                }
                break;
            case R.id.goback:
                if (!mProductHasChanged) {

                    startActivity(intent);
                    return true;
                } else {
                    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded.
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, navigate to parent activity.
                                    startActivity(intent);
                                }
                            };

                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                }


        }
        return super.onOptionsItemSelected(item);
    }


    public void browsePhoto(View view) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 225);
    }


}
