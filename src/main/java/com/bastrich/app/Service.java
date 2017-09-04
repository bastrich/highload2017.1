package com.bastrich.app;

import com.bastrich.entities.*;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.LongBuffer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;

import static com.bastrich.app.Main.addToArray;
import static com.bastrich.app.Main.visitsByLocation;
import static java.util.Calendar.YEAR;

/**
 * @author bastrich on 12.08.2017.
 */
public class Service {


    public static byte[][][] usersArray;
    public static byte[][][] locationsArray;
    public static long[][] visitsArray;
    public static long[][][] visitsByUser;


    public Service() {
        usersArray = Main.usersArray;
        locationsArray = Main.locationsArray;
        visitsArray = Main.visitsArray;
        visitsByUser = Main.visitsByUser;
    }


    public byte[] getUser(int id) throws IOException {
        return User.getJsonBytes(usersArray[id]);
    }

    public byte[] getLocation(int id) throws IOException {
        return Location.getJsonBytes(locationsArray[id]);
    }

    public byte[] getVisit(int id) throws IOException {
        return Visit.getJsonBytes(visitsArray[id]);
    }

    public VisitedLocationList getVisits(int userId, Map<String, String> params) throws IOException {
        if (usersArray[userId] == null) {
            return null;
        }

        long fromDate = params.containsKey("fromDate") ? Long.parseLong(params.get("fromDate")) : -1;
        long toDate = params.containsKey("toDate") ? Long.parseLong(params.get("toDate")) : -1;
        int toDistance = params.containsKey("toDistance") ? Integer.parseInt(params.get("toDistance")) : -1;
        byte[] country = params.containsKey("country") ? URLDecoder.decode(params.get("country"), "UTF-8").getBytes(Charset.forName("UTF-8")) : null;

        List<VisitedLocation> visitedLocations = new ArrayList<>();

        if (visitsByUser[userId] != null) {
            for (long[] visitLongs : visitsByUser[userId]) {
                if (visitLongs == null) {
                    continue;
                }

                if (fromDate != -1 && Visit.getVisited_at(visitLongs) <= fromDate) {
                    continue;
                }
                if (toDate != -1 && Visit.getVisited_at(visitLongs) >= toDate) {
                    continue;
                }
                byte[][] location = locationsArray[Math.toIntExact(Visit.getLocation(visitLongs))];

                if (toDistance != -1 && Location.getDistance(location) >= toDistance) {
                    continue;
                }
                if (!(country == null || country.equals("")) && !Arrays.equals(Location.getCountry(location), country)) {
                    continue;
                }

                add(visitedLocations, new VisitedLocation(Visit.getMark(visitLongs), Visit.getVisited_at(visitLongs), Location.getPlace(location)));
            }
        }

        return new VisitedLocationList(visitedLocations);
    }

