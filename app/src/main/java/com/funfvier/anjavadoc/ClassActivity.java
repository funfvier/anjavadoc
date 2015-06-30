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
import com.funfvier.anjavadoc.adapter.MemberAdapter;
import com.funfvier.anjavadoc.dao.MemberDao;
import com.funfvier.anjavadoc.db.DBOpenHelper;
import com.funfvier.anjavadoc.entity.EClass;
import com.funfvier.anjavadoc.entity.EMember;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClassActivity extends ActionBarActivity {
    private final String TAG = ClassActivity.class.getName();
    private DBOpenHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

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

        MemberDao memberDao = new MemberDao(dbhelper);

        final EMember[] classMembers = memberDao.getByClassId(classId).toArray(new EMember[]{});
        dbhelper.close();

        ListView lvClasses = (ListView) findViewById(R.id.lvMembers);
        ArrayAdapter<EMember> adapter = new MemberAdapter(this, R.layout.member_item, classMembers);
        lvClasses.setAdapter(adapter);

        lvClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent memberIntent = new Intent(ClassActivity.this, MemberActivity.class);
                memberIntent.putExtra(Const.MEMBER_ID.name(), classMembers[position].getId());
                startActivity(memberIntent);
            }
        });
    }

    private EMember[] getMembers(int classId, Iterable<EMember> members) {
        List<EMember> result = new ArrayList<>();
        for(EMember member : members) {
            if(classId == member.getClassId()) {
                result.add(member);
            }
        }
        return result.toArray(new EMember[] {});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_class, menu);
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
