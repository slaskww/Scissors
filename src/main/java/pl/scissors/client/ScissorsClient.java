package pl.scissors.client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScissorsClient extends JFrame implements ActionListener {

    private Socket client;
    private Socket statsClient;
    private BufferedReader reader;
    private PrintWriter writer;
    private int serverPort;
    private String serverIpAddress;
    private String login = "";

    JTextArea ta;
    JPanel panel;
    JPanel panel2;

    public ScissorsClient(int serverPort, String serverIp) {

        this.client = new Socket();
        this.serverPort = serverPort;
        this.serverIpAddress = serverIp;
        this.ta = new JTextArea( 20, 50 );
        ta.setEditable(false);

        createWindow();
        connect();
        initiateGame();

    }

    private void connect() {

        try {

            login();
            ta.append( "~Attempting a connection to " + serverIpAddress + ":" + serverPort + "\n" );
            TimeUnit.SECONDS.sleep( 2 );
            this.client.connect( new InetSocketAddress( serverIpAddress, serverPort ) );
            this.reader = new BufferedReader( new InputStreamReader( client.getInputStream(), StandardCharsets.UTF_8 ) );
            this.writer = new PrintWriter( new OutputStreamWriter( client.getOutputStream(), StandardCharsets.UTF_8 ) );
            ta.append( "~Successfully connected to server.\n" );
            TimeUnit.SECONDS.sleep( 2 );
            writer.println( login ); //send login to the server
            writer.flush();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void connectToStats(){

        Runnable statsWindow = new Runnable() {
            @Override
            public void run() {
                try(Socket statsClient = new Socket()){

                    statsClient.connect(new InetSocketAddress(serverIpAddress, 8013));
                    BufferedReader statsReader = new BufferedReader(new InputStreamReader(statsClient.getInputStream(), StandardCharsets.UTF_8));

                    String statistics;
                    statistics = statsReader.readLine();
                   List<String> splittedMsg =  Arrays.asList(statistics.split("#"));
                    new RankingFrame(splittedMsg);
                    statsReader.close();

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        new Thread(statsWindow).start();

    }


    private synchronized void login() {

        LoginFrame loginFrame = new LoginFrame();
        this.login = loginFrame.getLogin();
        while (login.equals( "" )) {
            this.login = loginFrame.getLogin();
            try {
                TimeUnit.MILLISECONDS.sleep( 1 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        loginFrame.closeWindow();
        showMainWindow();
    }

    private void createWindow() {

        ta.setMargin(new Insets(5, 10, 5,5));
        JScrollPane scrollPane = new JScrollPane(ta);
        setType(Type.POPUP);
        setTitle( "Scissors - Player's console" );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setLocationRelativeTo(null);

        panel = new JPanel();
        panel2 = new JPanel();
        panel.setBackground( new Color( 71, 89, 135 ) );
        panel2.setBackground( new Color( 71, 89, 135 ) );
        panel2.setBounds( 10, 10, 10, 10 );

        GridBagLayout gbl = new GridBagLayout();
        gbl.columnWidths = new int[]{10, 400, 10};
        gbl.rowHeights = new int[]{10, 350, 30, 10};
        panel2.setLayout(gbl);

        GridBagConstraints textAreaConstraints = new GridBagConstraints();
        textAreaConstraints.fill = GridBagConstraints.BOTH; //Make the component fill its display area entirely
        textAreaConstraints.gridx = 1;
        textAreaConstraints.gridy = 1;
        textAreaConstraints.gridwidth = 1;
        textAreaConstraints.gridheight = 1;
        textAreaConstraints.weighty = 1;
        textAreaConstraints.weightx = 1;

        panel2.add(scrollPane, textAreaConstraints);

        GridBagConstraints panelConstrains = new GridBagConstraints();
        panelConstrains.fill = GridBagConstraints.BOTH;
        panelConstrains.gridx = 1;
        panelConstrains.gridy = 2;
        panelConstrains.gridheight = 1;
        panelConstrains.gridwidth = 3;
        panelConstrains.weightx = 0;
        panelConstrains.weighty = 0;

        JButton b = new JButton( "Paper" );
        b.addActionListener( this );
        panel.add( b );
        b = new JButton( "Scissors" );
        b.addActionListener( this );
        panel.add( b );
        b = new JButton( "Stone" );
        b.addActionListener( this );
        panel.add( b );
        b = new JButton( "Quit Game" );
        b.setActionCommand( "Quit" );
        b.addActionListener( this );
        panel.add( b );
        b = new JButton( "Show Rank" );
        b.setActionCommand( "Rank" );
        b.addActionListener( e -> connectToStats() );
        panel.add( b );
        panel.setVisible( false );
        panel2.add(panel, panelConstrains );
        add(panel2 );
        pack();
    }


    @Override
    public synchronized void actionPerformed(ActionEvent actionEvent) {

        String cmd = actionEvent.getActionCommand();
        System.out.println( cmd );
        String playersChoice = cmd.toUpperCase();
        String lineToGet;

        try {

            writer.println( playersChoice );
            writer.flush();
            ta.append( login + ": I choose " + playersChoice + "\n" );
            lineToGet = reader.readLine();
            ta.append( lineToGet + "\n" );

            if (cmd.equals( "Quit" )) {
                close();
                System.exit( 0 );
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

    private void initiateGame() {


        String lineToGet;
        try {
            lineToGet = reader.readLine();
            ta.append( lineToGet + "\n" );

            while (reader.ready()) {
                lineToGet = reader.readLine();
                ta.append( lineToGet + "\n" );
            }

            showButtons();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ScissorsClient client = new ScissorsClient( 8012, "127.0.0.1" );
    }

    private void showButtons() {
        panel.setVisible( true );
    }

    private void showMainWindow() {
        setVisible( true );
    }
}
