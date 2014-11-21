package com.yichao.TrackLocationApp.Model;

import com.shaded.fasterxml.jackson.annotation.JsonProperty;
import com.shaded.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by yichaoyu on 11/21/14.
 */
@JsonSerialize
public class Coordinate {

    @JsonProperty("lat")
    private Double latitude;
    @JsonProperty("lng")
    private Double longtitude;
    @JsonProperty("capture_ts")
    private Long captureTs;

    public Coordinate(Double latitude, Double longtitude, Long captureTs) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.captureTs = captureTs;
    }

    public Coordinate() {}

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Long getCaptureTs() {
        return captureTs;
    }

    public void setCaptureTs(Long captureTs) {
        this.captureTs = captureTs;
    }
}
