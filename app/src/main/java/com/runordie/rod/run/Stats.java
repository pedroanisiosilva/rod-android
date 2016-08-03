package com.runordie.rod.run;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by wsouza on 7/29/16.
 */
public class Stats implements Serializable {

    private Integer number;
    private Integer goal;
    private Integer runCount;
    private Double totalKms;
    private String pace;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    @JsonProperty("run_count") public Integer getRunCount() {
        return runCount;
    }

    public void setRunCount(Integer runCount) {
        this.runCount = runCount;
    }

    public Double getTotalKms() {
        return totalKms;
    }

    @JsonProperty("total_kms") public void setTotalKms(Double totalKms) {
        this.totalKms = totalKms;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }
}
