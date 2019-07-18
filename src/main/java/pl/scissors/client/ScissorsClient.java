package pl.scissors.client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class ScissorsClient extends JFrame implements ActionListener {

    private Socket client;
    private BufferedReader reader;
    private PrintWriter writer;
    private int serverPort;
    private String serverIpAddress;
    JTextArea ta;

    public ScissorsClient(int serverPort, String serverIp) {


        this.client = new Socket();
        this.serverPort = serverPort;
        this.serverIpAddress = serverIp;
        this.ta = new JTextArea(20, 50);
        createWindow();
        connect();
        initiateGame();

    }

    private void connect() {

        try {
            ta.append("~Attempting a connection to " + serverIpAddress + ":" + serverPort + "\n");
            TimeUnit.SECONDS.sleep(2);
            this.client.connect(new InetSocketAddress(serverIpAddress, serverPort));
            this.reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            this.writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));
            ta.append("~Successfully connected to server.\n");
            TimeUnit.SECONDS.sleep(2);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createWindow() {
        setTitle("Scissors - Player's console");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);

        add(new JScrollPane(ta));

        JPanel panel = new JPanel();
        // panel.setLayout(new BorderLayout());
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
        b = new JButton("Quit Game");
        b.setActionCommand("Quit");
        b.addActionListener(this);
        panel.add(b);
        add(panel, "South");
        pack();
        setVisible(true);

    }


    @Override
    public synchronized void actionPerformed(ActionEvent actionEvent) {

        String cmd = actionEvent.getActionCommand();
        System.out.println(cmd);
        String playersChoice = cmd.toUpperCase();
        String lineToGet;

        try {

            writer.println(playersChoice);
            writer.flush();
            ta.append("Player: I choose " + playersChoice + "\n");
            lineToGet = reader.readLine();
            ta.append(lineToGet + "\n");

            if (cmd.equals("Quit")) {
                close();
                System.exit(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void close() {
        try {
            client.shutdownInput();
            client.shutdownOutput();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initiateGame()  {

        System.out.println("in initiateGame()");


        String lineToGet;
        try {
            lineToGet = reader.readLine();
            ta.append(lineToGet + "\n");

            while(reader.ready()){
                lineToGet = reader.readLine();
                ta.append(lineToGet + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ScissorsClient client = new ScissorsClient(8012, "127.0.0.1");
    }

}
