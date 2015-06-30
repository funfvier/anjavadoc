package com.funfvier.anjavadoc.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.funfvier.anjavadoc.entity.EClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lshi on 30.06.2015.
 */
public class ClassDao {
    private final static String TAG = ClassDao.class.getName();
    private final SQLiteOpenHelper dbHelper;

    public ClassDao(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public List<EClass> getByPackageId(int packageId) {
        if (dbHelper == null) {
            Log.d(TAG, "getAll dbHelper is null");
            return null;
        }
        String sql = "select _id, name, desc_short, desc_long, package_id from jd_classes where package_id = " + packageId;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        List<EClass> classes = new ArrayList<>();
        while(cursor.moveToNext()) {
            EClass c = new EClass();
            c.setId(cursor.getInt(0));
            c.setName(cursor.getString(1));
            c.setDescriptionShort(cursor.getString(2));
            c.setDescriptionLong(cursor.getString(3));
            c.setPackageId(cursor.getInt(4));
            classes.add(c);
        }
        return classes;
    }
}
