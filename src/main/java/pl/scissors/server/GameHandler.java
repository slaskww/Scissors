package pl.scissors.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GameHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private List<String> optionsToDraw;
    private HashMap<String, String> firstDefeatsSecond;
    private SecureRandom random;
    private String serversChoice;

    public GameHandler(Socket client) {

        this.socket = client;
        optionsToDraw = new ArrayList<>();
        firstDefeatsSecond = new HashMap<>();
        random = new SecureRandom();
        fillWithOptions();
        run();

    }

    @Override
    public void run() {

        try(Socket client = socket){

            reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));

            writer.println("Welcome to the game!\nChoose one of the following options and try to win.\nBut...choose wisely, I know what you are thinking about.\n");
            writer.flush();
            String lineToSend;
            String lineToGet;


            while(true){
                lineToGet = reader.readLine();
                System.out.println(lineToGet);
                Results result = checkWhoWins(lineToGet);
                TimeUnit.SECONDS.sleep(3);
                lineToSend = "Server: I have " + serversChoice + " so, You " + result.name();
                writer.println(lineToSend);
                writer.flush();
            }

        } catch(IOException e){
            System.out.println("Blad We/Wy");
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    private void fillWithOptions(){
        optionsToDraw.add("PAPER");
        optionsToDraw.add("SCISSORS");
        optionsToDraw.add("STONE");

        firstDefeatsSecond.put("PAPER", "STONE"); //paper defeats stone
        firstDefeatsSecond.put("STONE", "SCISSORS"); //stone defeats scissors
        firstDefeatsSecond.put("SCISSORS", "PAPER"); //scissors defeats paper


    }

    private Results checkWhoWins(String playersChoice){

        String serversChoice = optionsToDraw.get(random.nextInt(3));
        this.serversChoice = serversChoice;

        if (serversChoice.equals(playersChoice)){
            return Results.DREW;
        }

        if (firstDefeatsSecond.get(serversChoice).equals(playersChoice)){
            return Results.LOST;
        }

        return Results.WON;
    }
}
