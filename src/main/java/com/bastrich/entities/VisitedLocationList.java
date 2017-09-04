package com.bastrich.entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author bastrich on 12.08.2017.
 */
public class VisitedLocationList {
    public List<VisitedLocation> visits;

    public VisitedLocationList(List<VisitedLocation> visits) {
        this.visits = visits;
    }

    private static final byte[] VISITS = "{\"visits\": [".getBytes();

    private static final byte[] MARK = "{\"mark\":".getBytes();
    private static final byte[] VISITED_AT = ",\"visited_at\":".getBytes();
    private static final byte[] PLACE = ",\"place\":\"".getBytes();

    private static final byte[] LOCAL_END = "\"}".getBytes();
    private static final byte[] COMMA = ",".getBytes();
    private static final byte[] END = "]}".getBytes();

    public static byte[] getJsonBytes(List<VisitedLocation> visits) throws IOException {
        if (visits == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(VISITS);

        for (int i = 0; i < visits.size(); i++) {
            VisitedLocation visit = visits.get(i);
            try {
                stream.write(MARK);
                stream.write(String.valueOf(visit.mark).getBytes());
                stream.write(VISITED_AT);
                stream.write(String.valueOf(visit.visited_at).getBytes());
                stream.write(PLACE);
                stream.write(visit.place.getBytes(Charset.forName("UTF-8")));
                stream.write(LOCAL_END);
                if (i < visits.size() - 1) {
                    stream.write(COMMA);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        visits.forEach(visit -> {

        });


        stream.write(END);
        return stream.toByteArray();
    }
}
