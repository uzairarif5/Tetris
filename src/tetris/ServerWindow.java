package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ServerWindow extends Thread {

    public static boolean clientSide;
    public static boolean connection;

    public static JPanel startServerWindow(StartWindow titlewin, int portNum) {
        JPanel serverWin = new JPanel();
        serverWin.setBackground(new Color(10,10,80));
        serverWin.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel label = new JLabel("Waiting for other player",SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(new Color(100,100,250));
        label.setPreferredSize(new Dimension(250,40));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weighty = 0.4;
        gbc.anchor = GridBagConstraints.SOUTH;
        serverWin.add(label,gbc);
        JLabel codeLabel = new JLabel("Server Code: " + portNum,SwingConstants.CENTER);
        codeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        codeLabel.setForeground(new Color(100,100,250));
        codeLabel.setPreferredSize(new Dimension(250,30));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0.2;
        gbc.anchor = GridBagConstraints.NORTH;
        serverWin.add(codeLabel,gbc);
        JButton backB = new JButton("Back");
        backB.setFont(new Font("Arial", Font.PLAIN, 18));
        backB.setForeground(new Color(00,00,30));
        backB.setFocusPainted(false);
        backB.setPreferredSize(new Dimension(180,60));
        backB.setBackground(new Color(100,100,250));
        backB.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        backB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                titlewin.frame.add(titlewin.serverWin);
                titlewin.frame.remove(titlewin.startWin);
                titlewin.frame.setSize(300,400);
                if(!titlewin.ss.isClosed()) {
                    try {
                        titlewin.ss.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weighty = 0.4;
        gbc.anchor = GridBagConstraints.CENTER;
        serverWin.add(backB,gbc);
        return serverWin;
    }

    public static JPanel createLanWindow(StartWindow titlewin) {
        JPanel lanWin = new JPanel();
        lanWin.setBackground(new Color(10,10,80));
        lanWin.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel askLabel = new JLabel("Enter Server Code:");
        askLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        askLabel.setForeground(new Color(100,100,250));
        askLabel.setPreferredSize(new Dimension(250,40));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.5;
        gbc.anchor = GridBagConstraints.SOUTH;
        lanWin.add(askLabel,gbc);

        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 18));
        textField.setForeground(new Color(0,0,50));
        textField.setPreferredSize(new Dimension(200,40));
        textField.setBackground(new Color(150,150,250));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3,3,3,0,new Color(0,0,50)),
                BorderFactory.createEmptyBorder(0, 4, 0, 4)
        ));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.3;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        lanWin.add(textField,gbc);

        JButton goB = new JButton("Go");
        goB.setFont(new Font("Arial", Font.PLAIN, 18));
        goB.setForeground(new Color(0,0,30));
        goB.setFocusPainted(false);
        goB.setPreferredSize(new Dimension(40,40));
        goB.setBackground(new Color(100,100,250));
        goB.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 3));
        goB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    String input = textField.getText();
                    if(input.length() == 4 && titlewin.checkServer == false){
                        titlewin.checkServer = true;
                        titlewin.gamePanel = TwoPlayerGame.createGamePanel(titlewin);
                        titlewin.soc = new Socket("localhost",Integer.parseInt(input));
                        titlewin.din = new DataInputStream(titlewin.soc.getInputStream());
                        titlewin.dout = new DataOutputStream(titlewin.soc.getOutputStream());
                        Thread timer = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.sleep(6000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Thread checker = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (((String) titlewin.din.readUTF()).indexOf("TextToMakeSureRightPort") == 0 ){
                                        connection = true;
                                        timer.interrupt();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        connection = false;
                        checker.start();
                        timer.start();
                        timer.join();
                        if(!connection) {
                            throw new ArithmeticException();
                        }
                        titlewin.gpc = new GameplayCode(titlewin.nextPixel,titlewin.gamePanel);
                        GameplayCode.twoplayer = true;
                        ServerWindow.clientSide = true;
                        titlewin.frame.add(titlewin.gamePanel);
                        titlewin.frame.remove(titlewin.lanWin);
                        titlewin.frame.setSize(900,600);
                        Thread t1 = new Thread(new Runnable() {
                            @Override
                            public void run(){
                                titlewin.updatePanel2();
                            }
                        });
                        Thread t2 = new Thread(new Runnable() {
                            @Override
                            public void run(){
                                titlewin.startGameCode();
                            }
                        });
                        t1.start();
                        t2.start();
                        titlewin.checkServer = false;
                    }
                    else {
                        throw new Exception();
                    }
                }
                catch(ConnectException e1) {
                    JOptionPane.showMessageDialog(titlewin.frame,"No such server available.");
                    titlewin.checkServer = false;
                }
                catch(ArithmeticException e1) {
                    JOptionPane.showMessageDialog(titlewin.frame,"Time out error.\nCheck your code or your connection.");
                    titlewin.checkServer = false;
                }
                catch(Exception e1) {
                    JOptionPane.showMessageDialog(titlewin.frame,"Please check your code again.");
                    titlewin.checkServer = false;
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        lanWin.add(goB,gbc);

        JButton backB = new JButton("Back");
        backB.setFont(new Font("Arial", Font.PLAIN, 18));
        backB.setForeground(new Color(00,00,30));
        backB.setFocusPainted(false);
        backB.setPreferredSize(new Dimension(180,60));
        backB.setBackground(new Color(100,100,250));
        backB.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        backB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                titlewin.frame.add(titlewin.serverWin);
                titlewin.frame.remove(titlewin.lanWin);
                titlewin.frame.setSize(300,400);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0.2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0,0,20,0);
        lanWin.add(backB,gbc);

        return lanWin;
    }

    public static JPanel createServerWindow(StartWindow titlewin) {
        JPanel serverw = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        serverw.setBackground(new Color(10,10,80));
        serverw.setLayout(new GridBagLayout());

        JButton startB = new JButton("Start a Lan Server");
        startB.setFont(new Font("Arial", Font.PLAIN, 18));
        startB.setForeground(new Color(00,00,30));
        startB.setFocusPainted(false);
        startB.setPreferredSize(new Dimension(180,60));
        startB.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        startB.setBackground(new Color(100,100,250));
        startB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int portNum;
                Random rand = new Random();
                boolean connected = false;
                do {
                    portNum = rand.nextInt(9000) + 1000;
                    try {
                        titlewin.ss = new ServerSocket(portNum);
                        connected = true;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
                while(!connected);
                titlewin.startWin = ServerWindow.startServerWindow(titlewin, portNum);
                titlewin.frame.remove(titlewin.serverWin);
                titlewin.frame.add(titlewin.startWin);
                titlewin.frame.setSize(400,300);
                Thread serverThread = new Thread(new Runnable() {
                    @Override
                    public void run(){
                        try {
                            titlewin.soc = titlewin.ss.accept();
                            titlewin.din = new DataInputStream(titlewin.soc.getInputStream());
                            titlewin.dout = new DataOutputStream(titlewin.soc.getOutputStream());
                            titlewin.dout.writeUTF("TextToMakeSureRightPort");
                            titlewin.dout.flush();
                            ServerWindow.clientSide = false;
                            GameplayCode.twoplayer = true;
                            titlewin.gamePanel = TwoPlayerGame.createGamePanel(titlewin);
                            titlewin.gpc = new GameplayCode(titlewin.nextPixel,titlewin.gamePanel);
                            titlewin.frame.add(titlewin.gamePanel);
                            titlewin.frame.remove(titlewin.startWin);
                            titlewin.frame.setSize(900,600);
                            Thread t1 = new Thread(new Runnable() {
                                @Override
                                public void run(){
                                    titlewin.updatePanel2();
                                }
                            });
                            Thread t2 = new Thread(new Runnable() {
                                @Override
                                public void run(){
                                    titlewin.startGameCode();
                                }
                            });
                            t1.start();
                            t2.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                serverThread.start();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weighty = 0.3;
        gbc.anchor = GridBagConstraints.SOUTH;
        serverw.add(startB,gbc);

        JButton joinB = new JButton("Join a Lan Server");
        joinB.setFont(new Font("Arial", Font.PLAIN, 18));
        joinB.setForeground(new Color(00,00,30));
        joinB.setFocusPainted(false);
        joinB.setPreferredSize(new Dimension(180,60));
        joinB.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        joinB.setBackground(new Color(100,100,250));
        joinB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                titlewin.lanWin = ServerWindow.createLanWindow(titlewin);
                titlewin.frame.remove(titlewin.serverWin);
                titlewin.frame.add(titlewin.lanWin);
                titlewin.frame.setSize(400,300);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0.4;
        gbc.anchor = GridBagConstraints.CENTER;
        serverw.add(joinB,gbc);

        JButton backB = new JButton("Back");
        backB.setFont(new Font("Arial", Font.PLAIN, 18));
        backB.setForeground(new Color(00,00,30));
        backB.setFocusPainted(false);
        backB.setPreferredSize(new Dimension(180,60));
        backB.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        backB.setBackground(new Color(100,100,250));
        backB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                titlewin.frame.add(titlewin.titlePanel);
                titlewin.frame.remove(titlewin.serverWin);
                titlewin.frame.setSize(900, 550);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weighty = 0.3;
        gbc.anchor = GridBagConstraints.NORTH;
        serverw.add(backB,gbc);

        return serverw;
    }
}
