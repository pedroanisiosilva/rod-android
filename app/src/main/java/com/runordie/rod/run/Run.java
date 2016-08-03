package com.runordie.rod.run;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by wsouza on 7/6/16.
 */

//@JsonIgnoreProperties(ignoreUnknown=true)
public class Run {
    private Integer id;

    @JsonSerialize(using = RunDatetimeSerializer.class)
    private Date datetime;
    private Double distance;
    private Long duration;
    private String note;
    private Date createdAt;
    private Date updatedAt;
    private String pace;
    private String speed;

//    private HttpEntity<ByteArrayResource> img;
//
//    public HttpEntity<ByteArrayResource> getImg() {
//        return img;
//    }
//
//    public void setImg(HttpEntity<ByteArrayResource> img) {
//        this.img = img;
//    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    @JsonProperty("user_id") public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    @JsonProperty("created_at") public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
