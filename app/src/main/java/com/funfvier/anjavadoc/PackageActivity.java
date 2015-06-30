package com.funfvier.anjavadoc;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.funfvier.anjavadoc.adapter.ClassAdapter;
import com.funfvier.anjavadoc.adapter.PackageAdaper;
import com.funfvier.anjavadoc.dao.ClassDao;
import com.funfvier.anjavadoc.db.DBOpenHelper;
import com.funfvier.anjavadoc.entity.EClass;
import com.funfvier.anjavadoc.entity.EPackage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class PackageActivity extends ActionBarActivity {
    private final String TAG = PackageActivity.class.getName();
    private DBOpenHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);

        if(dbhelper == null) {
            dbhelper = new DBOpenHelper(this);

            try{
                dbhelper.createDatabase();
            } catch(IOException e) {
                Log.e(TAG, "Unable to create db", e);
                throw new Error("Unable to create db");
            }

            try{
                dbhelper.openDatabase();
            } catch(SQLException e) {
                Log.e(TAG, "Unable to open db", e);
                throw new Error("Unable to open db");
            }
        }

        int packageId = getIntent().getIntExtra(Const.PACKAGE_ID.name(), -1);

        ClassDao dao = new ClassDao(dbhelper);
        List<EClass> classes = dao.getByPackageId(packageId);
        dbhelper.close();

        final EClass[] packageClasses = classes.toArray(new EClass[]{});

        ListView lvPackages = (ListView) findViewById(R.id.lvClass);
        ArrayAdapter<EClass> adapter = new ClassAdapter(this, R.layout.class_item, packageClasses);
        lvPackages.setAdapter(adapter);

        lvPackages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent classesIntent = new Intent(PackageActivity.this, ClassActivity.class);
                classesIntent.putExtra(Const.CLASS_ID.name(), packageClasses[position].getId());
                startActivity(classesIntent);
            }
        });
    }

    private EClass[] getClasses(int packageId, Iterable<EClass> classes) {
        List<EClass> filtered = new ArrayList<EClass>();
        for (EClass c : classes) {
            if (c.getPackageId() == packageId) {
                filtered.add(c);
            }
        }
        return filtered.toArray(new EClass[]{});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_package, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
