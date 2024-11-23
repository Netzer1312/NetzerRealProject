package com.example.first;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
public class DateBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "CustomerDET.db";
    public static final String TABLE_NAME = "StudentTable.db";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "PASSWORD";
    public static final String COL_4 = "EMAIL";
    public static final String COL_5 = "BEST_SCORE";
    public static final String COL_6 = "PHONE_NUMBER";
    //creat the coloms on the database
public DateBaseHelper(Context context){
    super(context, DATABASE_NAME, null, 1);
}
    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("create table " + TABLE_NAME +"  (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,PASSWORD TEXT,BEST_SCORE TEXT,EMAIL TEXT,PHONE_NUMBER TEXT ) ");
    }  ///Creates the  table with columns ID, NAME, PASSWORD, BEST_SCORE, EMAIL, and PHONE_NUMBER.


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        ///Handles the scenario where the database schema changes
    }
    public void insertData(String name,String password,String email,String phone_number){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,password);
        contentValues.put(COL_4,email);
        contentValues.put(COL_6,phone_number);
       db.insert(TABLE_NAME,null,contentValues);
        //insert the data to the database
    }
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
        //the function takes the data
    }
}
