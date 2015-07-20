package com.funfvier.anjavadoc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.funfvier.anjavadoc.db.DBOpenHelper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lshi on 19.07.2015.
 */
public class LinkHandler extends AsyncTask<String, Void, String> {
    private Activity activity;
    private Context context;
    private DBOpenHelper dbhelper;
    Integer packageId = null;
    Integer classId = null;
    private static final String TAG = LinkHandler.class.getName();

    public LinkHandler(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @Override
    protected String doInBackground(String... urls) {
        String url = urls[0];
        Holder holder = parse(url);
        find(holder);
        return null;
    }

    public void openLink(String url) {
        Log.d(TAG, "openLink: " + url);
    }

    private void find(Holder holder) {
        if (dbhelper == null) {
            dbhelper = new DBOpenHelper(context);

            try {
                dbhelper.createDatabase();
            } catch (IOException e) {
                Log.e(TAG, "Unable to create db", e);
                throw new Error("Unable to create db");
            }

            try {
                dbhelper.openDatabase();
            } catch (SQLException e) {
                Log.e(TAG, "Unable to open db", e);
                throw new Error("Unable to open db");
            }
        }

        try {
            if (holder.name != null) {
                String sql = "select _id, 'package' as t from jd_packages p where name = ? " +
                        " union " +
                        " select _id, 'class' as t from jd_classes where name = ? ";
                Cursor cursor = dbhelper.getReadableDatabase().rawQuery(sql, new String[]{holder.name, holder.name});
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(0);
                    String type = cursor.getString(1);
                    if ("package".equals(type)) {
                        packageId = id;
                    }
                    if ("class".equals(type)) {
                        classId = id;
                    }
                }
            }
        } finally {
            if (dbhelper != null) {
                dbhelper.close();
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (packageId != null) {
            Intent classesIntent = new Intent(activity, PackageActivity.class);
            classesIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            classesIntent.putExtra(Const.PACKAGE_ID.name(), packageId);
            context.startActivity(classesIntent);
        }

        if (classId != null) {
            Intent classIntent = new Intent(activity, ClassActivity.class);
            classIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            classIntent.putExtra(Const.CLASS_ID.name(), classId);
            context.startActivity(classIntent);
        }
        super.onPostExecute(s);
    }

    private static Holder parse(String url) {
        Scanner sc = new Scanner(url);
        sc.useDelimiter("/");
        StringBuilder sb = new StringBuilder();
        String member = null;
        while (sc.hasNext()) {
            String chunk = sc.next();
            if ("..".equals(chunk)) {

            } else {
                int index = chunk.indexOf(".html");
                if (index >= 0) {
                    String lastName = chunk.substring(0, index);
                    sb.append(lastName);
                    int hashIndex = chunk.indexOf("#");
                    int minIndex = chunk.indexOf("-");
                    if (hashIndex >= 0 && minIndex > hashIndex) {
                        member = chunk.substring(hashIndex + 1, minIndex);
                    }
                } else {
                    sb.append(chunk).append(".");
                }
            }
        }
        Holder holder = new Holder();
        holder.name = sb.toString();
        holder.member = member;
        return holder;
    }

    private static class Holder {
        String name;
        String member;

        @Override
        public String toString() {
            return "Holder{" +
                    "name='" + name + '\'' +
                    ", member='" + member + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        String url = "../../java/io/Reader.html#read-java.nio.CharBuffer-";
        Holder holder = parse(url);
        System.out.println(holder);
    }
}
