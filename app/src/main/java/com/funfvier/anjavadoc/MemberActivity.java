package com.funfvier.anjavadoc;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.funfvier.anjavadoc.dao.MemberDao;
import com.funfvier.anjavadoc.db.DBOpenHelper;
import com.funfvier.anjavadoc.entity.EMember;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;


public class MemberActivity extends ActionBarActivity {
    private DBOpenHelper dbhelper;
    private static final String TAG = MemberActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

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

        MemberDao memberDao = new MemberDao(dbhelper);

        int memberId = getIntent().getIntExtra(Const.MEMBER_ID.name(), -1);
        EMember member = memberDao.getById(memberId);
        dbhelper.close();

        if(member != null) {
            WebView vwMember = (WebView) findViewById(R.id.wvMember);
            vwMember.loadDataWithBaseURL("", member.getLongDescription(), "text/html", "UTF-8", "");
            vwMember.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Log.d(TAG, "URL clicked: " + url);
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });
        }
    }

    private EMember getMember(int id, Iterable<EMember> members) {
        for(EMember member : members) {
            if(member.getId() == id) {
                return member;
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_member, menu);
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
