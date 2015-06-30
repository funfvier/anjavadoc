package com.funfvier.anjavadoc.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.funfvier.anjavadoc.entity.EMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lshi on 30.06.2015.
 */
public class MemberDao {
    private final static String TAG = MemberDao.class.getName();
    private final SQLiteOpenHelper dbHelper;

    public MemberDao(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public List<EMember> getByClassId(int classId) {
        if (dbHelper == null) {
            Log.d(TAG, "getAll dbHelper is null");
            return null;
        }
        String sql = "select _id, name, desc_short, desc_long, class_id from jd_members where class_id = " + classId;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        List<EMember> members = new ArrayList<>();
        while(cursor.moveToNext()) {
            EMember m = new EMember();
            m.setId(cursor.getInt(0));
            m.setName(cursor.getString(1));
            m.setShortDescription(cursor.getString(2));
            m.setLongDescription(cursor.getString(3));
            m.setClassId(cursor.getInt(4));
            members.add(m);
        }
        return members;
    }

    public EMember getById(int id) {
        if (dbHelper == null) {
            Log.d(TAG, "getAll dbHelper is null");
            return null;
        }
        String sql = "select _id, name, desc_short, desc_long, class_id from jd_members where _id = " + id;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        if(cursor.moveToNext()) {
            EMember m = new EMember();
            m.setId(cursor.getInt(0));
            m.setName(cursor.getString(1));
            m.setShortDescription(cursor.getString(2));
            m.setLongDescription(cursor.getString(3));
            m.setClassId(cursor.getInt(4));
            return m;
        }
        return null;
    }
}
