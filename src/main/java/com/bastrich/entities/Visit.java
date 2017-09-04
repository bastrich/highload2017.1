package com.bastrich.entities;

import java.io.IOException;

/**
 * @author bastrich on 12.08.2017.
 */
public class Visit {


    public int id;
    public int location;
    public int user;
    public long visited_at;
    public int mark;

    private static final byte[] VISIT = "{\"user\":      ,\"location\":      ,\"visited_at\":                ,\"id\":       ,\"mark\": }".getBytes();


    public static byte[] getJsonBytes(long[] visit) throws IOException {
        if (visit == null) {
            return null;
        }

        byte[] result = new byte[VISIT.length];
        System.arraycopy(VISIT, 0, result, 0, VISIT.length);

        byte[] userBytes = Long.toString(visit[2]).getBytes();
        System.arraycopy(userBytes, 0, result, 8, userBytes.length);
        byte[] locationBytes = Long.toString(visit[1]).getBytes();
        System.arraycopy(locationBytes, 0, result, 26, locationBytes.length);
        byte[] visited_atBytes = Long.toString(visit[3]).getBytes();
        System.arraycopy(visited_atBytes, 0, result, 46, visited_atBytes.length);
        byte[] idBytes = Long.toString(visit[0]).getBytes();
        System.arraycopy(idBytes, 0, result, 68, idBytes.length);
        byte[] markBytes = Long.toString(visit[4]).getBytes();
        System.arraycopy(markBytes, 0, result, 83, markBytes.length);

        return result;
    }

    public static long[] getLongs(Visit visit) {
        long[] visitLongs = new long[5];
        visitLongs[0] = visit.id;
        visitLongs[1] = visit.location;
        visitLongs[2] = visit.user;
        visitLongs[3] = visit.visited_at;
        visitLongs[4] = visit.mark;
        return visitLongs;
    }



    public static long getUser(long[] visit) {
        return visit[2];
    }

    public static long getVisited_at(long[] visit) {
        return visit[3];
    }

    public static long getLocation(long[] visit) {
        return visit[1];
    }

    public static byte getMark(long[] visit) {
        return (byte) visit[4];
    }

}
