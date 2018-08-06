package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.item.ItemCategory;
import com.example.item.ItemChannel;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "liveTv.db";

    public static final String TABLE_FAVOURITE_NAME = "favourite";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";

    public static final String TABLE_FAVOURITE_CHANNLE = "favourite_channel";
    public static final String KEY_CHANNLE_ID = "channle_id";
    public static final String KEY_CHANNLE_URL = "channle_url";
    public static final String KEY_CHANNLE_IMAGE = "channle_image";
    public static final String KEY_CHANNLE_NAME = "channle_name";
    public static final String KEY_CHANNLE_DESC = "channle_desc";

    public static final String TABLE_RECENT_CHANNEL = "recent_channel";
    public static final String KEY_RECENT_ID = "recent_id";
    public static final String KEY_RECENT_URL = "recent_url";
    public static final String KEY_RECENT_IMAGE = "recent_image";
    public static final String KEY_RECENT_NAME = "recent_name";
    public static final String KEY_RECENT_TIMESTAMP = "recent_timestamp";
    public static final String KEY_RECENT_DESC = "recent_desc";

    public static final String TABLE_CATEGORY = "table_category";
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_CATEGORY_NAME = "category_name";
    public static final String KEY_CATEGORY_IMAGE = "category_image";
    public static final String KEY_CATEGORY_SELECTION = "category_selection";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + TABLE_FAVOURITE_NAME + "("
                + KEY_ID + " INTEGER,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT"
                + ")";
        db.execSQL(CREATE_FAVOURITE_TABLE);

        String CREATE_FAVOURITE_CHANNLE = "CREATE TABLE " + TABLE_FAVOURITE_CHANNLE + "("
                + KEY_CHANNLE_ID + " INTEGER,"
                + KEY_CHANNLE_URL + " TEXT,"
                + KEY_CHANNLE_IMAGE + " TEXT,"
                + KEY_CHANNLE_NAME + " TEXT,"
                + KEY_CHANNLE_DESC + " TEXT" + ")";
        db.execSQL(CREATE_FAVOURITE_CHANNLE);

        String CREATE_RECENT_CHANNLE = "CREATE TABLE " + TABLE_RECENT_CHANNEL + "("
                + KEY_RECENT_ID + " INTEGER,"
                + KEY_RECENT_URL + " TEXT,"
                + KEY_RECENT_IMAGE + " TEXT,"
                + KEY_RECENT_NAME + " TEXT,"
                + KEY_RECENT_TIMESTAMP + " TEXT,"
                + KEY_RECENT_DESC + " TEXT" + ")";
        db.execSQL(CREATE_RECENT_CHANNLE);


        String CREATE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_CATEGORY_ID + " INTEGER,"
                + KEY_CATEGORY_IMAGE + " TEXT,"
                + KEY_CATEGORY_SELECTION + " TEXT,"
                + KEY_CATEGORY_NAME + " TEXT" + ")";
        db.execSQL(CREATE_CATEGORY);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE_NAME);
        // Create tables again
        onCreate(db);
    }
    public boolean getFavouriteById(String story_id) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{story_id};
        Cursor cursor = db.rawQuery("SELECT id FROM favourite WHERE id=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }
    public boolean getFavouriteChannelById(String channleId) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{channleId};
        Cursor cursor = db.rawQuery("SELECT channle_id FROM favourite_channel WHERE channle_id=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }
    public void removeFavouriteById(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM  favourite " + " WHERE " + KEY_ID + " = " + _id);
        db.close();
    }
    public long addFavourite(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TableName, s1, contentvalues);
    }
    public long addFavouriteChannel(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TableName, s1, contentvalues);
    }
    public ArrayList<ItemChannel> getFavouriteChannle() {
        ArrayList<ItemChannel> channleList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_FAVOURITE_CHANNLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ItemChannel contact = new ItemChannel();
                contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CHANNLE_ID)));
                contact.setChannelUrl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CHANNLE_URL)));
                contact.setImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CHANNLE_IMAGE)));
                contact.setChannelName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CHANNLE_NAME)));
                contact.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CHANNLE_DESC)));


                channleList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return channleList;
    }
    public void removeFavouriteChannleById(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM  favourite_channel " + " WHERE " + KEY_CHANNLE_ID + " = " + _id);
        db.close();
    }

    public long addRecentChannel(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TableName, s1, contentvalues);
    }
    public ArrayList<ItemChannel> getRecentChannle() {
        ArrayList<ItemChannel> channleList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String countRecords = "SELECT COUNT (*)  FROM " + TABLE_RECENT_CHANNEL;
        Cursor cursorCount = db.rawQuery(countRecords,null);
        cursorCount.moveToFirst();
        int count= cursorCount.getInt(0);
        cursorCount.close();

        String selectQuery="";
        if(count > 20){
             selectQuery = "SELECT *  FROM "
                    + TABLE_RECENT_CHANNEL +" ORDER BY "+KEY_RECENT_TIMESTAMP+" DESC LIMIT 20";
        }else {
             selectQuery = "SELECT *  FROM " + TABLE_RECENT_CHANNEL;
        }
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ItemChannel contact = new ItemChannel();
                contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RECENT_ID)));
                contact.setChannelUrl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECENT_URL)));
                contact.setImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECENT_IMAGE)));
                contact.setChannelName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECENT_NAME)));
                contact.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECENT_DESC)));
                channleList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return channleList;
    }
    public boolean getRecentChannelById(String channleId) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{channleId};
        Cursor cursor = db.rawQuery("SELECT recent_id FROM recent_channel WHERE recent_id=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }
    public long addCategory(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TableName, s1, contentvalues);
    }
    public void deleteCategory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_CATEGORY);
        db.close();
    }
    public long updateCategory(boolean flag,int categoryId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CATEGORY_SELECTION, flag);

        String selection = KEY_CATEGORY_ID + " LIKE ?"; // where ID column = rowId (that is, selectionArgs)
        String[] selectionArgs = { String.valueOf(categoryId) };

        long id = db.update(TABLE_CATEGORY, contentValues, selection,
                selectionArgs);
        db.close();
        return id;
    }
    public ArrayList<ItemCategory> getCategory() {
        ArrayList<ItemCategory> categoryList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_CATEGORY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ItemCategory category = new ItemCategory();
                category.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CATEGORY_ID)));
                category.setCategoryImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY_IMAGE)));
                category.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY_NAME)));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categoryList;
    }
    public ArrayList<ItemChannel> getFavourite() {
        ArrayList<ItemChannel> chapterList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_FAVOURITE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ItemChannel contact = new ItemChannel();
                contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                contact.setChannelName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                contact.setImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)));
                chapterList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return chapterList;
    }
}
