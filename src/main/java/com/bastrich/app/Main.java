package com.bastrich.app;

import com.bastrich.entities.Location;
import com.bastrich.entities.User;
import com.bastrich.entities.Visit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lingala.zip4j.core.ZipFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author bastrich on 12.08.2017.
 */
public class Main {

    public static ObjectMapper mapper = new ObjectMapper();

    public static byte[][][] usersArray = new byte[1000000][][];
    public static byte[][][] locationsArray = new byte[1000000][][];
    public static long[][] visitsArray = new long[3000000][];

    public static long[][][] visitsByUser = new long[1000000][][];
    public static long[][][] visitsByLocation = new long[1000000][][];


    public static void main(String[] args) throws Exception {


        uploadData();

        Server.main(null);

        try {

            for (int i = 1; i <= 100; i++) {
                sendGet("http://localhost/users/" + i);
            }

            for (int i = 1; i <= 100; i++) {
                sendGet("http://localhost/locations/" + i);
            }

            for (int i = 1; i <= 100; i++) {
                sendGet("http://localhost/visits/" + i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void uploadData() {


        String source = "/tmp/data/data.zip";
        String destination = "tmp";

        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);

//            usersIndex = new ObjectLockingIndexedCollection<>(OnHeapPersistence.onPrimaryKey(User.ID), 3);
//            usersIndex = new ConcurrentIndexedCollection<>(OnHeapPersistence.onPrimaryKey(User.ID));
//            usersIndex.addIndex(UniqueIndex.onAttribute(User.ID));

//            locationsIndex =  new ObjectLockingIndexedCollection<>(OnHeapPersistence.onPrimaryKey(Location.ID), 3);
//            locationsIndex = new ConcurrentIndexedCollection<>(OnHeapPersistence.onPrimaryKey(Location.ID));
//            locationsIndex.addIndex(UniqueIndex.onAttribute(Location.ID));
//            locationsIndex.addIndex(HashIndex.onAttribute(Location.COUNTRY));
//            locationsIndex.addIndex(NavigableIndex.onAttribute(Location.DISTANCE));

//            visitsIndex =  new ObjectLockingIndexedCollection<>(OnHeapPersistence.onPrimaryKey(Visit.ID), 3);
//            visitsIndex = new ConcurrentIndexedCollection<>(OnHeapPersistence.onPrimaryKey(Visit.ID));


            File folder = new File(destination);
            File[] listOfFiles = folder.listFiles();

            Arrays.sort(listOfFiles, Comparator.comparing((a) -> {
                if (a.getName().startsWith("users")) {
                    return 1;
                }
                if (a.getName().startsWith("locations")) {
                    return 2;
                }
                return 3;
            }));

            for (int i = 0; i < listOfFiles.length; i++) {
                File file = listOfFiles[i];

                if (file.isFile() && file.getName().endsWith(".json")) {
                    String content = new String(Files.readAllBytes(listOfFiles[i].toPath()), Charset.forName("UTF-8"));
                    content = content.substring(0, content.length());


                    if (file.getName().startsWith("users")) {
                        content = content.substring(10);
                        List<User> users = mapper.readValue(content, new TypeReference<List<User>>() {
                        });

                        users.forEach(user -> {
                            usersArray[user.id] = User.getBytes(user);
                        });
                    }

                    if (file.getName().startsWith("locations")) {
                        content = content.substring(14);
                        List<Location> locations = mapper.readValue(content, new TypeReference<List<Location>>() {
                        });

                        locations.forEach(location -> {
                            locationsArray[location.id] = Location.getBytes(location);
                        });
                    }

                    if (file.getName().startsWith("visits")) {
                        content = content.substring(11);
                        List<Visit> visits = mapper.readValue(content, new TypeReference<List<Visit>>() {
                        });

                        visits.forEach(visit -> {
                            long[] visitLongs = Visit.getLongs(visit);
                            visitsArray[visit.id] = visitLongs;
                            if (visitsByUser[visit.user] == null) {
                                visitsByUser[visit.user] = new long[1000][];
                            }
                            addToArray(visitsByUser[visit.user], visitLongs);
                            if (visitsByLocation[visit.location] == null) {
                                visitsByLocation[visit.location] = new long[1000][];
                            }
                            addToArray(visitsByLocation[visit.location], visitLongs);
                        });


                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static void addToArray(long[][] array, long[] item) {
        for(int i = 0; i <array.length;i++) {
            if (array[i] == null) {
                array[i] = item;
                return;
            }
        }
        throw new RuntimeException("AAAAAAA");
    }

    private static void sendGet(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        while (in.readLine() != null) {
        }
        in.close();
    }

}
