package com.bastrich.app;

/**
 * @author bastrich on 19.08.2017.
 */


import com.bastrich.entities.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Arrays.asList;

public class Server {

    public static final Service service = new Service();

    public static void main(String[] args) throws Exception {

//        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(80), 10);
        serverSocketChannel.configureBlocking(true);

//        SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_READ);

        int port = 80;
        System.out.println("Dummy Server started on port " + port);

        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(3);
        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            threadPoolExecutor.submit(() -> serve(socketChannel));
        }
    }

    private static final byte[] HTML_200_START = ("HTTP/1.1 200 OK\r\n" +
            "Content-Type: application/json;charset=UTF-8\r\n" +
            "Connection: keep-alive\r\n" +
            "Content-Length: ").getBytes(StandardCharsets.ISO_8859_1);

    private static final byte[] HTML_200_H = ("\r\n\r\n").getBytes(StandardCharsets.ISO_8859_1);

    private static final byte[] HTML_404 = ("HTTP/1.1 404 Not Found\r\n" +
            "Content-Type: application/json;charset=UTF-8\r\n" +
            "Connection: keep-alive\r\n" +
            "Content-Length: 0").getBytes(StandardCharsets.ISO_8859_1);

    private static final byte[] HTML_400 = ("HTTP/1.1 400 Bad Request\r\n" +
            "Content-Type: application/json;charset=UTF-8\r\n" +
            "Connection: keep-alive\r\n" +
            "Content-Length: 0").getBytes(StandardCharsets.ISO_8859_1);

    static void serve(SocketChannel socketChannel) {
        try {
            socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

            finish:while (true) {
                byteBuffer.clear();
                int r = 0;


                long startKeepAlive = System.currentTimeMillis();
                while (r == 0) {
                    if (System.currentTimeMillis() - startKeepAlive > 1000) {
                        break finish;
                    }
                    r = socketChannel.read(byteBuffer);
                    if (r == -1) {
                        break finish;
                    }
                }

                byte[] buffer = byteBuffer.array();
                if (buffer[0] == 'G' && buffer[1] == 'E' && buffer[2] == 'T') {
                    if (buffer[5] == 'u') {
                        int id = 0;
                        boolean isVisits = false;
                        int paramIndex = -1;
                        for (int i = 11; ; i++) {
                            if (buffer[i] == '1') {
                                id = id * 10 + 1;
                            } else if (buffer[i] == '2') {
                                id = id * 10 + 2;
                            } else if (buffer[i] == '3') {
                                id = id * 10 + 3;
                            } else if (buffer[i] == '4') {
                                id = id * 10 + 4;
                            } else if (buffer[i] == '5') {
                                id = id * 10 + 5;
                            } else if (buffer[i] == '6') {
                                id = id * 10 + 6;
                            } else if (buffer[i] == '7') {
                                id = id * 10 + 7;
                            } else if (buffer[i] == '8') {
                                id = id * 10 + 8;
                            } else if (buffer[i] == '9') {
                                id = id * 10 + 9;
                            } else if (buffer[i] == '0') {
                                id = id * 10;
                            } else {
                                if (buffer[i] == '/') {
                                    isVisits = true;
                                    paramIndex = i + 7;
                                    break;
                                }
                                if (buffer[i] != ' ') {
                                    write404(socketChannel);
                                    continue finish;
                                }
                                break;
                            }
                        }
                        if (isVisits) {

                            Map<String, String> params = new HashMap<>();
                            if (buffer[paramIndex] == '?') {
                                params = parseParams(buffer, paramIndex + 1);
                            }


                            VisitedLocationList visitedLocationList = service.getVisits(id, params);
                            if (visitedLocationList == null) {
                                write404(socketChannel);
                                continue finish;
                            }
                            write200(socketChannel, VisitedLocationList.getJsonBytes(visitedLocationList.visits));
                            continue finish;
                        } else {
                            byte[] user = service.getUser(id);
                            if (user == null) {
                                write404(socketChannel);
                                continue finish;
                            }
                            write200(socketChannel, user);
                            continue finish;
                        }
                    } else if (buffer[5] == 'l') {
                        int id = 0;
                        boolean isAvg = false;
                        int paramIndex = -1;
                        for (int i = 15; ; i++) {
                            if (buffer[i] == '1') {
                                id = id * 10 + 1;
                            } else if (buffer[i] == '2') {
                                id = id * 10 + 2;
                            } else if (buffer[i] == '3') {
                                id = id * 10 + 3;
                            } else if (buffer[i] == '4') {
                                id = id * 10 + 4;
                            } else if (buffer[i] == '5') {
                                id = id * 10 + 5;
                            } else if (buffer[i] == '6') {
                                id = id * 10 + 6;
                            } else if (buffer[i] == '7') {
                                id = id * 10 + 7;
                            } else if (buffer[i] == '8') {
                                id = id * 10 + 8;
                            } else if (buffer[i] == '9') {
                                id = id * 10 + 9;
                            } else if (buffer[i] == '0') {
                                id = id * 10;
                            } else {
                                if (buffer[i] == '/') {
                                    isAvg = true;
                                    paramIndex = i + 4;
                                    break;
                                }
                                if (buffer[i] != ' ') {
                                    write404(socketChannel);
                                    continue finish;
                                }
                                break;
                            }
                        }
                        if (isAvg) {

                            Map<String, String> params = new HashMap<>();
                            if (buffer[paramIndex] == '?') {
                                params = parseParams(buffer, paramIndex + 1);

                                if (params.get("gender") != null && !params.get("gender").equals("m") && !params.get("gender").equals("f")) {
                                    write400(socketChannel);
                                    continue finish;
                                }
                            }


                            double avgMark = service.getAvgMark(id, params);
                            if (avgMark == -1) {
                                write404(socketChannel);
                                continue finish;
                            }
                            write200(socketChannel, AvgMark.getJsonBytes(avgMark));
                            continue finish;
                        } else {
                            byte[] location = service.getLocation(id);
                            if (location == null) {
                                write404(socketChannel);
                                continue finish;
                            }
                            write200(socketChannel, location);
                            continue finish;
                        }
                    } else {
                        int id = 0;
                        for (int i = 12; ; i++) {
                            if (buffer[i] == '1') {
                                id = id * 10 + 1;
                            } else if (buffer[i] == '2') {
                                id = id * 10 + 2;
                            } else if (buffer[i] == '3') {
                                id = id * 10 + 3;
                            } else if (buffer[i] == '4') {
                                id = id * 10 + 4;
                            } else if (buffer[i] == '5') {
                                id = id * 10 + 5;
                            } else if (buffer[i] == '6') {
                                id = id * 10 + 6;
                            } else if (buffer[i] == '7') {
                                id = id * 10 + 7;
                            } else if (buffer[i] == '8') {
                                id = id * 10 + 8;
                            } else if (buffer[i] == '9') {
                                id = id * 10 + 9;
                            } else if (buffer[i] == '0') {
                                id = id * 10;
                            } else {
                                if (buffer[i] != ' ') {
                                    write404(socketChannel);
                                    continue finish;
                                }
                                break;
                            }
                        }

                        byte[] visit = service.getVisit(id);
                        if (visit == null) {
                            write404(socketChannel);
                            continue finish;
                        }
                        write200(socketChannel, visit);
                        continue finish;
                    }
                } else {

                    JSONParser parser = new JSONParser();
                    JSONObject object;


                    if (buffer[6] == 'u') {
                        if (buffer[12] == 'n') {
                            try {
                                object = (JSONObject) parser.parse(readBody(buffer, 15, r));
                                if (!validateNewUser(object)) {
                                    write400(socketChannel);
                                    return;
                                }

                                User user = new User();
                                user.id = Math.toIntExact((Long) object.get("id"));
                                user.email = (String) object.get("email");
                                user.first_name = (String) object.get("first_name");
                                user.last_name = (String) object.get("last_name");
                                user.gender = (String) object.get("gender");
                                user.birth_date = (Long) object.get("birth_date");

                                writeCode(socketChannel, service.addUser(user));
                                return;


                            } catch (ParseException e) {
                                write400(socketChannel);
                                return;
                            }
                        } else {
                            int id = 0;
                            for (int i = 12; ; i++) {
                                if (buffer[i] == '1') {
                                    id = id * 10 + 1;
                                } else if (buffer[i] == '2') {
                                    id = id * 10 + 2;
                                } else if (buffer[i] == '3') {
                                    id = id * 10 + 3;
                                } else if (buffer[i] == '4') {
                                    id = id * 10 + 4;
                                } else if (buffer[i] == '5') {
                                    id = id * 10 + 5;
                                } else if (buffer[i] == '6') {
                                    id = id * 10 + 6;
                                } else if (buffer[i] == '7') {
                                    id = id * 10 + 7;
                                } else if (buffer[i] == '8') {
                                    id = id * 10 + 8;
                                } else if (buffer[i] == '9') {
                                    id = id * 10 + 9;
                                } else if (buffer[i] == '0') {
                                    id = id * 10;
                                } else {
                                    if (buffer[i] != ' ' && buffer[i] != '?') {
                                        write404(socketChannel);
                                        return;
                                    }
                                    break;
                                }
                            }

                            try {
                                object = (JSONObject) parser.parse(readBody(buffer, 20, r));
                            } catch (ParseException e) {
                                write400(socketChannel);
                                return;
                            }

                            if (!validateUpdateUser(object)) {
                                write400(socketChannel);
                                return;
                            }

                            writeCode(socketChannel, service.updateUser(id, object));
                            return;

                        }
                    } else if (buffer[6] == 'l') {
                        if (buffer[16] == 'n') {
                            try {
                                object = (JSONObject) parser.parse(readBody(buffer, 20, r));
                            } catch (ParseException e) {
                                write400(socketChannel);
                                return;
                            }

                            if (!validateNewLocation(object)) {
                                write400(socketChannel);
                                return;
                            }

                            Location location = new Location();
                            location.id = Math.toIntExact((Long) object.get("id"));
                            location.place = (String) object.get("place");
                            location.country = (String) object.get("country");
                            location.city = (String) object.get("city");
                            location.distance = Math.toIntExact((Long) object.get("distance"));

                            writeCode(socketChannel, service.addLocation(location));
                            return;
                        } else {
                            int id = 0;
                            for (int i = 16; ; i++) {
                                if (buffer[i] == '1') {
                                    id = id * 10 + 1;
                                } else if (buffer[i] == '2') {
                                    id = id * 10 + 2;
                                } else if (buffer[i] == '3') {
                                    id = id * 10 + 3;
                                } else if (buffer[i] == '4') {
                                    id = id * 10 + 4;
                                } else if (buffer[i] == '5') {
                                    id = id * 10 + 5;
                                } else if (buffer[i] == '6') {
                                    id = id * 10 + 6;
                                } else if (buffer[i] == '7') {
                                    id = id * 10 + 7;
                                } else if (buffer[i] == '8') {
                                    id = id * 10 + 8;
                                } else if (buffer[i] == '9') {
                                    id = id * 10 + 9;
                                } else if (buffer[i] == '0') {
                                    id = id * 10;
                                } else {
                                    if (buffer[i] != ' ' && buffer[i] != '?') {
                                        write404(socketChannel);
                                        return;
                                    }
                                    break;
                                }
                            }
                            try {
                                object = (JSONObject) parser.parse(readBody(buffer, 22, r));
                            } catch (ParseException e) {
                                write400(socketChannel);
                                return;
                            }

                            if (!validateUpdateLocation(object)) {
                                write400(socketChannel);
                                return;
                            }

                            writeCode(socketChannel, service.updateLocation(id, object));
                            return;

                        }
                    } else {
                        if (buffer[13] == 'n') {
                            try {
                                object = (JSONObject) parser.parse(readBody(buffer, 15, r));
                            } catch (ParseException e) {
                                write400(socketChannel);
                                return;
                            }

                            if (!validateNewVisit(object)) {
                                write400(socketChannel);
                                return;
                            }

                            Visit visit = new Visit();
                            visit.id = Math.toIntExact((Long) object.get("id"));
                            visit.location = Math.toIntExact((Long) object.get("location"));
                            visit.user = Math.toIntExact((Long) object.get("user"));
                            visit.visited_at = (Long) object.get("visited_at");
                            visit.mark = Math.toIntExact((Long) object.get("mark"));


                            writeCode(socketChannel, service.addVisit(visit));
                            return;

                        } else {
                            int id = 0;
                            for (int i = 13; ; i++) {
                                if (buffer[i] == '1') {
                                    id = id * 10 + 1;
                                } else if (buffer[i] == '2') {
                                    id = id * 10 + 2;
                                } else if (buffer[i] == '3') {
                                    id = id * 10 + 3;
                                } else if (buffer[i] == '4') {
                                    id = id * 10 + 4;
                                } else if (buffer[i] == '5') {
                                    id = id * 10 + 5;
                                } else if (buffer[i] == '6') {
                                    id = id * 10 + 6;
                                } else if (buffer[i] == '7') {
                                    id = id * 10 + 7;
                                } else if (buffer[i] == '8') {
                                    id = id * 10 + 8;
                                } else if (buffer[i] == '9') {
                                    id = id * 10 + 9;
                                } else if (buffer[i] == '0') {
                                    id = id * 10;
                                } else {
                                    if (buffer[i] != ' ' && buffer[i] != '?') {
                                        write404(socketChannel);
                                        return;
                                    }
                                    break;
                                }
                            }

                            try {
                                object = (JSONObject) parser.parse(readBody(buffer, 17, r));
                            } catch (ParseException e) {
                                write400(socketChannel);
                                return;
                            }


                            if (!validateUpdateVisit(object)) {
                                write400(socketChannel);
                                return;
                            }

                            writeCode(socketChannel, service.updateVisit(id, object));
                            return;


                        }
                    }
                }
            }


        } catch (NumberFormatException e) {
            try {
                write400(socketChannel);
            } catch (IOException e1) {
                e1.printStackTrace();
                throw new RuntimeException(e1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                write400(socketChannel);
            } catch (IOException e1) {
                e1.printStackTrace();
                throw new RuntimeException(e1);
            }
        } finally {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private static void write200(SocketChannel sc, byte[] bytes) throws IOException {
        byte[] contentLength = String.valueOf(bytes.length).getBytes();
        ByteBuffer bb = ByteBuffer.allocate(HTML_200_START.length + contentLength.length + HTML_200_H.length + bytes.length);
        bb.put(HTML_200_START);
        bb.put(contentLength);
        bb.put(HTML_200_H);
        bb.put(bytes);
        bb.flip();
        sc.write(bb);
    }

    private static void write404(SocketChannel sc) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(HTML_404.length);
        bb.put(HTML_404);
        bb.flip();
        sc.write(bb);
    }

    private static void write400(SocketChannel sc) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(HTML_400.length);
        bb.put(HTML_400);
        bb.flip();
        sc.write(bb);
    }

    private static void writeCode(SocketChannel sc, int code) throws IOException {
        if (code == 400) {
            write400(sc);
        } else if (code == 404) {
            write404(sc);
        } else if (code == 200) {
            write200(sc, "{}".getBytes());
        }
    }

    private static String readBody(byte[] buffer, int startIndex, int maxIndex) {
        byte[] result = new byte[600];
        for (int i = startIndex; i <= maxIndex; i++) {
            if (buffer[i] == '\r' && buffer[i + 1] == '\n' && buffer[i + 2] == '\r' && buffer[i + 3] == '\n') {
                startIndex = i + 4;
                break;
            }
        }

        for (int i = startIndex; i <= maxIndex; i++) {
            if (buffer[i] == 0) {
                break;
            }

            result[i - startIndex] = buffer[i];
        }

        return new String(result, Charset.forName("UTF-8")).trim();
    }

    private static boolean validateNewUser(JSONObject object) {
        return object.keySet().size() == 6
                && object.containsKey("id") && object.get("id") != null && object.get("id") instanceof Long
                && object.containsKey("email") && object.get("email") != null && object.get("email") instanceof String
                && object.containsKey("first_name") && object.get("first_name") != null && object.get("first_name") instanceof String
                && object.containsKey("last_name") && object.get("last_name") != null && object.get("last_name") instanceof String
                && object.containsKey("gender") && object.get("gender") != null && object.get("gender") instanceof String && (object.get("gender").equals("m") || object.get("gender").equals("f"))
                && object.containsKey("birth_date") && object.get("birth_date") != null && object.get("birth_date") instanceof Long;
    }

    private static boolean validateUpdateUser(JSONObject object) {
        for (Object keyObj : object.keySet()) {
            String key = (String) keyObj;
            if (key.equals("id")) {
                return false;
            }
            if (object.get(key) == null) {
                return false;
            }
            if (!asList("email", "first_name", "last_name", "gender", "birth_date").contains(key)) {
                return false;
            }
            if (key.equals("gender") && object.get("gender") != null && !object.get("gender").equals("m") && !object.get("gender").equals("f")) {
                return false;
            }
        }
        return true;
    }

    private static boolean validateNewLocation(JSONObject object) {
        return object.keySet().size() == 5
                && object.containsKey("id") && object.get("id") != null && object.get("id") instanceof Long
                && object.containsKey("place") && object.get("place") != null && object.get("place") instanceof String
                && object.containsKey("country") && object.get("country") != null && object.get("country") instanceof String
                && object.containsKey("city") && object.get("city") != null && object.get("city") instanceof String
                && object.containsKey("distance") && object.get("distance") != null && object.get("distance") instanceof Long;
    }

    private static boolean validateUpdateLocation(JSONObject object) {
        for (Object keyObj : object.keySet()) {
            String key = (String) keyObj;
            if (key.equals("id")) {
                return false;
            }
            if (object.get(key) == null) {
                return false;
            }
            if (!asList("place", "country", "city", "distance").contains(key)) {
                return false;
            }
        }
        return true;
    }

    private static boolean validateNewVisit(JSONObject object) {
        return object.keySet().size() == 5
                && object.containsKey("id") && object.get("id") != null && object.get("id") instanceof Long
                && object.containsKey("location") && object.get("location") != null && object.get("location") instanceof Long
                && object.containsKey("user") && object.get("user") != null && object.get("user") instanceof Long
                && object.containsKey("visited_at") && object.get("visited_at") != null && object.get("visited_at") instanceof Long
                && object.containsKey("mark") && object.get("mark") != null && object.get("mark") instanceof Long;
    }

    private static boolean validateUpdateVisit(JSONObject object) {
        for (Object keyObj : object.keySet()) {
            String key = (String) keyObj;
            if (key.equals("id")) {
                return false;
            }
            if (object.get(key) == null) {
                return false;
            }
            if (!asList("location", "user", "visited_at", "mark").contains(key)) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, String> parseParams(byte[] buffer, int startIndex) {
        Map<String, String> params = new HashMap<>();

        byte[] queryBytes = new byte[250];

        for (int i = startIndex; buffer[i] != ' '; i++) {
            queryBytes[i - startIndex] = buffer[i];
        }

        String query = new String(queryBytes, Charset.forName("UTF-8")).trim();

        if (query == null || query.equals("")) {
            return params;
        }

        String[] paramArray = query.split("&");

        for (int i = 0; i < paramArray.length; i++) {
            String[] param = paramArray[i].split("=");
            params.put(param[0], param[1]);
        }

        return params;
    }
}