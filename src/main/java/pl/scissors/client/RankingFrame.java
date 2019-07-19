package pl.scissors.client;

import javax.swing.*;
import java.awt.*;

public class RankingFrame extends JFrame {


    private JPanel panel;
    private JTextArea textArea;
    private JButton button;

    public RankingFrame(String rankingList) throws HeadlessException {

        createWindow(rankingList);
    }


    private void createWindow(String text) {

        setType( Type.POPUP );
        setTitle( "Overall ranking" );
        setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        setSize( 1000, 500 );
        setLocationRelativeTo( null );


        panel = new JPanel();
        panel.setBackground( new Color( 71, 89, 135 ) );
        setContentPane( panel );
        GridBagLayout gblContentPane = new GridBagLayout(); //tworzymy siatke o wymiarach 4 x 3
        gblContentPane.columnWidths = new int[]{10, 130, 40, 130, 10}; //cztery kolumny o indeksach 0..3
        gblContentPane.rowHeights = new int[]{20, 300, 40, 2}; //trzy wiersze o indeksach 0..2
        panel.setLayout( gblContentPane );

        textArea = new JTextArea();
        textArea.setEditable( false );
        textArea.setFont(new Font("Verdana", Font.PLAIN, 16));
        textArea.setMargin(new Insets(5, 10, 5, 5)); //top, left, bottom, right
        GridBagConstraints textFieldConstraints = new GridBagConstraints();
        textFieldConstraints.fill = GridBagConstraints.BOTH;
        textFieldConstraints.gridx = 1;
        textFieldConstraints.gridy = 1;
        textFieldConstraints.gridwidth = 3;
        textFieldConstraints.gridheight = 1;
        textFieldConstraints.weightx = 1;
        textFieldConstraints.weighty = 1;

        setText(text);
        panel.add( textArea, textFieldConstraints );

        button = new JButton( "Close" );
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
        buttonConstraints.gridx = 2;
        buttonConstraints.gridy = 2;
        buttonConstraints.gridheight = 1;
        buttonConstraints.gridwidth = 1;
        buttonConstraints.weightx = 0;
        buttonConstraints.weighty = 0;

        button.addActionListener( e -> dispose() );

        panel.add( button, buttonConstraints );

        pack();
        setVisible( true );

    }

 /*   public static void main(String[] args) {
        RankingFrame rank = new RankingFrame("Top 10 Players\n\n1. Ann\n2. Bob\n3. Annie\n4. Bobby\n5. Ann\n6. Bob\n7. Annie\n8. Bobby\n9. Bob\n10. Ann");
    }*/

    public void setText(String text) {
        textArea.setText( text );
    }

}
