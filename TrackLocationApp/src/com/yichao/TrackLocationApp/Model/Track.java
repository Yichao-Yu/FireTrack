package com.yichao.TrackLocationApp.Model;

import com.shaded.fasterxml.jackson.annotation.JsonProperty;
import com.shaded.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by yichaoyu on 11/21/14.
 */
@JsonSerialize
public class Track {
    @JsonProperty("friendlyName")
    private String name;
    @JsonProperty("start_coordinate")
    private Coordinate start;
    @JsonProperty("end_coordinate")
    private Coordinate end;

    public Track() {
        this.name = "new track";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinate getStart() {
        return start;
    }

    public void setStart(Coordinate start) {
        this.start = start;
    }

    public Coordinate getEnd() {
        return end;
    }

    public void setEnd(Coordinate end) {
        this.end = end;
    }
}
