package com.bastrich.entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author bastrich on 12.08.2017.
 */
public class AvgMark {

    private static final byte[] AVG = "{\"avg\":       }".getBytes();

    public static byte[] getJsonBytes(double avg) throws IOException {
        byte[] result = AVG.clone();
        byte[] avgBytes = String.valueOf(avg).getBytes();
        System.arraycopy(avgBytes, 0, result, 7, avgBytes.length);
        return result;
    }
}
