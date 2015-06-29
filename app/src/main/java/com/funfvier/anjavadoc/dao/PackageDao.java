package com.funfvier.anjavadoc.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.funfvier.anjavadoc.entity.EPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lshi on 29.06.2015.
 */
public class PackageDao {
    private final static String TAG = PackageDao.class.getName();
    private final SQLiteOpenHelper dbHelper;

    public PackageDao(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public List<EPackage> getAll() {
        if (dbHelper == null) {
            Log.d(TAG, "getAll dbHelper is null");
            return null;
        }
        String sql = "select _id, name, description, full_description from jd_packages order by name";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        List<EPackage> packages = new ArrayList<>();
        while (cursor.moveToNext()) {
            EPackage p = new EPackage();
            p.setId(cursor.getInt(0));
            p.setName(cursor.getString(1));
            p.setDescription(cursor.getString(2));
            p.setFullDescription(cursor.getString(3));
            packages.add(p);
        }
        return packages;
    }
}
