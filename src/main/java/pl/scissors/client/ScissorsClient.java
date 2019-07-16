package pl.scissors.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ScissorsClient extends JFrame implements Runnable, ActionListener {

    private Socket client;
    private BufferedReader reader;
    private PrintWriter writer;
    private int serverPort;
    private String playersChoice = "SCISSORS";
    JTextArea ta;

    public ScissorsClient(int serverPort) {

        try (Socket newSocket = new Socket()) {
            this.client = newSocket;
            this.serverPort = serverPort;
            this.ta = new JTextArea(20, 50);

            this.client.connect(new InetSocketAddress("localhost", serverPort));
            this.reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            this.writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));
            createWindow();
            run();

        } catch (IOException e) {
            System.out.println("Blad I/O");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {

            // String lineToSend = "PAPER";
            String lineToGet;

            lineToGet = reader.readLine();
            System.out.println(lineToGet);
            ta.append(lineToGet + "\n");


            while (true) {
                writer.println(playersChoice);
                writer.flush();
                lineToGet = reader.readLine();
                ta.append(lineToGet + "\n");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void createWindow(){
        setTitle("Scissors - Player's console");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);

        add(new JScrollPane(ta));

        JPanel panel = new JPanel();
        panel.setBackground(new Color(71, 89, 135));
        panel.setBounds(10, 10, 10, 10);
        JButton b = new JButton("Paper");
        b.addActionListener(this);
        panel.add(b);
        b = new JButton("Scissors");
        b.addActionListener(this);
        panel.add(b);
        b = new JButton("Stone");
        b.addActionListener(this);
        panel.add(b);
        add(panel, "South");
        pack();
        setVisible(true);

    }

    public static void main(String[] args) {

        ScissorsClient client = new ScissorsClient(8012);

        Thread runClient = new Thread(client);
        runClient.start();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        String cmd = actionEvent.getActionCommand();
        System.out.println(cmd);

        playersChoice = cmd.toUpperCase();


    }
}