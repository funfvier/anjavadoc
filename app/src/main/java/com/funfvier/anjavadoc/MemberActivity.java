package com.funfvier.anjavadoc;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.funfvier.anjavadoc.entity.EMember;

import java.util.Arrays;


public class MemberActivity extends ActionBarActivity {
    private static final String TAG = MemberActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        final DataSource ds = new DataSource(this);

        int memberId = getIntent().getIntExtra(Const.MEMBER_ID.name(), -1);
        EMember member = getMember(memberId, Arrays.asList(ds.members));

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
