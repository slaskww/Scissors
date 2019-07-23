package pl.scissors.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ScissorsServer implements Runnable {


    private ServerSocket gameServer;
    private ServerSocket statsServer;
    private int port;
    private ExecutorService exec;
    public static ConcurrentHashMap<String, Scores> scores = new ConcurrentHashMap<>(); //ConcurrentHashMap class is thread-safe i.e. multiple thread can operate on a single object without any complications


    public ScissorsServer(int port) {
        try {
            this.gameServer = new ServerSocket();
            this.gameServer.bind( new InetSocketAddress( "localhost", port ) );

            this.statsServer = new ServerSocket( 8013 );

            this.port = port;
            this.exec = Executors.newFixedThreadPool( 5 );
            run();

        } catch (BindException e) {
            System.out.println( "Nie udalo sie zbindowac serwera" );
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println( "Blad I/O" );
            e.printStackTrace();
        }
    }


    @Override
    public void run() {

        System.out.println( "Server started on port: " + port );
        handleStatsRequest();

        while (true) {

            try {
                Socket socket = gameServer.accept();
                System.out.println( "Client " + socket.getLocalAddress().toString() + ":" + socket.getPort() + " connected: " );
                exec.execute( new GameHandler( socket ) );

                System.out.println( "Za exec:" );
            } catch (IOException e) {
                System.out.println( "Blad I/O" );
                e.printStackTrace();
            }

        }
    }



    public static void playerWin(String login) {
        scores.putIfAbsent( login, new Scores( 0, 0 ) );

     /*If the specified key is not already associated with a
      (non-null) value, associates it with the given value (scores.get(login)).
      Otherwise, replaces the value with the results of the given
      remapping function*/

        scores.merge( login, scores.get( login ), (v1, v2) -> new Scores( v1.getWinnings() + 1, v1.getLosses() ) );
        System.out.println( login + ": " + scores.get( login ).toString() );

    }

    public static void playerLose(String login) {
        scores.putIfAbsent( login, new Scores( 0, 0 ) );
        scores.merge( login, scores.get( login ), (v1, v2) -> new Scores( v1.getWinnings(), v1.getLosses() + 1 ) );
        System.out.println( login + ": " + scores.get( login ).toString() );
    }

    private void handleStatsRequest() {

        Runnable statsRequestThread = new Runnable() {
            @Override
            public void run() {

                while (true) {

                    try {
                        Socket player;
                        player = statsServer.accept();
                        System.out.println( "New request from player " + player.getLocalAddress().toString() + ":" + player.getPort() );
                        PrintWriter statsWriter = new PrintWriter( new OutputStreamWriter( player.getOutputStream(), StandardCharsets.UTF_8 ) );

                        List<String> stats = getStats();
                        statsWriter.append("Top players:#");


                            for (int i = stats.size() -1; i >= 0 ;i--){
                                statsWriter.append(stats.get(i)).append("#");
                            }

                        statsWriter.flush();
                        player.shutdownOutput();
                        statsWriter.close();
                        player.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        };

        new Thread( statsRequestThread ).start();
    }


    public static List<String> getStats() {

        return scores.entrySet().stream()
                .sorted( Comparator.comparing( (v) -> v.getValue().getWinnings() ) )
                .limit( 10 )
                .map( e -> e.getKey() + ", winnings: " + e.getValue().getWinnings() + ", losses: " + e.getValue().getLosses() )
                .collect( Collectors.toList() );

    }

    public static void main(String[] args) {

        ScissorsServer server = new ScissorsServer( 8012 );
        Thread runServer = new Thread( server, "Server" );
        runServer.start();
    }
}