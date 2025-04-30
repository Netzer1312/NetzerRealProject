package com.example.first;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
public class DateBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "CustomerDET.db";
    public static final String TABLE_NAME = "StudentTable";
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
    public boolean insertData(String name,String password,String email,String phone_number){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,password);
        contentValues.put(COL_4,email);
        contentValues.put(COL_6,phone_number);
        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1; // Returns true if insert was successful, false otherwise
        //insert the data to the database
    }
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
        //the function takes the data
    }
    public boolean setBestScore(String username, int newBestScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5, newBestScore);
        // Update the BEST_SCORE for the given username
        int result = db.update(TABLE_NAME, contentValues, "NAME = ?", new String[]{username});

        // Return true if the update was successful, false otherwise
        return result != -1;
    }
    public int getBestScore(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int bestScore = 0;

        Cursor cursor = db.rawQuery("SELECT BEST_SCORE FROM " + TABLE_NAME + " WHERE NAME=?", new String[]{username});
        if (cursor.moveToFirst()) {
            bestScore = cursor.getInt(0);
        }
        cursor.close();
        return bestScore;
    }
    public boolean authenticateUser(String name, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                        " WHERE NAME = ? AND PASSWORD = ?",
                new String[]{name, password});

        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }
}
