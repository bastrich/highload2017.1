package com.bastrich.entities;

import com.bastrich.app.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author bastrich on 12.08.2017.
 */
public class Location {

    public int id;
    public String place;
    public String country;
    public String city;
    public int distance;

    private static final byte[] LOCATION = "{\"distance\":        ,\"city\":\"                                                                                                     ,\"place\":\"                                                                                                     ,\"id\":      ,\"country\":\"                                                                                                     }".getBytes();


    public static byte[] getJsonBytes(byte[][] location) throws IOException {
        if (location == null) {
            return null;
        }

        byte[] result = new byte[LOCATION.length];
        System.arraycopy(LOCATION, 0, result, 0, LOCATION.length);

        byte[] distanceBytes = location[4];
        System.arraycopy(distanceBytes, 0, result, 12, distanceBytes.length);
        byte[] cityBytes = location[3];
        System.arraycopy(cityBytes, 0, result, 29, cityBytes.length);
        result[29+cityBytes.length] = '"';
        byte[] placeBytes = location[1];
        System.arraycopy(placeBytes, 0, result, 140, placeBytes.length);
        result[140+placeBytes.length] = '"';
        byte[] idBytes = location[0];
        System.arraycopy(idBytes, 0, result, 247, idBytes.length);
        byte[] countryBytes = location[2];
        System.arraycopy(countryBytes, 0, result, 265, countryBytes.length);
        result[265+countryBytes.length] = '"';

        return result;
    }

    public static byte[][] getBytes(Location location) {
        byte[][] locationBytes = new byte[5][];
        locationBytes[0] = String.valueOf(location.id).getBytes();
        locationBytes[1] = location.place.getBytes(Charset.forName("UTF-8"));
        locationBytes[2] = location.country.getBytes(Charset.forName("UTF-8"));
        locationBytes[3] = location.city.getBytes(Charset.forName("UTF-8"));
        locationBytes[4] = String.valueOf(location.distance).getBytes();
        return locationBytes;
    }

    public static int getDistance(byte[][] location) {
        return Utils.parseInt(location[4]);
    }

    public static byte[] getCountry(byte[][] location) {
        return location[2];
    }

    public static String getPlace(byte[][] location) {
        return new String(location[1], Charset.forName("UTF-8"));
    }
}
