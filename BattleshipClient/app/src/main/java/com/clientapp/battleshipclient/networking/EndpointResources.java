//package com.clientapp.battleshipclient.networking;
//
//public class EndpointResources {
//
//    public static String ip = "10.0.2.2";
////    public static String ip = "192.168.1.181";
//
//    public static String signInEndpoint = "http://" + ip +":8080/signIn";
//    public static String signUpEndpoint = "http://" + ip +":8080/signUp";
////      public static String signUpEndpoint = "http://localhost:8080/signUp";
//
//    public static String getGameIdEndPoint = "http://" + ip +":8080/game"; // where i get gameId from the server (for a new session of game with a random opponent)
//    public static String getGameEndpoint = "http://" + ip +":8080/game/"; // where i get a game details and status
//
//    public static String putGameEndpoint = "http://" + ip +":8080/game/"; // where i tell the server about my game status
//
//    public static String postCreateBoardEndpoint = "http://" + ip +":8080/board"; // where i send a board to the server, when user clicks on "I'm ready" button
//    public static String postAttackEndpoint = "http://" + ip +":8080/attack";
//    public static String getAttackEndpoint = "http://" + ip +":8080/attack";
//
//    public static String getTopScoresEndpoint = "http://" + ip +":8080/getTopScores"; // where i get the top scores from the server
//
//
//}


package com.clientapp.battleshipclient.networking;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/*
*
*
* */

public class EndpointResources {

    private static final String TAG = "Endpoints";
    public static String path = "config.txt";
    public static String protocol = "http://";
    public static String port = ":8080";
    public static String ip;
    public static String prefix;
    public static String postSignInEndpoint;
    public static String postSignUpEndpoint;
    public static String getGameIdEndPoint; // where player enters waiting list to get gameId from the server (and a match with random opponent)
    public static String deleteGameEndpoint; // where player cancels being in waiting list
    public static String getGameEndpoint; // where player cancels being in waiting list
    public static String putGameEndpoint; // where player tells the server about game status //TODO not In use
    public static String postCreateBoardEndpoint; // where i send a board to the server, when user clicks on "I'm ready" button
    public static String postAttackEndpoint;// where player sends an attack to the server
    public static String getAttackEndpoint; // where player is being attacked- and gets the attck details
    public static String putEndGameEndpoint; // where player ends the game
    public static String putKeepGameAliveEndpoint; // where player keeps the game alive - notifying being still in the game
    public static String getScoresEndpoint; // where player gets the game status
    public static String getCurrentBoard;  // where player gets his whole board state
    public static String putPauseGameEndpoint;  // where player pauses the game
    public static String putResumeGameEndpoint; // where player resumes the game

    public final static String DEFAULT_IP = "10.0.2.2";
    public final static String COMPUTER_IP = "192.168.1.182";

    public final static String ROUTER_IP = "77.137.65.238";
    public final static String Xiaomi13T_hotspot_IP = "192.168.191.69";



    public static void initializeEndpoints(Context context) {
        ip = COMPUTER_IP;
//        ip = readIPFromFile(context, path); //everything starts here
        updateEndpoints();
    }

    private static String readIPFromFile(Context context, String path) {
        String ipAddress = null;
        boolean DoesExist = doesConfigFileExist(context, path);
        if (!DoesExist) {
            Log.d("DEBUG readIp", "readIPFromFile: " + "File does not exist");

            try {
                createConfigFileWithDefaults(context, path, DEFAULT_IP);
            } catch (IOException e)
            {
                Log.d("DEBUG readIp", "readIPFromFile: " + "Cant create file");
            }
            return null;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(context.getExternalFilesDir(null)+"/"+path))) {
            ipAddress = br.readLine();
            Log.d("DEBUG readIp", "readIPFromFile: " + ipAddress);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("DEBUG readIp", "readIPFromFile: " + e.getMessage());
        }
        return ipAddress;
    }

    private static boolean doesConfigFileExist(Context context, String path) {
        File file = new File(context.getExternalFilesDir(null), path);
        //log path
        String FullFilePath=context.getExternalFilesDir(null)+"/"+path;
        Log.d(TAG, "doesConfigFileExist: " + FullFilePath+ " "+file.exists());
        return file.exists() && !file.isDirectory();
    }

    private static void createConfigFileWithDefaults(Context context, String path, String DEFAULT_SETTINGS) throws IOException {
       String FullFilePath=context.getExternalFilesDir(null)+"/"+path;
        File configFile = new File(context.getExternalFilesDir(null), path);
        if (!configFile.exists()) {
            Log.d(TAG, "createConfigFileWithDefaults: succesfuly created config files");
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                fos.write(DEFAULT_SETTINGS.getBytes());
            } catch (IOException e) {
                Log.d(TAG, "createConfigFileWithDefaults: Failed to create write to config file");
            }
        } else {
            // Optionally, read the config file or notify that it already exists
            Log.d(TAG, "createConfigFileWithDefaults: Failed to create config files");
        }
    }


    private static void updateEndpoints() {

        prefix = protocol + ip + port;
        postSignInEndpoint = prefix + "/signIn";
        postSignUpEndpoint = prefix + "/signUp";
        getGameIdEndPoint = prefix + "/game";
        deleteGameEndpoint = prefix + "/opponent/";  // pathparam {userId}
        getGameEndpoint = prefix + "/game/"; // pathparam {gameId}
        putGameEndpoint = prefix + "/game/";
        postCreateBoardEndpoint = prefix + "/board";
        postAttackEndpoint = prefix + "/attack";
        getAttackEndpoint = prefix + "/attack";
        putEndGameEndpoint = prefix + "/endGame/";   // pathparam {gameId}
        putKeepGameAliveEndpoint = prefix + "/keepGameAlive/";  // pathparam {gameId}
        getScoresEndpoint = prefix + "/userStatistics"; //requestparam
        getCurrentBoard = prefix + "/board/";    // pathparam {boardId}
        putPauseGameEndpoint = prefix + "/pauseGame/"; // pathparam {gameId}
        putResumeGameEndpoint = prefix + "/resumeGame/"; //pathparam {gameId}
    }
}


