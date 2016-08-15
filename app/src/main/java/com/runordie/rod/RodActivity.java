package com.runordie.rod;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.runordie.rod.helpers.Config;
import com.runordie.rod.login.Login;
import com.runordie.rod.login.RodLoginActivity;
import com.runordie.rod.run.RunEditActivity;
import com.runordie.rod.run.RunItemListViewAdapter;
import com.runordie.rod.run.Runs;
import com.runordie.rod.status.StatsActivity;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

//hockeyapp
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.metrics.MetricsManager;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;


public class RodActivity extends AppCompatActivity {
    private String TAG = "RodActivity";
    private SwipeRefreshLayout swipeContainer;
    private Runs runs = null;

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        updateRuns(true);
    }

    private void setRunsRefresh(){
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.runSwipeContainer);
        // Setup refresh listener which triggers new data loading

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRuns(false);
                swipeContainer.setRefreshing(false);
            }
        });

//        swipeContainer.setColorSchemeResources(R.color.primary, android.R.color.holo_green_light,
//                                                android.R.color.holo_orange_light,
//                                                android.R.color.holo_red_light);

    }
    private void updateRuns(boolean showLoading){
        new UserRuns(showLoading, this).execute(Config.getRunsUrl(this));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerAppManger();
        setContentView(R.layout.activity_rod);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        if(Login.isLogged(this)){
            setRunsRefresh();
            setOnClickActions();
        }else{
            startActivity(new Intent(this,RodLoginActivity.class));
            this.finish();
        }
    }

    private void setOnClickActions() {

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
                if(getRuns() != null){
                    Intent intent = new Intent(getBaseContext(),StatsActivity.class);
                    intent.putExtra("stats", (Serializable) runs.getStats());
                    startActivityForResult(intent, 0);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rod, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            Login.logOut(this);
            startActivity(new Intent(this, RodLoginActivity.class));
            this.finish();
            return true;
        }
//        if (id == R.id.user_setting) {
//            startActivity(new Intent(this, UserActivity.class));
//            return false;
//        }

        return super.onOptionsItemSelected(item);
    }

    public class UserRuns extends AsyncTask<String, Void, Runs> {
        private Boolean showLoading = true;
        private Context context;

        public UserRuns(Boolean showLoading, Context context){
            this.showLoading = showLoading;
            this.context = context;
        }

        public Runs doInBackground(String... url) {

            HttpEntity<?> requestEntity = new HttpEntity<Object>(Config.getHttpHeaders(context));

            // tratar aqui na classe as exception, remover da atividade
            try {
                ResponseEntity<Runs> runs = new RestTemplate().exchange(url[0], HttpMethod.GET, requestEntity, Runs.class);

                return runs.getBody();
            }catch (HttpClientErrorException e){
                if(e.getStatusCode().value() == 401){
                    Login.logOut(getBaseContext());
                    startActivity(new Intent(getBaseContext(), RodLoginActivity.class));
                    finish();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(showLoading)
                ((RelativeLayout)findViewById(R.id.loadingPanel)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Runs runs) {
            super.onPostExecute(runs);
            if(showLoading)
                ((RelativeLayout)findViewById(R.id.loadingPanel)).setVisibility(View.GONE);

            if(runs != null){
                ListView listview = (ListView) findViewById(R.id.listOfRuns);
                listview.setAdapter(new RunItemListViewAdapter(context, R.layout.run_list_item, runs.getRuns()));
                setRuns(runs);
            }
        }
    }


    public Runs getRuns() {
        return runs;
    }

    public void setRuns(Runs runs) {
        this.runs = runs;
    }

    private void registerAppManger() {
        MetricsManager.register(this, getApplication());
        CrashManager.register(this);
    }
}
