package com.bastrich.entities;

/**
 * @author bastrich on 12.08.2017.
 */
public class VisitedLocation {
    public long mark;
    public long visited_at;
    public String place;


    public VisitedLocation(long mark, long visited_at, String place) {
        this.mark = mark;
        this.visited_at = visited_at;
        this.place = place;
    }
}
