package com.runordie.rod.status;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
//import com.runordie.rod.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.larswerkman.lobsterpicker.OnColorListener;
import com.larswerkman.lobsterpicker.sliders.LobsterShadeSlider;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.runordie.rod.R;
import com.runordie.rod.run.Stats;

import java.util.List;

/**
 * Created by wsouza on 8/1/16.
 */
public class StatsActivity extends AppCompatActivity {
    CircularProgressBarPercent circularProgressBar;
    CircularProgressBarRun circularProgressBarPace;
    CircularProgressBarRun circularProgressBarKM;
    CircularProgressBarRun circularProgressBarRuns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);
        circularProgressBar = (CircularProgressBarPercent)findViewById(R.id.runStatsProgressBar);
        circularProgressBarKM = (CircularProgressBarRun)findViewById(R.id.runStatsKMsProgressBar);
        circularProgressBarPace = (CircularProgressBarRun)findViewById(R.id.runStatsPaceProgressBar);
        circularProgressBarRuns = (CircularProgressBarRun)findViewById(R.id.runStatsRunsProgressBar);

        float percent = 0;
        float realPercent = 0;

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            List<Stats> stats = (List<Stats>)extras.getSerializable("stats");
            Stats stat;
            if(!stats.isEmpty()){
                stat = stats.get(0);
                realPercent = (stat.getTotalKms().floatValue() / stat.getGoal().floatValue()) * 100;
                if(realPercent > 100){
                    circularProgressBar.setRealPercent(realPercent);
                    percent = 99.999f;
                }else{
                    percent = realPercent;
                }

                circularProgressBarRuns.setRunData(String.format("%02d", stat.getRunCount()));
                circularProgressBarKM.setRunData(stat.getTotalKms().toString());
                circularProgressBarPace.setRunData(stat.getPace());
                final WebView runStatsChart = (WebView)findViewById(R.id.runStatsChart);
                runStatsChart.setBackgroundColor(Color.TRANSPARENT);
                WebSettings settins = runStatsChart.getSettings();
                settins.setJavaScriptEnabled(true);

//                settins.setPluginState(WebSettings.PluginState.ON);

                runStatsChart.loadUrl("file:///android_res/raw/stats_chart.html");

                try {
                    final String statusJsonAsString = new ObjectMapper().writeValueAsString(stats);
                    runStatsChart.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url){
                            runStatsChart.evaluateJavascript("javascript:processData("+statusJsonAsString+", 29);", null);
                        }
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        }
        circularProgressBar.setProgressWithAnimation(percent);

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
}
