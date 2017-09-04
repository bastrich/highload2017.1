package com.bastrich.entities;

import com.bastrich.app.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author bastrich on 12.08.2017.
 */
public class User {

    public int id;
    public String email;
    public String first_name;
    public String last_name;
    public String gender;
    public Long birth_date;

    private static final byte[] USER = "{\"first_name\":\"                                                                                                     ,\"last_name\":\"                                                                                                     ,\"birth_date\":                    ,\"gender\":\" \",\"id\":      ,\"email\":\"                                                                                                     }".getBytes();

    public static byte[] getJsonBytes(byte[][] user) throws IOException {
        if (user == null) {
            return null;
        }

        byte[] result = new byte[USER.length];
        System.arraycopy(USER, 0, result, 0, USER.length);

        byte[] first_nameBytes = user[2];
        System.arraycopy(first_nameBytes, 0, result, 15, first_nameBytes.length);
        result[15+first_nameBytes.length] = '"';
        byte[] last_nameBytes = user[3];
        System.arraycopy(last_nameBytes, 0, result, 130, last_nameBytes.length);
        result[130+last_nameBytes.length] = '"';
        byte[] birth_dateBytes = user[5];
        System.arraycopy(birth_dateBytes, 0, result, 245, birth_dateBytes.length);
        byte[] genderBytes = user[4];
        System.arraycopy(genderBytes, 0, result, 276, genderBytes.length);
        byte[] idBytes = user[0];
        System.arraycopy(idBytes, 0, result, 284, idBytes.length);
        byte[] emailBytes = user[1];
        System.arraycopy(emailBytes, 0, result, 300, emailBytes.length);
        result[300+emailBytes.length] = '"';

        return result;
    }

    public static byte[][] getBytes(User user) {
        byte[][] userBytes = new byte[6][];
        userBytes[0] = String.valueOf(user.id).getBytes();
        userBytes[1] = user.email.getBytes();
        userBytes[2] = user.first_name.getBytes(Charset.forName("UTF-8"));
        userBytes[3] = user.last_name.getBytes(Charset.forName("UTF-8"));
        userBytes[4] = user.gender.getBytes();
        userBytes[5] = String.valueOf(user.birth_date).getBytes();
        return userBytes;
    }



    public static long getBirth_date(byte[][] user) {
        return Utils.parseLong(user[5]);
    }

    public static String getGender(byte[][] user) {
        return new String(user[4]);
    }

}
