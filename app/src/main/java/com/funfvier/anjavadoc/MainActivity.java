package com.funfvier.anjavadoc;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.funfvier.anjavadoc.adapter.PackageAdaper;
import com.funfvier.anjavadoc.entity.EPackage;


public class MainActivity extends ActionBarActivity {

    EPackage[] packages = {new EPackage("java.applet", "Provides the classes necessary to create an applet and the classes an applet uses to communicate with its applet context."),
            new EPackage("java.awt", "Contains all of the classes for creating user interfaces and for painting graphics and images."),
            new EPackage("java.awt.color", "Provides classes for color spaces."),
            new EPackage("java.awt.datatransfer", "Provides interfaces and classes for transferring data between and within applications.")};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvPackages = (ListView)findViewById(R.id.lvPackages);
        ArrayAdapter<EPackage> adapter = new PackageAdaper(this, R.layout.package_item, packages);
        lvPackages.setAdapter(adapter);

        Button test1Button = (Button) findViewById(R.id.test1Button);
    }

    public void openWebView(View view) {
        Intent intent = new Intent(this, WebViewActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