    private void add(List<VisitedLocation> list, VisitedLocation visitedLocation) {
        if (list.size() == 0){
            list.add(visitedLocation);
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).visited_at > visitedLocation.visited_at) {
                    list.add(i, visitedLocation);
                    return;
                }
            }
            list.add(visitedLocation);
        }
    }


    public double getAvgMark(int locationId, Map<String, String> params) throws IOException {
        if (locationsArray[locationId] == null) {
            return -1;
        }


        long fromDate = params.containsKey("fromDate") ? Long.parseLong(params.get("fromDate")) : -1;
        long toDate = params.containsKey("toDate") ? Long.parseLong(params.get("toDate")) : -1;
        int fromAge = params.containsKey("fromAge") ? Integer.parseInt(params.get("fromAge")) : -1;
        int toAge = params.containsKey("toAge") ? Integer.parseInt(params.get("toAge")) : -1;
        String gender = params.get("gender");

        ByteArrayOutputStream marksStream = new ByteArrayOutputStream();

        if (visitsByLocation[locationId] != null) {
            for (long[] visitLongs : visitsByLocation[locationId]) {
                if (visitLongs == null) {
                    continue;
                }


                if (Visit.getLocation(visitLongs) != locationId) {
                    continue;
                }
                if (fromDate != -1 && Visit.getVisited_at(visitLongs) <= fromDate) {
                    continue;
                }
                if (toDate != -1 && Visit.getVisited_at(visitLongs) >= toDate) {
                    continue;
                }
                byte[][] user = usersArray[Math.toIntExact(Visit.getUser(visitLongs))];

                if (fromAge != -1 && User.getBirth_date(user) >= getAgeTimestamp(fromAge)) {
                    continue;
                }
                if (toAge != -1 && User.getBirth_date(user) <= getAgeTimestamp(toAge)) {
                    continue;
                }
                if (!(gender == null || gender.equals("")) && !gender.equals(User.getGender(user))) {
                    continue;
                }
                marksStream.write(new byte[]{Visit.getMark(visitLongs)});
            }
        }

        byte[] marks = marksStream.toByteArray();
        if (marks.length == 0) {
            return 0;
        }
        double avg = 0;
        for (int i = 0; i < marks.length; i++) {
            avg += marks[i];
        }
        avg /= marks.length;

        return castRoundTo5(avg);
    }

    public static double castRoundTo5(double d) {
        return (long) (d * 100000 + 0.5) / 100000.0;
    }

    public int addUser(User user) {
        usersArray[user.id] = User.getBytes(user);
        return 200;
    }

    public int addLocation(Location location) {
        locationsArray[location.id] = Location.getBytes(location);
        return 200;
    }

    public int addVisit(Visit visit) {
        if (locationsArray[visit.location] == null) {
            return 404;
        }
        if (usersArray[visit.user] == null) {
            return 404;
        }
        long[] visitLongs = Visit.getLongs(visit);
        visitsArray[visit.id] = visitLongs;
        if (visitsByUser[visit.user] == null) {
            visitsByUser[visit.user] = new long[100][];
        }
        addToArray(visitsByUser[visit.user], visitLongs);
        if (visitsByLocation[visit.location] == null) {
            visitsByLocation[visit.location] = new long[1000][];
        }
        addToArray(visitsByLocation[visit.location], visitLongs);
        return 200;
    }



    public int updateUser(int id, JSONObject jsonObject) {
        byte[][] userBytes = usersArray[id];
        if (userBytes == null) {
            return 404;
        }

        if (jsonObject.containsKey("email")) {
            userBytes[1] = ((String) jsonObject.get("email")).getBytes();
        }
        if (jsonObject.containsKey("first_name")) {
            userBytes[2] = ((String) jsonObject.get("first_name")).getBytes(Charset.forName("UTF-8"));
        }
        if (jsonObject.containsKey("last_name")) {
            userBytes[3] = ((String) jsonObject.get("last_name")).getBytes(Charset.forName("UTF-8"));
        }
        if (jsonObject.containsKey("gender")) {
            userBytes[4] = ((String) jsonObject.get("gender")).getBytes();
        }
        if (jsonObject.containsKey("birth_date")) {
            userBytes[5] = String.valueOf((long) jsonObject.get("birth_date")).getBytes();
        }

        return 200;
    }

    public int updateLocation(int id, JSONObject jsonObject) {
        byte[][] locationBytes = locationsArray[id];
        if (locationBytes == null) {
            return 404;
        }

        if (jsonObject.containsKey("place")) {
            locationBytes[1] = ((String) jsonObject.get("place")).getBytes(Charset.forName("UTF-8"));
        }
        if (jsonObject.containsKey("country")) {
            locationBytes[2] = ((String) jsonObject.get("country")).getBytes(Charset.forName("UTF-8"));
        }
        if (jsonObject.containsKey("city")) {
            locationBytes[3] = ((String) jsonObject.get("city")).getBytes(Charset.forName("UTF-8"));
        }
        if (jsonObject.containsKey("distance")) {
            locationBytes[4] = String.valueOf((long) jsonObject.get("distance")).getBytes();
        }

        return 200;
    }

    public int updateVisit(int id, JSONObject jsonObject) {
        long[] visitLongs = visitsArray[id];
        if (visitLongs == null) {
            return 404;
        }


        if (jsonObject.containsKey("location")) {
            int location = Math.toIntExact((Long) jsonObject.get("location"));
            if (locationsArray[location] == null) {
                return 404;
            }
            long oldLocation = Visit.getLocation(visitLongs);

            visitLongs[1] = location;

            if (visitsByLocation[location] == null) {
                visitsByLocation[location] = new long[1000][];
            }
            addToArray(visitsByLocation[location], visitLongs);
            for(int i =0; i < visitsByLocation[(int)oldLocation].length; i ++) {
                if (visitsByLocation[(int)oldLocation][i] != null && visitsByLocation[(int)oldLocation][i][0] == id) {
                    visitsByLocation[(int)oldLocation][i] = null;
                    break;
                }
            }
        }
        if (jsonObject.containsKey("user")) {
            int user = Math.toIntExact((Long) jsonObject.get("user"));
            if (usersArray[user] == null) {
                return 404;
            }
            long oldUser = Visit.getUser(visitLongs);

            visitLongs[2] = user;

            if (visitsByUser[user] == null) {
                visitsByUser[user] = new long[100][];
            }
            addToArray(visitsByUser[user], visitLongs);
            for(int i =0; i < visitsByUser[(int)oldUser].length; i ++) {
                if (visitsByUser[(int)oldUser][i] != null && visitsByUser[(int)oldUser][i][0] == id) {
                    visitsByUser[(int)oldUser][i] = null;
                    break;
                }
            }
        }
        if (jsonObject.containsKey("visited_at")) {
            visitLongs[3] = (long) jsonObject.get("visited_at");
        }
        if (jsonObject.containsKey("mark")) {
            visitLongs[4] = (long) jsonObject.get("mark");
        }

        return 200;
    }

    public static long getAgeTimestamp(int age) {
        Calendar b = Calendar.getInstance(Locale.getDefault());
        b.set(Calendar.HOUR_OF_DAY, 0);
        b.add(YEAR, -age);
        return b.getTimeInMillis() / 1000;
    }
}
