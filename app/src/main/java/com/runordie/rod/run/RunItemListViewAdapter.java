package com.runordie.rod.run;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runordie.rod.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

/**
 * Created by wsouza on 7/21/16.
 */
public class RunItemListViewAdapter extends ArrayAdapter<Run>{
    List<Run> runs;
    private int resource;

    public RunItemListViewAdapter(Context context, int resource, List<Run> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View  view, ViewGroup parent) {
        LinearLayout runView;

        final Run run = getItem(position);

        if (view == null) {
            runView = new LinearLayout(getContext());
            LayoutInflater.from(getContext()).inflate(resource, runView, true);
        } else {
            runView = (LinearLayout) view;
        }

        final String kms = String.format("%02d", run.getDistance().intValue());
        ((TextView)runView.findViewById(R.id.runKMTxt)).setText(kms);

        String digit = String.valueOf(run.getDistance()).replaceAll("^\\d+.", "");
        final String meters = String.format("%02d", Integer.parseInt(digit));
        ((TextView)runView.findViewById(R.id.runMetersTxt)).setText("."+meters);


        String timeAgo = new PrettyTime().format(run.getDatetime());
        ((TextView)runView.findViewById(R.id.runTimeAgoTxt)).setText(timeAgo);

        double speed = run.getDistance() / (run.getDuration() / 3600);

        ((ImageView)runView.findViewById(R.id.runFlagImg)).setImageResource(getFlagIconName(speed));

        double avgPaceSecMeters = (run.getDuration() / (run.getDistance() * 1000) * 1000);

        int paceMin = (int) (avgPaceSecMeters / 60);
        int paceSec = (int) (avgPaceSecMeters  - (paceMin*60));

        String pace = String.format("%02d:%02d", paceMin, paceSec);

        ((TextView)runView.findViewById(R.id.runPaceTxt)).setText(String.valueOf(pace));

        runView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editRun = new Intent(getContext(),RunEditActivity.class);
                editRun.putExtra(RunEnum.RUN_ID, run.getId());
                editRun.putExtra(RunEnum.RUN_DISTANCE, run.getDistance());
                editRun.putExtra(RunEnum.RUN_DURATION, run.getDuration() * 1000);
                editRun.putExtra(RunEnum.RUN_DATETIME, run.getDatetime().getTime());
                getContext().startActivity(editRun);
            }
        });

        return runView;
    }

    public int getFlagIconName(Double speed){

        if (speed >= 17.1 )
            return R.mipmap.falcon_flag;

        if (speed >= 15 && speed < 17.1)
            return R.mipmap.eagle_flag;

        if (speed >= 13.3 && speed < 15)
            return R.mipmap.cheetah_flag;

        if (speed >= 12 && speed < 13.3)
            return R.mipmap.ostrich_flag;

        if (speed >= 10.9 && speed < 12)
            return R.mipmap.longhorn_flag;

        if (speed >= 10 && speed < 10.9)
            return R.mipmap.bull_flag;

        if (speed >= 9.2 && speed < 10)
            return R.mipmap.hare_flag;

        if (speed >= 8.6 && speed < 9.2)
            return R.mipmap.fox_flag;

        if (speed >= 7.5 && speed < 8.6)
            return R.mipmap.horse_flag;

        if (speed < 7.5 && speed > 0)
            return R.mipmap.mamba_flag;

        return R.mipmap.run_or_die_principal_logo;

    }

}
