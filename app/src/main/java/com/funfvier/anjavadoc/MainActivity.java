package com.funfvier.anjavadoc;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.funfvier.anjavadoc.adapter.PackageAdaper;
import com.funfvier.anjavadoc.entity.EClass;
import com.funfvier.anjavadoc.entity.EPackage;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DataSource ds = new DataSource(this);

        ListView lvPackages = (ListView)findViewById(R.id.lvPackages);
        ArrayAdapter<EPackage> adapter = new PackageAdaper(this, R.layout.package_item, ds.packages);
        lvPackages.setAdapter(adapter);

        lvPackages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent classesIntent = new Intent(MainActivity.this, PackageActivity.class);
                classesIntent.putExtra(Const.PACKAGE_ID.name(), ds.packages[position].getId());
                startActivity(classesIntent);
            }
        });
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
