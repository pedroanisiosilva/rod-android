package com.runordie.rod;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.runordie.rod.run.UserRuns;

import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class RodActivity extends AppCompatActivity {
    private RunEditActivity newRun = new RunEditActivity();
    private String TAG = "RodActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Login.isLogged(this)){
            setContentView(R.layout.activity_rod);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            final ListView listview = (ListView) findViewById(R.id.listOfRuns);

            try {

                List<Run> runs = new UserRuns(this).execute(Config.getRunsUrl(this)).get();
                RunItemListViewAdapter adapter = new RunItemListViewAdapter(this, R.layout.run_list_item, runs);

                listview.setAdapter(adapter);

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

            FloatingActionButton addRun = (FloatingActionButton) findViewById(R.id.addRun);
            addRun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(getBaseContext(),RunEditActivity.class), 0);
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
