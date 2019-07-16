package pl.scissors.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class GameHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public GameHandler(Socket client) {

        this.socket = client;
        run();

    }

    @Override
    public void run() {

        try(Socket client = socket){

            reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));

            writer.println("Hello client");
            writer.flush();

            String lineToSend;
            String lineToGet;

            while(true){
                lineToGet = reader.readLine();
                System.out.println(lineToGet);
                lineToSend = "ECHO: " + lineToGet;
                writer.println(lineToSend);
                writer.flush();
            }

        } catch(IOException e){
            System.out.println("Blad We/Wy");
            e.printStackTrace();
        }

    }
}
