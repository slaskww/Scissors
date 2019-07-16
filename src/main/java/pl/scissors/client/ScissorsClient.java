package pl.scissors.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ScissorsClient implements Runnable {

    private Socket client;
    private BufferedReader reader;
    private PrintWriter writer;
    private int serverPort;

    public ScissorsClient(int serverPort) {

        try (Socket newSocket = new Socket()) {
            client = newSocket;
            this.serverPort = serverPort;
            client.connect(new InetSocketAddress("localhost", serverPort));
            reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));
            run();

        } catch (IOException e) {
            System.out.println("Blad I/O");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {

            Scanner scanner = new Scanner(System.in);
            String lineToSend;
            String lineToGet;

            lineToGet = reader.readLine();
            System.out.println(lineToGet);

            while (true) {
                lineToSend = scanner.nextLine();
                writer.println(lineToSend);
                writer.flush();
                lineToGet = reader.readLine();
                System.out.println("Server: " + lineToGet);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {

        ScissorsClient client = new ScissorsClient(8012);

        Thread runClient = new Thread(client, "Client");
        runClient.start();
    }

}
