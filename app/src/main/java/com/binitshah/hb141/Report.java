package com.binitshah.hb141;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by bshah on 11/9/2016.
 */

@IgnoreExtraProperties
public class Report {

    private String reportId;
    private String dateVisited;
    private String placeId;
    private String volunteerId;
    private boolean passFail; //true = pass
    private boolean publicView;
    private boolean restroomView;
    private boolean noView;
    private String additionalComments;

    public Report() {
    }

    public Report(String reportId, String dateVisited, String placeId, String volunteerId, boolean passFail, boolean publicView, boolean restroomView, boolean noView, String additionalComments) {
        this.reportId = reportId;
        this.dateVisited = dateVisited;
        this.placeId = placeId;
        this.volunteerId = volunteerId;
        this.passFail = passFail;
        this.publicView = publicView;
        this.restroomView = restroomView;
        this.noView = noView;
        this.additionalComments = additionalComments;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getDateVisited() {
        return dateVisited;
    }

    public void setDateVisited(String dateVisited) {
        this.dateVisited = dateVisited;
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

    public boolean isPassFail() {
        return passFail;
    }

    public void setPassFail(boolean passFail) {
        this.passFail = passFail;
    }

    public boolean isPublicView() {
        return publicView;
    }

    public void setPublicView(boolean publicView) {
        this.publicView = publicView;
    }

    public boolean isRestroomView() {
        return restroomView;
    }

    public void setRestroomView(boolean restroomView) {
        this.restroomView = restroomView;
    }

    public boolean isNoView() {
        return noView;
    }

    public void setNoView(boolean noView) {
        this.noView = noView;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }
}
