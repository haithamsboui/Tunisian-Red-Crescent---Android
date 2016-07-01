package com.esprit.redcrescentapp.Handlers;

import android.util.Log;

import org.json.JSONArray;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by haith on 24/11/2015.
 */
public class SocketHandler {
    private static Socket socket;
    public static JSONArray Members;

    private SocketHandler() {
        try {
            socket = IO.socket("https://crt-server-ibicha.c9users.io");
            socket.connect();

        } catch (Exception e) {
            Log.d("SOCKET.IO", "ERROR");
        }
    }


    public static Socket getSocketInstance() {
        if (socket == null) {
            try {
                socket = IO.socket("https://crt-server-ibicha.c9users.io");
                socket.connect();

            } catch (Exception e) {
                Log.d("SOCKET.IO", "ERROR");
            }

        }
        return socket;
    }

    public static JSONArray GetMembers() {
        return Members;
    }
}
