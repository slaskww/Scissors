package pl.scissors.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScissorsServer implements Runnable {


    private ServerSocket server;
    private int port;
    private ExecutorService exec;


    public ScissorsServer(int port) {

        try{
            this.server = new ServerSocket();
            this.server.bind(new InetSocketAddress("localhost", port));
            this.port = port;
            this.exec = Executors.newFixedThreadPool(5);
            run();

        } catch (BindException e){
            System.out.println("Nie udalo sie zbindowac serwera");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("Blad I/O");
            e.printStackTrace();
        }
    }


    @Override
    public void run() {

        System.out.println("Server started on port: " + port);

        while(true){

            try (Socket socket = server.accept()){
                System.out.println("Client " + socket.getLocalAddress().toString() + ":" + socket.getPort() + " connected: ");
                exec.execute(new GameHandler(socket));

            } catch (IOException e){
                System.out.println("Blad I/O");
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {

        ScissorsServer server = new ScissorsServer(8012);
        Thread runServer = new Thread(server, "Server");
        runServer.start();
    }


}