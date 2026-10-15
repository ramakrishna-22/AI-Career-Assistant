package com.careerassistant.dto;

public class ApplicationStatsResponse {

    private long totalApplications;
    private long applied;
    private long shortlisted;
    private long interviews;
    private long selected;
    private long rejected;
    private long withdrawn;

    public ApplicationStatsResponse() {
    }

    public ApplicationStatsResponse(long totalApplications, long applied, long shortlisted,
                                  long interviews, long selected, long rejected, long withdrawn) {
        this.totalApplications = totalApplications;
        this.applied = applied;
        this.shortlisted = shortlisted;
        this.interviews = interviews;
        this.selected = selected;
        this.rejected = rejected;
        this.withdrawn = withdrawn;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getApplied() {
        return applied;
    }

    public void setApplied(long applied) {
        this.applied = applied;
    }

    public long getShortlisted() {
        return shortlisted;
    }

    public void setShortlisted(long shortlisted) {
        this.shortlisted = shortlisted;
    }

    public long getInterviews() {
        return interviews;
    }

    public void setInterviews(long interviews) {
        this.interviews = interviews;
    }

    public long getSelected() {
        return selected;
    }

    public void setSelected(long selected) {
        this.selected = selected;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }

    public long getWithdrawn() {
        return withdrawn;
    }

    public void setWithdrawn(long withdrawn) {
        this.withdrawn = withdrawn;
    }
}
