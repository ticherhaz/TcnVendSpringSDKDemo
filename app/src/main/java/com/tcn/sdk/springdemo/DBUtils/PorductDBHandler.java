package com.tcn.sdk.springdemo.DBUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tcn.sdk.springdemo.Model.ProductDbModel;
import com.tcn.sdk.springdemo.Model.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class PorductDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "productManager";
    private static final String TABLE_PRODUCTS = "products";
    private static final String KEY_ID = "id";
    private static final String KEY_FPRODID = "fprodid";
    private static final String KEY_TEMP = "temp";
    private static final String KEY_TITLE = "itemname";
    private static final String KEY_SIZE = "itemsize";
    private static final String KEY_PRICE = "itemprice";
    private static final String KEY_NUMBER = "itemnumber";
    private static final String KEY_QTY = "itemqty";
    private static final String KEY_SERIAL = "serial_port";
    private static final String KEY_SERIALCOM = "serial_port_com";
    private static final String KEY_Percent = "rrp_percent";
    private static final String KEY_PRODID = "prodid";
    private static final String KEY_URL = "img";


    public PorductDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FPRODID + " TEXT,"
                + KEY_TEMP + " INTEGER,"
                + KEY_NUMBER + " TEXT,"
                + KEY_TITLE + " TEXT,"
                + KEY_SIZE + " TEXT,"
                + KEY_QTY + " TEXT,"
                + KEY_PRICE + " TEXT,"
                + KEY_SERIAL + " TEXT,"
                + KEY_SERIALCOM + " TEXT,"
                + KEY_Percent + " TEXT,"
                + KEY_PRODID + " INTEGER,"
                + KEY_URL + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);

        // Create tables again
        onCreate(db);

    }


    // code to add the new item
    public void addItem(ProductDbModel productDbModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FPRODID, productDbModel.getFprodid());
        values.put(KEY_TEMP, productDbModel.getTemp());
        values.put(KEY_NUMBER, productDbModel.getItemnumber());
        values.put(KEY_TITLE, productDbModel.getItemname());
        values.put(KEY_SIZE, productDbModel.getItemsize());
        values.put(KEY_QTY, productDbModel.getItemqty());
        values.put(KEY_PRICE, productDbModel.getItemprice());
        values.put(KEY_SERIAL, productDbModel.getSerial_port());
        values.put(KEY_SERIALCOM, productDbModel.getSerial_port_com());
        values.put(KEY_Percent, productDbModel.getRrp_percent());
        values.put(KEY_PRODID, productDbModel.getProdid());
        values.put(KEY_URL, productDbModel.getImg());

        // Inserting Row
        db.insert(TABLE_PRODUCTS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to add the new item new
    public void addItemNew(List<ProductModel> productDbModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        ContentValues values = new ContentValues();
        for (int i = 0; i < productDbModel.size(); i++) {
            values.put(KEY_FPRODID, productDbModel.get(i).getId());
            values.put(KEY_TEMP, productDbModel.get(i).getTemperature());
            values.put(KEY_NUMBER, String.valueOf(productDbModel.get(i).getItem_Number()));
            values.put(KEY_TITLE, productDbModel.get(i).getName());
            values.put(KEY_SIZE, productDbModel.get(i).getSize());
            values.put(KEY_QTY, productDbModel.get(i).getQuantity());
            values.put(KEY_PRICE, String.format("%.2f", productDbModel.get(i).getPrice()));
            values.put(KEY_SERIAL, productDbModel.get(i).getSerial_Port());
            values.put(KEY_SERIALCOM, productDbModel.get(i).getSerial_Port_Code());
            values.put(KEY_Percent, String.valueOf(productDbModel.get(i).getRRP_Percent()));
            values.put(KEY_PRODID, productDbModel.get(i).getPID());
            values.put(KEY_URL, productDbModel.get(i).getImage());
            // Inserting Row
            db.insert(TABLE_PRODUCTS, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }


    // code to get the single item
    public ProductDbModel getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUCTS, new String[]{KEY_ID, KEY_FPRODID, KEY_TEMP,
                        KEY_NUMBER, KEY_TITLE, KEY_SIZE, KEY_QTY, KEY_PRICE, KEY_SERIAL, KEY_SERIALCOM, KEY_Percent, KEY_PRODID, KEY_URL}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ProductDbModel productDbModel = new ProductDbModel(
                Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)),
                Integer.parseInt(cursor.getString(2)),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getString(9),
                cursor.getString(10),
                Integer.parseInt(cursor.getString(11)),
                cursor.getString(12));
        // return cartListModel
        return productDbModel;
    }


    // code to get all items in a list view
    public List<ProductDbModel> getAllItems() {
        List<ProductDbModel> productDbModelArrayList = new ArrayList<ProductDbModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProductDbModel cartListModel = new ProductDbModel();
                cartListModel.setId(Integer.parseInt(cursor.getString(0)));
                cartListModel.setFprodid(Integer.parseInt(cursor.getString(1)));
                cartListModel.setTemp(Integer.parseInt(cursor.getString(2)));
                cartListModel.setItemnumber(cursor.getString(3));
                cartListModel.setItemname(cursor.getString(4));
                cartListModel.setItemsize(cursor.getString(5));
                cartListModel.setItemqty(cursor.getString(6));
                cartListModel.setItemprice(cursor.getString(7));
                cartListModel.setSerial_port(cursor.getString(8));
                cartListModel.setSerial_port_com(cursor.getString(9));
                cartListModel.setRrp_percent(cursor.getString(10));
                cartListModel.setProdid(Integer.parseInt(cursor.getString(11)));
                cartListModel.setImg(cursor.getString(12));

                // Adding items to list
                productDbModelArrayList.add(cartListModel);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        // return contact list
        return productDbModelArrayList;
    }

    // code to update the single item
    public int updateitem(ProductDbModel cartListModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_FPRODID, cartListModel.getFprodid());
        values.put(KEY_NUMBER, cartListModel.getItemnumber());
        values.put(KEY_TITLE, cartListModel.getItemname());
        values.put(KEY_SIZE, cartListModel.getItemsize());
        values.put(KEY_QTY, cartListModel.getItemqty());
        values.put(KEY_PRICE, cartListModel.getItemprice());
        values.put(KEY_SERIAL, cartListModel.getSerial_port());
        values.put(KEY_SERIALCOM, cartListModel.getSerial_port_com());
        values.put(KEY_Percent, cartListModel.getRrp_percent());
        values.put(KEY_PRODID, cartListModel.getProdid());
        values.put(KEY_URL, cartListModel.getImg());


        // updating row
        return db.update(TABLE_PRODUCTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(cartListModel.getId())});
    }

    // Deleting single item
    public void deleteitem(ProductDbModel cartListMode) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PRODUCTS, KEY_ID + " = ?",
                new String[]{String.valueOf(cartListMode.getId())});
        db.close();
    }

    // Getting item Count
    public int getItemCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);


        // return count
        return cursor.getCount();
    }

    // Deleting all item
    public void deleteallitems() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Delete all records of table
        db.execSQL("DELETE FROM " + TABLE_PRODUCTS);

        //For go back free space by shrinking sqlite file
        db.execSQL("VACUUM");
        db.close();
    }


}
