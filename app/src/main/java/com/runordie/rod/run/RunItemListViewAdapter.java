package com.runordie.rod.run;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runordie.rod.R;
import com.runordie.rod.helpers.Config;

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
    public View getView(final int position, View  view, ViewGroup parent) {
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


        ((ImageView)runView.findViewById(R.id.runFlagImg)).setImageResource(getFlagIconName(run.getSpeed()));

        double avgPaceSecMeters = (run.getDuration() / (run.getDistance() * 1000) * 1000);

        int paceMin = (int) (avgPaceSecMeters / 60);
        int paceSec = (int) (avgPaceSecMeters  - (paceMin*60));

        String pace = String.format("%02d:%02d", paceMin, paceSec);

        ((TextView)runView.findViewById(R.id.runPaceTxt)).setText(String.valueOf(pace));

        runView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editRun = new Intent(getContext(),RunEditActivity.class);
                editRun.putExtra(RunEnum.RUN, run);
                getContext().startActivity(editRun);
            }
        });

        runView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(final View view) {
                view.setBackgroundColor(view.getResources().getColor((R.color.list_item_selected)));

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dark_Dialog);

                builder.setTitle("Deseja remover corrida?");
                if(run.getNote() == null){
                    // oque vai ser aqui? quando não existir descrição
                }else{
                    builder.setMessage(run.getNote());
                }

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {
                        view.setBackgroundColor(view.getResources().getColor((R.color.white)));
                        boolean deleted = false;
                        try {
                            deleted = new RunDelete(run, getContext()).execute(Config.getRunDUUrl(getContext(), run.getId())).get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        if(deleted){
                            remove(run);
                            Toast.makeText(getContext(), "Corrida deletada com sucesso", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(), "Erro ao deletar corrida", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.setBackgroundColor(view.getResources().getColor((R.color.white)));
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                return true;
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
