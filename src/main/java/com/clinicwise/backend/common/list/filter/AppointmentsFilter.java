package com.clinicwise.backend.common.list.filter;

public class AppointmentsFilter extends BaseFilter{
    private String duration;
    private String status;
    private String additionalNote;
    private String startDate;

    public AppointmentsFilter(int size, int page, String sort, String duration, String status, String additionalNote, String startDate) {
        super(size, page, sort);
        this.duration = duration;
        this.status = status;
        this.additionalNote = additionalNote;
        this.startDate = startDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
