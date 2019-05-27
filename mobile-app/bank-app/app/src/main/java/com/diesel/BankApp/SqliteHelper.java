package com.diesel.BankApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

import android.util.Base64;

import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SqliteHelper extends SQLiteOpenHelper {

    //CLAVE
    private String clave = "Diesel";

    //DATABASE NAME
    public static final String DATABASE_NAME = "BankAppDb";

    //DATABASE VERSION
    public static final int DATABASE_VERSION = 1;

    //TABLE NAME
    public static final String TABLE_USERS = "users";

    //TABLE USERS COLUMNS
    //COLUMN id @primaryKey
    public static final String KEY_ID = "id";

    //COLUMN username
    public static final String KEY_USER_NAME = "username";

    //COLUMN change_date
    public static final String KEY_CHANGE_DATE = "change_date";

    //COLUMN password
    public static final String KEY_PASSWORD = "password";

    //COLUMN account_number
    public static final String KEY_ACCOUNT_NUMBER = "account_number";

    //SQL for creating users table
    public static final String SQL_TABLE_USERS = " CREATE TABLE " + TABLE_USERS
            + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_USER_NAME + " TEXT, "
            + KEY_CHANGE_DATE + " NUMERIC, "
            + KEY_PASSWORD + " TEXT, "
            + KEY_ACCOUNT_NUMBER + " NUMERIC"
            + " ) ";


    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create Table when oncreate gets called
        sqLiteDatabase.execSQL(SQL_TABLE_USERS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //drop table to create new one if database version updated
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_USERS);

        //Create Table again
        sqLiteDatabase.execSQL(SQL_TABLE_USERS);
    }

    //using this method we can add users to user table
    public void addUser(User user) {

        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();

        //Put id in  @values
        values.put(KEY_ID, user.id);

        //Put username in  @values
        values.put(KEY_USER_NAME, user.userName);

        //Put current date in  @values
        values.put(KEY_CHANGE_DATE, String.valueOf(Calendar.getInstance().getTime().getTime()));//valor en milisegundos desde January 1, 1970, 00:00:00 GMT

        //Put password in  @values
        values.put(KEY_PASSWORD, user.password);

        //Put null account_number in  @values
        values.put(KEY_ACCOUNT_NUMBER, "NULL");

        // insert row
        long todo_id = db.insert(TABLE_USERS, null, values);

        db.close();
    }

    public User Authenticate(User user) throws Exception {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " +  TABLE_USERS
                        + " WHERE " + KEY_USER_NAME + " = '" + user.userName + "'", null);

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {

            //if cursor has value then in user database there is user associated with this given username
            User user1 = new User(cursor.getString(cursor.getColumnIndex(KEY_ID)), cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)),
                                cursor.getString(cursor.getColumnIndex(KEY_CHANGE_DATE)),cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)),cursor.getString(cursor.getColumnIndex(KEY_ACCOUNT_NUMBER)));

            cursor.close();
            db.close();
            //Match both passwords check they are same or not
            if (user.password.equalsIgnoreCase(desencriptar(user1.password, clave))){
                return user1;
            }
        }

        //if user password does not matches or there is no record with that email then return @false
        return null;
    }

    public boolean doesExist(String ID, String Username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " +  TABLE_USERS
                        + " WHERE " + KEY_USER_NAME + " = '" + Username
                        + "' OR " + KEY_ID + " = " + ID, null);

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            //if cursor has value then in user database there is user associated with this given id or username so return true
            return true;
        }
        cursor.close();
        db.close();
        //if id and username does not exist return false
        return false;
    }

    public String desencriptar (String datos, String password) throws Exception {
        SecretKeySpec secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] datosDecodificados = Base64.decode(datos, Base64.DEFAULT);
        byte[] datosDesencriptadosByte = cipher.doFinal(datosDecodificados);
        String datosDesencriptadosString = new String(datosDesencriptadosByte);
        return datosDesencriptadosString;
    }
    private SecretKeySpec generateKey(String password) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }
}