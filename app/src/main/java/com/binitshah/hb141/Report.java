package com.binitshah.hb141;

import java.util.Date;
import java.util.List;

/**
 * Created by bshah on 11/9/2016.
 */

public class Report {

    String placeId;
    String volunteerId;
    Date dateVisited;
    boolean public_view;
    boolean restroom_view;
    boolean no_view;
    String comment;

    public Report() {
        this("", "", null, false, false, false, "");
    }

    public Report(String placeId, String volunteerId, Date dateVisited, boolean public_view, boolean restroom_view, boolean no_view, String comment) {
        this.placeId = placeId;
        this.volunteerId = volunteerId;
        this.dateVisited = dateVisited;
        this.public_view = public_view;
        this.restroom_view = restroom_view;
        this.no_view = no_view;
        this.comment = comment;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Date getDateVisited() {
        return dateVisited;
    }

    public void setDateVisited(Date dateVisited) {
        this.dateVisited = dateVisited;
    }

    public boolean isPublic_view() {
        return public_view;
    }

    public void setPublic_view(boolean public_view) {
        this.public_view = public_view;
    }

    public boolean isRestroom_view() {
        return restroom_view;
    }

    public void setRestroom_view(boolean restroom_view) {
        this.restroom_view = restroom_view;
    }

    public boolean isNo_view() {
        return no_view;
    }

    public void setNo_view(boolean no_view) {
        this.no_view = no_view;
    }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }
}
