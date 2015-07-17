package com.funfvier.anjavadoc;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.funfvier.anjavadoc.dao.ClassDao;
import com.funfvier.anjavadoc.db.DBOpenHelper;
import com.funfvier.anjavadoc.entity.EClass;

import java.io.IOException;
import java.sql.SQLException;


public class ClassLongDescriptionActivity extends ActionBarActivity {
    private final String TAG = ClassLongDescriptionActivity.class.getName();
    private DBOpenHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_long_description);

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

        int classId = getIntent().getIntExtra(Const.CLASS_ID.name(), -1);

        ClassDao classDao = new ClassDao(dbhelper);
        final EClass eClass = classDao.get(classId);

        if(eClass != null) {
            setTitle("Class: " + eClass.getName());
        } else {
            setTitle("Class");
        }

        WebView vwLongDesc = (WebView) findViewById(R.id.wvClassLongDescription);
        vwLongDesc.loadDataWithBaseURL("", eClass.getDescriptionLong(), "text/html", "UTF-8", "");
        vwLongDesc.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "URL clicked: " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        dbhelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_class_long_description, menu);
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
