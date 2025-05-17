package com.tcn.sdk.springdemo.DBUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tcn.sdk.springdemo.Model.CartListModel;

import java.util.ArrayList;
import java.util.List;

public class CartDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cartManager";
    private static final String TABLE_CARTITEMS = "cartItems";
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
    private static final String KEY_URL = "img";
    private static final String KEY_VOUCHER = "isVoucher";
    private static final String KEY_Prodid = "prodid";

    public CartDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CARTITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TEMP + " INTEGER,"
                + KEY_FPRODID + " TEXT,"
                + KEY_NUMBER + " TEXT,"
                + KEY_TITLE + " TEXT,"
                + KEY_SIZE + " TEXT,"
                + KEY_QTY + " TEXT,"
                + KEY_PRICE + " TEXT,"
                + KEY_SERIAL + " TEXT,"
                + KEY_SERIALCOM + " TEXT,"
                + KEY_Percent + " TEXT,"
                + KEY_URL + " TEXT,"
                + KEY_VOUCHER + " TEXT,"
                + KEY_Prodid + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARTITEMS);

        // Create tables again
        onCreate(db);

    }

    // code to add the new item
    public void addItem(CartListModel cartListModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FPRODID, cartListModel.getFprodid());
        values.put(KEY_TEMP, cartListModel.getTemp());
        values.put(KEY_NUMBER, cartListModel.getItemnumber());
        values.put(KEY_TITLE, cartListModel.getItemname());
        values.put(KEY_SIZE, cartListModel.getItemsize());
        values.put(KEY_QTY, cartListModel.getItemqty());
        values.put(KEY_PRICE, cartListModel.getItemprice());
        values.put(KEY_SERIAL, cartListModel.getSerial_port());
        values.put(KEY_SERIALCOM, cartListModel.getSerial_port_com());
        values.put(KEY_Percent, cartListModel.getRrp_percent());
        values.put(KEY_URL, cartListModel.getImg());
        values.put(KEY_VOUCHER, cartListModel.getVoucher());
        values.put(KEY_Prodid, cartListModel.getProdid());

        // Inserting Row
        db.insert(TABLE_CARTITEMS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }


    // code to get the single item
    public CartListModel getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CARTITEMS, new String[]{KEY_ID, KEY_TEMP, KEY_FPRODID,
                        KEY_NUMBER, KEY_TITLE, KEY_SIZE, KEY_QTY, KEY_PRICE, KEY_SERIAL, KEY_SERIALCOM, KEY_Percent, KEY_URL, KEY_VOUCHER, KEY_Prodid}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        CartListModel cartListModel = new CartListModel(
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
                cursor.getString(11),
                cursor.getString(12),
                cursor.getString(13));
        // return cartListModel
        return cartListModel;
    }


    // code to get all items in a list view
    public List<CartListModel> getAllItems() {
        List<CartListModel> cartListModelArrayList = new ArrayList<CartListModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CARTITEMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CartListModel cartListModel = new CartListModel();
                cartListModel.setId(Integer.parseInt(cursor.getString(0)));
                cartListModel.setTemp(Integer.parseInt(cursor.getString(1)));
                cartListModel.setFprodid(Integer.parseInt(cursor.getString(2)));
                cartListModel.setItemnumber(cursor.getString(3));
                cartListModel.setItemname(cursor.getString(4));
                cartListModel.setItemsize(cursor.getString(5));
                cartListModel.setItemqty(cursor.getString(6));
                cartListModel.setItemprice(cursor.getString(7));
                cartListModel.setSerial_port(cursor.getString(8));
                cartListModel.setSerial_port_com(cursor.getString(9));
                cartListModel.setRrp_percent(cursor.getString(10));
                cartListModel.setImg(cursor.getString(11));
                cartListModel.setVoucher(cursor.getString(12));
                cartListModel.setProdid(cursor.getString(13));

                // Adding items to list
                cartListModelArrayList.add(cartListModel);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        // return contact list
        return cartListModelArrayList;
    }

    // code to update the single item
    public int updateitem(CartListModel cartListModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FPRODID, cartListModel.getFprodid());
        values.put(KEY_TEMP, cartListModel.getTemp());
        values.put(KEY_NUMBER, cartListModel.getItemnumber());
        values.put(KEY_TITLE, cartListModel.getItemname());
        values.put(KEY_SIZE, cartListModel.getItemsize());
        values.put(KEY_QTY, cartListModel.getItemqty());
        values.put(KEY_PRICE, cartListModel.getItemprice());
        values.put(KEY_SERIAL, cartListModel.getSerial_port());
        values.put(KEY_SERIALCOM, cartListModel.getSerial_port_com());
        values.put(KEY_Percent, cartListModel.getRrp_percent());
        values.put(KEY_URL, cartListModel.getImg());
        values.put(KEY_VOUCHER, cartListModel.getVoucher());
        values.put(KEY_Prodid, cartListModel.getProdid());


        // updating row
        return db.update(TABLE_CARTITEMS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(cartListModel.getId())});
    }


    // Deleting single item
    public void deleteitem(CartListModel cartListMode) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_CARTITEMS, KEY_ID + " = ?",
                new String[]{String.valueOf(cartListMode.getId())});
        db.close();
    }

    // Getting item Count
    public int getItemCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CARTITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);


        // return count
        return cursor.getCount();
    }

    // Deleting all item
    public void deleteallitems() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Delete all records of table
        db.execSQL("DELETE FROM " + TABLE_CARTITEMS);

        //For go back free space by shrinking sqlite file
        db.execSQL("VACUUM");
        db.close();
    }


}
