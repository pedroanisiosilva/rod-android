package com.runordie.rod;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.runordie.rod.helpers.Config;
import com.runordie.rod.login.Login;
import com.runordie.rod.login.RodLoginActivity;
import com.runordie.rod.run.RunEditActivity;
import com.runordie.rod.run.Run;
import com.runordie.rod.run.RunItemListViewAdapter;
import com.runordie.rod.run.Runs;
import com.runordie.rod.run.UserRuns;
import com.runordie.rod.status.StatsActivity;

import org.springframework.web.client.HttpClientErrorException;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class RodActivity extends AppCompatActivity {
    private RunEditActivity newRun = new RunEditActivity();
    private String TAG = "RodActivity";
    private SwipeRefreshLayout swipeContainer;
    private Runs runs = null;

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        updateListView();

    }

    private void setRunsRefresh(){
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.runSwipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateListView();
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(R.color.primary, android.R.color.holo_green_light,
                                                android.R.color.holo_orange_light,
                                                android.R.color.holo_red_light);


    }

    private void updateListView(){

        final ListView listview = (ListView) findViewById(R.id.listOfRuns);

        try {
            Log.i(TAG,Config.getRunsUrl(this));
            runs = new UserRuns(this).execute(Config.getRunsUrl(this)).get();
            RunItemListViewAdapter adapter = new RunItemListViewAdapter(this, R.layout.run_list_item, runs.getRuns());

            listview.setAdapter(adapter);
//            AndroidSwipeLayout
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof  HttpClientErrorException){
                if(((HttpClientErrorException)e.getCause()).getStatusCode().value() == 401){
                    Login.logOut(this);
                }
            }else{
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Login.isLogged(this)){
            setContentView(R.layout.activity_rod);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);
            setRunsRefresh();

            FloatingActionButton addRun = (FloatingActionButton) findViewById(R.id.addRun);
            addRun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(getBaseContext(),RunEditActivity.class), 0);
                }
            });

            FloatingActionButton runStats = (FloatingActionButton) findViewById(R.id.runStats);
            runStats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getBaseContext(),StatsActivity.class);
                    intent.putExtra("stats", (Serializable) runs.getStats());
                    startActivityForResult(intent, 0);
                }
            });
        }else{
            startActivity(new Intent(this,RodLoginActivity.class));
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rod, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            Login.logOut(this);
            startActivity(new Intent(this, RodLoginActivity.class));
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
