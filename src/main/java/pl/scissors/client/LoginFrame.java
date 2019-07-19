package pl.scissors.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class LoginFrame extends JFrame {

    private JTextField txtLogin;
    private JLabel lblLogin;
    private JButton btnLogin;
    private String login = "";


    public LoginFrame() throws HeadlessException {
        createWindow();
    }


    private void createWindow(){

        setType(Type.POPUP);
        setTitle("User login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(); //na panelu umiejscowiamy buttony i labele
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setBackground(new Color(71, 89, 135));
        setContentPane(panel);
        panel.setLayout(null);

        txtLogin = new JTextField();
        txtLogin.setBounds(65, 50, 160, 33);
        panel.add( txtLogin );
        lblLogin = new JLabel("Enter your login:");
        lblLogin.setBounds(95, 22, 100, 18);
        lblLogin.setForeground(new Color(255, 255, 255));
        panel.add( lblLogin );
        lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
        btnLogin = new JButton("Enter");
        btnLogin.setBounds(105, 100, 80, 33);
        btnLogin.addActionListener( e -> {

            if (txtLogin.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Please enter your login");

            } else{
                this.login = txtLogin.getText();
            }



        } );
        panel.add(btnLogin);
        setVisible(true);
    }


    public void closeWindow(){
        this.dispose();
    }

    public String getLogin(){
        return this.login;
    }
}
