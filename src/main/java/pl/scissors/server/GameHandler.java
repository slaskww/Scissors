package pl.scissors.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private List<String> optionsToDraw;
    private HashMap<String, String> firstDefeatsSecond;
    private SecureRandom random;
    private String serversChoice;
    private String playerLogin;

    public GameHandler(Socket client) {

        this.socket = client;
        optionsToDraw = new ArrayList<>();
        firstDefeatsSecond = new HashMap<>();
        random = new SecureRandom();
        fillWithOptions();
    }

    @Override
    public void run() {


        try (Socket client = this.socket ){

            reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));
            playerLogin = reader.readLine();
            writer.println("\nServer: Welcome to the game " + playerLogin + "!\nServer: Choose one of the following options and try to win.\nServer: But...choose wisely, I know what you are thinking about.\n");
            writer.flush();
            String lineToSend;
            String lineToGet;

            while (true) {
                lineToGet = reader.readLine();
                System.out.println("Server: Player chose " + lineToGet);

                if (lineToGet.equals("QUIT")) {
                    System.out.println("Server: Player left the game\n");
                    closeClient();
                    break;
                }

                Results result = checkWhoWins(lineToGet);
                System.out.println("Result is: " + result.name());
                TimeUnit.MILLISECONDS.sleep(0);
                lineToSend = "Server: I have " + serversChoice + " so, You " + result.name();
                writer.println(lineToSend);
                writer.flush();

                System.out.println("Ranking\n");
                List<String> rank = ScissorsServer.getStats();

                if (rank.size() > 0){
                    for (int i = rank.size() -1; i >= 0 ;i--){
                        System.out.println(rank.get(i));
                    }

                }



            }


        } catch (IOException e) {
            System.out.println("I/O Exception");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void closeClient() {

        try {
            socket.shutdownInput();
            socket.shutdownOutput();



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void fillWithOptions() {
        optionsToDraw.add("PAPER");
        optionsToDraw.add("SCISSORS");
        optionsToDraw.add("STONE");

        firstDefeatsSecond.put("PAPER", "STONE"); //paper defeats stone
        firstDefeatsSecond.put("STONE", "SCISSORS"); //stone defeats scissors
        firstDefeatsSecond.put("SCISSORS", "PAPER"); //scissors defeats paper


    }

    private Results checkWhoWins(String playersChoice) {

        String serversChoice = optionsToDraw.get(random.nextInt(3));
        this.serversChoice = serversChoice;

        if (serversChoice.equals(playersChoice)) {
            return Results.DREW;
        }

        if (firstDefeatsSecond.get(serversChoice).equals(playersChoice)) {
            ScissorsServer.playerLose(playerLogin);
            return Results.LOST;
        }
        ScissorsServer.playerWin(playerLogin);
        return Results.WON;
    }
}
