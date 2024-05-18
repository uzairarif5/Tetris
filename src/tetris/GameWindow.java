package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class GameWindow {

    public static JPanel createGamePanel(StartWindow sw) {
        JPanel gamePanel = new JPanel();
        gamePanel.setBackground(new Color(20,20,60));
        gamePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        ImageIcon gameTitle = new ImageIcon(new ImageIcon(StartWindow.class.getResource("/gameTitle.PNG")).getImage().getScaledInstance(340,100, Image.SCALE_DEFAULT));
        JLabel title = new JLabel(gameTitle);
        title.setOpaque(true);
        title.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,new Color(10,10,40), Color.black));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.2;
        gbc.gridheight = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.SOUTH;
        gamePanel.add(title,gbc);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridBagLayout());
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.black,4));
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.insets = new Insets(0, 0, 0, 0);

        int curPixelR;
        int curPixelC;
        sw.allPixel = new JLabel[GameplayCode.totalRows-4][GameplayCode.totalCols];
        for(curPixelR = 0; curPixelR< GameplayCode.totalRows-4; curPixelR++) {
            for(curPixelC = 0; curPixelC< GameplayCode.totalCols; curPixelC++) {
                JLabel pixel = new JLabel();
                pixel.setBorder(BorderFactory.createLineBorder(Color.black,1));
                pixel.setOpaque(true);
                sw.allPixel[curPixelR][curPixelC] = pixel;
                gbcLabel.gridx = curPixelC;
                gbcLabel.gridy = curPixelR+1;
                gbcLabel.ipadx = 15;
                gbcLabel.ipady = 15;
                gbcLabel.gridwidth = 1;
                gbcLabel.gridheight = 1;
                boardPanel.add(pixel,gbcLabel);
            }
        }
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 0.8;
        gbc.gridwidth = 1;
        gbc.gridheight = 5;

        sw.score = new JLabel(" Score: 0");
        sw.score.setBackground(Color.DARK_GRAY);
        sw.score.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.black));
        sw.score.setOpaque(true);
        sw.score.setPreferredSize(new Dimension(GameplayCode.totalCols*17,30));
        sw.score.setForeground(Color.white);
        sw.score.setFont(new Font("Arial", Font.PLAIN, 20));
        gbcLabel.gridx = 0;
        gbcLabel.gridy = 0;
        gbcLabel.ipadx = 0;
        gbcLabel.ipady = 0;
        gbcLabel.gridwidth = GameplayCode.totalCols+1;
        gbcLabel.gridheight = 1;
        boardPanel.add(sw.score,gbcLabel);
        gbc.anchor = GridBagConstraints.CENTER;
        gamePanel.add(boardPanel,gbc);

        JPanel nextPanel = new JPanel();
        nextPanel.setLayout(new GridBagLayout());
        nextPanel.setBorder(BorderFactory.createLineBorder(Color.black,4));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        gbc.weighty = 0.3;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        sw.nextPixel = new JLabel[5][5];
        for(curPixelR = 0;curPixelR<5;curPixelR++) {
            for(curPixelC = 0;curPixelC<5;curPixelC++) {
                JLabel pixel = new JLabel();
                pixel.setBorder(BorderFactory.createLineBorder(Color.black,1));
                pixel.setBackground(Color.gray);
                pixel.setOpaque(true);
                sw.nextPixel[curPixelR][curPixelC] = pixel;
                gbcLabel.gridx = curPixelC;
                gbcLabel.gridy = curPixelR;
                gbcLabel.ipadx = 15;
                gbcLabel.ipady = 15;
                gbcLabel.gridwidth = 1;
                gbcLabel.gridheight = 1;
                nextPanel.add(pixel,gbcLabel);
            }
        }
        gamePanel.add(nextPanel,gbc);

        ImageIcon leftRPic = new ImageIcon(new ImageIcon(StartWindow.class.getResource("/leftR.png")).getImage().getScaledInstance(42,42, Image.SCALE_DEFAULT));
        JButton leftOrient = new JButton(leftRPic);
        leftOrient.setForeground(new Color(00,00,30));
        leftOrient.setFocusPainted(false);
        leftOrient.setPreferredSize(new Dimension(50,50));
        leftOrient.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        leftOrient.setBackground(new Color(100,100,250));
        leftOrient.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    sw.gpc.rotate(4);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0,0,1,1);
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gamePanel.add(leftOrient,gbc);

        ImageIcon rightRPic = new ImageIcon(new ImageIcon(StartWindow.class.getResource("/rightR.png")).getImage().getScaledInstance(42,42, Image.SCALE_DEFAULT));
        JButton rightOrient = new JButton(rightRPic);
        rightOrient.setForeground(new Color(00,00,30));
        rightOrient.setFocusPainted(false);
        rightOrient.setPreferredSize(new Dimension(50,50));
        rightOrient.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        rightOrient.setBackground(new Color(100,100,250));
        rightOrient.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    sw.gpc.rotate(5);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0,1,1,0);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gamePanel.add(rightOrient,gbc);

        ImageIcon leftPic = new ImageIcon(new ImageIcon(StartWindow.class.getResource("/left.png")).getImage().getScaledInstance(50,50, Image.SCALE_DEFAULT));
        JButton goLeft = new JButton(leftPic);
        goLeft.setForeground(new Color(00,00,30));
        goLeft.setFocusPainted(false);
        goLeft.setPreferredSize(new Dimension(50,50));
        goLeft.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        goLeft.setBackground(new Color(100,100,250));
        goLeft.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    sw.gpc.moveSide(2);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(1,0,0,1);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gamePanel.add(goLeft,gbc);

        ImageIcon rightPic = new ImageIcon(new ImageIcon(StartWindow.class.getResource("/right.png")).getImage().getScaledInstance(50,50, Image.SCALE_DEFAULT));
        JButton goRight = new JButton(rightPic);
        goRight.setForeground(new Color(00,00,30));
        goRight.setFocusPainted(false);
        goRight.setPreferredSize(new Dimension(50,50));
        goRight.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        goRight.setBackground(new Color(100,100,250));
        goRight.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    sw.gpc.moveSide(3);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(1,1,0,0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gamePanel.add(goRight,gbc);

        JButton pauseB = new JButton("Pause");
        pauseB.setFont(new Font("Arial", Font.PLAIN, 20));
        pauseB.setForeground(new Color(00,00,30));
        pauseB.setFocusPainted(false);
        pauseB.setPreferredSize(new Dimension(120,60));
        pauseB.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        pauseB.setBackground(new Color(100,100,250));
        pauseB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    sw.gpc.pause(pauseB);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.4;
        gbc.weighty = 0.1;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gamePanel.add(pauseB,gbc);

        JButton quitB = new JButton("Give Up");
        quitB.setFont(new Font("Arial", Font.PLAIN, 20));
        quitB.setForeground(new Color(00,00,30));
        quitB.setFocusPainted(false);
        quitB.setPreferredSize(new Dimension(120,60));
        quitB.setBorder(BorderFactory.createLineBorder(new Color(0,0,50), 4));
        quitB.setBackground(new Color(100,100,250));
        quitB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                sw.gpc.quit = true;
                if(!sw.gpc.lost) {
                    sw.gpc.interrupt();
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 0.4;
        gbc.weighty = 0.2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        gamePanel.add(quitB,gbc);

        return gamePanel;
    }
}
