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


package com.clientapp.battleshipclient.networking.NWutils;

/*
*   This class centralizes all the endpoints of the server
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
    public static String getGameEndpoint; // where player gets the game details and status
    public static String postCreateBoardEndpoint; // where i send a board to the server, when user clicks on "I'm ready" button
    public static String postAttackEndpoint;// where player sends an attack to the server
    public static String putEndGameEndpoint; // where player ends the game
    public static String putKeepGameAliveEndpoint; // where player keeps the game alive - notifying being still in the game
    public static String getScoresEndpoint; // where player gets the game status
    public static String getCurrentBoard;  // where player gets his whole board state
    public static String putPauseGameEndpoint;  // where player pauses the game
    public static String putResumeGameEndpoint; // where player resumes the game
    public static String putKeepUserAliveEndpoint; // where app keeps user signed in

    public final static String DEFAULT_IP = "10.0.2.2";
//    public final static String DEFAULT_IP = "192.168.1.182"; //TODO: change to the server's ip




    /*
    *  Method to initialize the endpoints
    *  with the current ip
    * */
    public static void initializeEndpoints( String chosenIp) {
        if (ip == null || ip.isEmpty())
            ip = DEFAULT_IP;
        else
            ip = chosenIp;
        updateEndpoints();
    }




    /*
    *  Method to update the endpoints
    *  with the current ip
    * */
    private static void updateEndpoints() {

        prefix = protocol + ip + port;
        postSignInEndpoint = prefix + "/signIn";
        postSignUpEndpoint = prefix + "/signUp";
        getGameIdEndPoint = prefix + "/game";
        deleteGameEndpoint = prefix + "/opponent/";  // pathparam {userId}
        getGameEndpoint = prefix + "/game/"; // pathparam {gameId}
        postCreateBoardEndpoint = prefix + "/board";
        postAttackEndpoint = prefix + "/attack";
        putEndGameEndpoint = prefix + "/endGame/";   // pathparam {gameId}
        putKeepGameAliveEndpoint = prefix + "/keepGameAlive/";  // pathparam {gameId}
        getScoresEndpoint = prefix + "/userStatistics"; //requestparam
        getCurrentBoard = prefix + "/board/";    // pathparam {boardId}
        putPauseGameEndpoint = prefix + "/pauseGame/"; // pathparam {gameId}
        putResumeGameEndpoint = prefix + "/resumeGame/"; //pathparam {gameId}
        putKeepUserAliveEndpoint = prefix + "/keepUserAlive/"; //pathparam {userId}
    }
}

