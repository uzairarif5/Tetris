package tetris;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.*;

public class StartWindow{
    public CardLayout card;
    public JFrame frame;
    public final int mainTitleW = 530;
    public final int mainTitleH = 250;
    public JPanel titlePanel;
    public JPanel gamePanel;
    public JPanel serverWin;
    public JPanel lanWin;
    public JPanel startWin;
    public JLabel score;
    public JLabel secondScore;
    public GameplayCode gpc;
    public JLabel[][] allPixel;
    public JLabel[][] otherPixel;
    public JLabel[][] nextPixel;
    public boolean checkServer;
    public ServerSocket ss;
    public Socket soc;
    public DataOutputStream dout;
    public DataInputStream din;

    public JPanel titleWindow() {
        StartWindow thisobj = this;
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel f = new JPanel();
        f.setBackground(new Color(10,10,80));
        f.setLayout(new GridBagLayout());

        ImageIcon mainTitle = new ImageIcon(new ImageIcon(StartWindow.class.getResource("/title.PNG")).getImage().getScaledInstance(this.mainTitleW, this.mainTitleH, Image.SCALE_DEFAULT));
        JLabel title = new JLabel(mainTitle);
        title.setOpaque(true);
        title.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 5));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(50,0,0,0);
        f.add(title,gbc);

        JButton startB = new JButton("Single Play");
        startB.setFont(new Font("Arial", Font.PLAIN, 20));
        startB.setForeground(new Color(00,00,30));
        startB.setFocusPainted(false);
        startB.setPreferredSize(new Dimension(150,60));
        startB.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        startB.setBackground(new Color(100,100,250));
        startB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                thisobj.gamePanel = GameWindow.createGamePanel(thisobj);
                thisobj.gpc = new GameplayCode(thisobj.nextPixel,thisobj.gamePanel);
                thisobj.frame.add(thisobj.gamePanel);
                try {
                    Thread t1 = new Thread(new Runnable() {
                        @Override
                        public void run(){
                            GameplayCode.twoplayer = false;
                            thisobj.frame.remove(thisobj.titlePanel);
                            thisobj.frame.setSize(500,600);
                            thisobj.updatePanel();
                        }
                    });
                    Thread t2 = new Thread(new Runnable() {
                        @Override
                        public void run(){
                            try {
                                thisobj.startGameCode();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    t1.start();
                    t2.start();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(20,0,60,0);
        gbc.anchor = GridBagConstraints.CENTER;
        f.add(startB,gbc);

        JButton LanB = new JButton("Play on LAN");
        LanB.setFont(new Font("Arial", Font.PLAIN, 20));
        LanB.setForeground(new Color(00,00,30));
        LanB.setFocusPainted(false);
        LanB.setPreferredSize(new Dimension(150,60));
        LanB.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        LanB.setBackground(new Color(100,100,250));
        LanB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                thisobj.serverWin = ServerWindow.createServerWindow(thisobj);
                thisobj.frame.add(thisobj.serverWin);
                thisobj.frame.remove(thisobj.titlePanel);
                thisobj.frame.setSize(300,400);
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.4;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(20,0,60,0);
        gbc.anchor = GridBagConstraints.CENTER;
        f.add(LanB,gbc);

        JButton quitB = new JButton("Quit");
        quitB.setFont(new Font("Arial", Font.PLAIN, 20));
        quitB.setForeground(new Color(00,00,30));
        quitB.setFocusPainted(false);
        quitB.setPreferredSize(new Dimension(150,60));
        quitB.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        quitB.setBackground(new Color(100,100,250));
        quitB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(20,0,60,0);
        gbc.anchor = GridBagConstraints.CENTER;
        f.add(quitB,gbc);

        return f;
    }

    public static void main(String args[]) {
        StartWindow sw = new StartWindow();

        sw.card = new CardLayout();
        sw.frame = new JFrame("Tetris by Uzair Arif");
        ImageIcon icon = new ImageIcon(StartWindow.class.getResource("/t.PNG"));
        sw.frame.setIconImage(icon.getImage());
        sw.frame.setLayout(sw.card);
        sw.frame.setSize(900, 550);
        sw.frame.setResizable(false);
        sw.titlePanel = sw.titleWindow();
        sw.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        sw.checkServer = false;
        sw.frame.add(sw.titlePanel);
        sw.frame.setVisible(true);
    }

    public void updatePanel() {
        while(!this.gpc.quit) {
            int curPixelR;
            int curPixelC;
            int curyOnBoard;
            for(curPixelR = 4; curPixelR< GameplayCode.totalRows; curPixelR++) {
                curyOnBoard = curPixelR - 4;
                for(curPixelC = 0; curPixelC< GameplayCode.totalCols; curPixelC++) {
                    switch (this.gpc.gameBoard[curPixelR][curPixelC]) {
                        case 'E':
                            this.allPixel[curyOnBoard][curPixelC].setBackground(Color.gray);
                            break;
                        case 'x':
                            this.allPixel[curyOnBoard][curPixelC].setBackground(Color.black);
                            break;
                        default:
                            this.allPixel[curyOnBoard][curPixelC].setBackground(PieceControl.pieceToColorList.get(this.gpc.gameBoard[curPixelR][curPixelC]));
                    }
                }
            }
        }
    }

    public void updatePanel2() {
        try {
            boolean once = true;
            while(!this.gpc.quit) {
                int curPixelR;
                int curPixelC;
                int curyOnBoard;
                boolean scored = false;
                String str = "";
                if(!this.gpc.lost) {
                    for(curPixelR = 4; curPixelR< GameplayCode.totalRows; curPixelR++) {
                        curyOnBoard = curPixelR - 4;
                        for(curPixelC = 0; curPixelC< GameplayCode.totalCols; curPixelC++) {
                            str += (this.gpc.gameBoard[curPixelR][curPixelC]);
                            switch (this.gpc.gameBoard[curPixelR][curPixelC]) {
                                case 'E':
                                    this.allPixel[curyOnBoard][curPixelC].setBackground(Color.gray);
                                    break;
                                case 'x':
                                    scored = true;
                                    this.allPixel[curyOnBoard][curPixelC].setBackground(Color.black);
                                    break;
                                default:
                                    this.allPixel[curyOnBoard][curPixelC].setBackground(PieceControl.pieceToColorList.get(this.gpc.gameBoard[curPixelR][curPixelC]));
                            }
                        }
                        str += "\n";
                    }
                }
                if (scored == true) {
                    this.dout.writeUTF("newScore" + this.gpc.personalScore);
                    this.dout.flush();
                    scored = false;
                }
                this.dout.writeUTF(str);
                this.dout.flush();
                curPixelR = 0;
                curPixelC = 0;
                try {
                    str = (String)this.din.readUTF();
                    if(str.indexOf("quit") > -1) {
                        this.gpc.quit = true;
                        this.gpc.extramsg = "The other player has quit.\nThe other player's score: " + str.substring(4) + "\n";
                        this.gpc.interrupt();
                    }
                    else if(str.indexOf("newScore") > -1) {
                        this.gpc.otherScore = str.substring(8);
                        this.secondScore.setText(" Score: " + str.substring(8));
                    }
                    else if(str.indexOf("otherLost") > -1) {
                        this.gpc.otherLost = true;
                        this.gpc.extramsg = "Opponent score: " + this.gpc.otherScore + "\n";
                    }
                    else if(str.indexOf("end") > -1) {
                        this.gpc.extramsg = "Opponent score: " + this.gpc.otherScore + "\n";
                        synchronized(this.gpc) {
                            this.gpc.notifyAll();
                        }
                    }
                    else {
                        for(char i:str.toCharArray()) {
                            if(i == '\n') {
                                curPixelR++;
                                curPixelC = 0;
                            }
                            else {
                                switch (i) {
                                    case 'E':
                                        this.otherPixel[curPixelR][curPixelC].setBackground(Color.gray);
                                        break;
                                    case 'x':
                                        this.otherPixel[curPixelR][curPixelC].setBackground(Color.black);
                                        break;
                                    default:
                                        this.otherPixel[curPixelR][curPixelC].setBackground(PieceControl.pieceToColorList.get(i));
                                }
                                curPixelC++;
                            }
                        }
                    }
                    if(this.gpc.lost) {
                        if(!this.gpc.otherLost) {
                            if(once == true) {
                                this.dout.writeUTF("otherLost" + this.gpc.personalScore);
                                this.dout.flush();
                                once = false;
                            }
                        }
                        else {
                            this.dout.writeUTF("end" + this.gpc.personalScore);
                            this.dout.flush();
                        }
                    }
                }
                catch (SocketException e) {
                    this.gpc.quit = true;
                    this.gpc.extramsg = "The other player has disconnected.\n Opponent Score:" + this.gpc.otherScore+ "\n";
                    this.gpc.interrupt();
                }
            }
            this.dout.writeUTF("quit" + this.gpc.personalScore);
            this.dout.flush();
            if(ServerWindow.clientSide == false) {
                this.ss.close();
            }
            this.soc.close();
            this.din.close();
            this.dout.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                if(ServerWindow.clientSide == false) {
                    this.ss.close();
                }
                this.soc.close();
                this.din.close();
                this.dout.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        synchronized(this.gpc) {
            this.gpc.notifyAll();
        }
        this.gpc.interrupt();
    }

    protected Integer startGameCode() {
        try {
            Thread.sleep(300);
            JOptionPane.showMessageDialog(this.frame,this.gpc.playGame(this.score));
            this.frame.remove(this.gamePanel);
            this.frame.add(this.titlePanel);
            this.frame.setSize(900,550);
            this.score.setText(" Score: 0");
        } catch (Exception excp) {
            excp.printStackTrace();
        }
        return 0;
    }

}
