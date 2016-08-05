package com.runordie.rod.status;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
//import com.runordie.rod.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runordie.rod.R;
import com.runordie.rod.run.Stats;

import java.util.List;

/**
 * Created by wsouza on 8/1/16.
 */
public class StatsActivity extends AppCompatActivity {
    private CircularProgressBarPercent circularProgressBar;
    private TextView circularProgressBarPace;
    private TextView circularProgressBarKM;
    private TextView circularProgressBarRuns;
    private TextView runStatsWeekText;
    private TextView runStatsMeta;
    private int lastWeek;
    private List<Stats> stats;
    private String statusJsonAsString;
    private Button nextWeek;
    private Button previusWeek;
    private int activeWeek;
    private WebView runStatsChart;
    private Integer statsActive = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);


        Bundle extras = getIntent().getExtras();
        runStatsChart = (WebView)findViewById(R.id.runStatsChart);
        runStatsChart.setBackgroundColor(Color.TRANSPARENT);
        WebSettings settins = runStatsChart.getSettings();
        settins.setJavaScriptEnabled(true);


        if(extras != null){
            stats = (List<Stats>)extras.getSerializable("stats");
            if(!stats.isEmpty()){
                runStatsChart.loadUrl("file:///android_res/raw/stats_chart.html");
                try {
                    statusJsonAsString = new ObjectMapper().writeValueAsString(stats);
                    runStatsChart.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url){
                            runStatsChart.evaluateJavascript("javascript:processData("+statusJsonAsString+", "+lastWeek+ ");", null);
                        }
                    });

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                showStats();

                getNextWeek().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (statsActive > 0){
                            activeWeek+=1;
                            showWeekStats(activeWeek);
                            statsActive-=1;
                            showStats();
                        }else{

                        }
                    }
                });

                getPreviusWeek().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(statsActive < (stats.size() - 1) ){
                            activeWeek-=1;
                            statsActive+=1;
                            showWeekStats(activeWeek);
                            showStats();
                        }
                    }
                });

            }
        }

    }
    private void showStats(){

        float percent = 0;
        float realPercent = 0;
        final Stats stat = stats.get(statsActive);
        activeWeek = lastWeek = stat.getNumber();
        realPercent = (stat.getTotalKms().floatValue() / stat.getGoal().floatValue()) * 100;

        if(realPercent > 100){
            getCircularProgressBar().setRealPercent(realPercent);
            percent = 99.999f;
        }else{
            percent = realPercent;
        }

        getCircularProgressBarRuns().setText(String.format("%02d", stat.getRunCount()));
        getCircularProgressBarKM().setText(stat.getTotalKms().toString());
        getCircularProgressBarPace().setText(stat.getPace());
        getRunStatsMeta().setText(stat.getGoal().toString());
        getCircularProgressBar().setProgressWithAnimation(percent);

        setWeekLabel(lastWeek);

        int color = 0;
        int bgColor = 0;

        if(percent < 25 ){
            color = -2937298;
            bgColor = 1305685550;
        }else if(percent < 50){
            color = -5317;
            bgColor = 1308617531;
        }else if(percent < 75){
            color = -16728876;
            bgColor = 1291893972;
        }else{
            color = -10011977;
            bgColor = 1298610871;
        }

        circularProgressBar.setColor(color);
        circularProgressBar.setBackgroundColor(bgColor);
    }
    private void setWeekLabel(Integer week) {
        getRunStatsWeekText().setText("Semana " + week);
    }

    private void showWeekStats(final Integer week) {
        runStatsChart.evaluateJavascript("javascript:processData("+statusJsonAsString+", "+week+ ");", null);
        setWeekLabel(week);
    }


    public TextView getRunStatsWeekText() {
        if(this.runStatsWeekText == null)
            this.runStatsWeekText = (TextView) findViewById(R.id.runStatsRunsWeekText);

        return runStatsWeekText;
    }

    public CircularProgressBarPercent getCircularProgressBar() {
        if(this.circularProgressBar == null)
            circularProgressBar = (CircularProgressBarPercent)findViewById(R.id.runStatsProgressBar);
        return circularProgressBar;
    }

    public TextView getCircularProgressBarPace() {
        if(this.circularProgressBarPace == null)
            circularProgressBarPace = (TextView)findViewById(R.id.runStatsPaceProgressBar);
        return circularProgressBarPace;
    }

    public TextView getCircularProgressBarKM() {
        if(this.circularProgressBarKM == null)
            circularProgressBarKM = (TextView)findViewById(R.id.runStatsKMsProgressBar);
        return circularProgressBarKM;
    }

    public TextView getCircularProgressBarRuns() {
        if(this.circularProgressBarRuns == null)
            circularProgressBarRuns = (TextView)findViewById(R.id.runStatsRunsProgressBar);
        return circularProgressBarRuns;
    }

    public Button getPreviusWeek() {
        if(this.previusWeek == null)
            this.previusWeek = (Button) findViewById(R.id.runStatsPreviuesWeek);
        return previusWeek;
    }

    public Button getNextWeek() {
        if(this.nextWeek == null)
            this.nextWeek = (Button) findViewById(R.id.runStatsNextWeek);
        return nextWeek;
    }

    public TextView getRunStatsMeta() {
        if(this.runStatsMeta == null)
            this.runStatsMeta = (TextView) findViewById(R.id.runStatsMetaDescription);
        return runStatsMeta;
    }

    public String getStatusJsonAsString() {
        return statusJsonAsString;
    }

    public List<Stats> getStats() {
        return stats;
    }

    public int getLastWeek() {
        return lastWeek;
    }
}
