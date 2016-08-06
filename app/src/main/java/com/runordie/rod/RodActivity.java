package com.runordie.rod;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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

import java.io.Serializable;
import java.util.concurrent.ExecutionException;


public class RodActivity extends AppCompatActivity {
    private RunEditActivity newRun = new RunEditActivity();
    private String TAG = "RodActivity";
    private SwipeRefreshLayout swipeContainer;
    private Runs runs = null;

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        updateListView(true);

    }

    private void setRunsRefresh(){
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.runSwipeContainer);
        // Setup refresh listener which triggers new data loading

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateListView(false);
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(R.color.primary, android.R.color.holo_green_light,
                                                android.R.color.holo_orange_light,
                                                android.R.color.holo_red_light);

    }

    private void updateListView(boolean showLoading){
        Log.i(TAG,Config.getRunsUrl(this));
        new UserRuns(showLoading).execute(Config.getRunsUrl(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rod);
        if(Login.isLogged(this)){
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
                    if(getRuns() != null){
                        Intent intent = new Intent(getBaseContext(),StatsActivity.class);
                        intent.putExtra("stats", (Serializable) runs.getStats());
                        startActivityForResult(intent, 0);
                    }
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
    public class UserRuns extends AsyncTask<String, Void, Runs> {
        public Boolean showLoading = true;

        public UserRuns(){}
        public UserRuns(Boolean showLoading){
            this.showLoading = showLoading;
        }

        public Runs doInBackground(String... url) {

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.set("X-User-Email", Login.getLoginInfo(getBaseContext())[0]);
            requestHeaders.set("X-User-Token", Login.getLoginInfo(getBaseContext())[1]);
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

            RestTemplate restTemplate = new RestTemplate();
            // tratar aqui na classe as exception, remover da atividade
            try {
                ResponseEntity<Runs> runs = restTemplate.exchange(url[0], HttpMethod.GET, requestEntity, Runs.class);
                return runs.getBody();
            }catch (HttpClientErrorException e){
                if(e.getStatusCode().value() == 401){
                    Login.logOut(getBaseContext());
                    startActivity(new Intent(getBaseContext(), RodLoginActivity.class));
                    finish();
                    Toast.makeText(getBaseContext(), "Erro ao realizar login", Toast.LENGTH_LONG).show();
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
                listview.setAdapter(new RunItemListViewAdapter(getBaseContext(), R.layout.run_list_item, runs.getRuns()));
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
}
