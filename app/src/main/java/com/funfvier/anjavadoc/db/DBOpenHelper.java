package com.funfvier.anjavadoc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by lshi on 29.06.2015.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "docdb";
    private static String DB_PATH = "/data/data/com.funfvier.anjavadoc/databases/";
    private final String TAG = DBOpenHelper.class.getName();
    private final Context context;
    private SQLiteDatabase db;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    public synchronized void createDatabase() throws IOException {
        boolean isDbExist = checkDbExist();

        if (!isDbExist) {
            this.getReadableDatabase();
            copyDatabase();
        } else {

        }
    }

    public synchronized void openDatabase() throws SQLException {
        File path = new File(DB_PATH, DB_NAME);
        db = SQLiteDatabase.openDatabase(path.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
    }

    private boolean checkDbExist() {
        SQLiteDatabase checkDb = null;

        File path = new File(DB_PATH, DB_NAME);
        try {
            checkDb = SQLiteDatabase.openDatabase(path.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.d(TAG, "Path: " + path + " not exist yet.");
        }

        if (checkDb != null) {
            checkDb.close();
        }

        return checkDb != null;
    }

    private void copyDatabase() throws IOException {
        InputStream is = context.getAssets().open(DB_NAME);
        File path = new File(DB_PATH, DB_NAME);
        OutputStream os = new FileOutputStream(path);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        os.flush();
        os.close();
        is.close();
    }

    @Override
    public synchronized void close() {
        if (db != null) {
            db.close();
        }

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
