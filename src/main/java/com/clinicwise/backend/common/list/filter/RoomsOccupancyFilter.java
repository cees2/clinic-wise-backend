package com.clinicwise.backend.common.list.filter;

public class RoomsOccupancyFilter extends BaseFilter{
    private String dateFilter;
    private String roomFilter;

    public RoomsOccupancyFilter(int size, int page, String sort, String search, String dateFilter, String roomFilter) {
        super(size, page, sort, search);
        this.dateFilter = dateFilter;
        this.roomFilter = roomFilter;
    }

    public String getDateFilter() {
        return dateFilter;
    }

    public void setDate_filter(String dateFilter) {
        this.dateFilter = dateFilter;
    }

    public String getRoomFilter() {
        return roomFilter;
    }

    public void setRoom_filter(String roomFilter) {
        this.roomFilter = roomFilter;
    }
}
