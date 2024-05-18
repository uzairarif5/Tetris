package tetris;

import java.awt.Color;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameplayCode extends Thread{
    public JPanel gamePanel;
    public JLabel[][] nextPixel;
    public char[][] gameBoard;
    public char[][] nextBoard;
    public int personalScore;
    public String otherScore;
    public String extramsg;
    public PieceControl nextPiece;
    public PieceControl curPiece;
    public boolean quit;
    public boolean lost;
    public boolean otherLost;
    public static final int totalRows = 21;
    public static final int totalCols = 10;
    public JLabel scoreb;
    public boolean sidePressed;
    public int scorePos;
    public Thread sideThread;
    public Thread turnThread;
    public Thread pauseThread;
    public Thread sendThread;
    public boolean gamePaused;
    public static boolean twoplayer = false;

    public GameplayCode(JLabel[][] pixelBoard, JPanel gamePanel){
        this.gamePanel = gamePanel;
        this.nextPixel = pixelBoard;
        this.sidePressed = false;
        this.personalScore = 0;
        this.otherScore = "0";
        this.scorePos = -1;
        this.quit = false;
        this.lost = false;
        this.otherLost = false;
        this.gamePaused = false;
        this.nextBoard  = new char[5][6];
        this.nextPiece = new PieceControl(this.nextBoard);
        this.gameBoard  = new char[GameplayCode.totalRows][GameplayCode.totalCols];
        this.extramsg = "";
        int curLine;
        for(curLine = 0; curLine< GameplayCode.totalRows; curLine++) {
            Arrays.fill(this.gameBoard[curLine], 'E');
        }
		/*
	    InputMap im = gamePanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
	    ActionMap am = gamePanel.getActionMap();
	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,0,false), "q pressed");
	    am.put("q pressed", new AbstractAction(){
	    	public void actionPerformed(ActionEvent e){
		    	try {
					thisobj.gpc.moveSide(4);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	    	}
	    });*/
    }

    public void moveSide(int num) throws Exception {
        if(this.sidePressed == false && this.gamePaused == false && this.curPiece.settled == false) {
            GameplayCode gpc = this;
            this.sidePressed = true;
            this.sideThread = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(100);
                        if(gpc.curPiece.settled == false) {
                            gpc.curPiece.pieceMovement(gpc.gameBoard,num);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            this.interrupt();
            this.sideThread.start();
            this.sideThread.join();
            this.sidePressed = false;
            synchronized(this) {
                this.notifyAll();
            }
        }
    }

    public void rotate(int num) throws Exception {
        if(this.sidePressed == false && this.gamePaused == false && this.curPiece.thisPiece != 'P' && this.curPiece.settled == false) {
            GameplayCode gpc = this;
            this.sidePressed = true;
            this.turnThread = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(100);
                        gpc.curPiece.pieceMovement(gpc.gameBoard, num);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            this.interrupt();
            this.turnThread.start();
            this.turnThread.join();
            this.sidePressed = false;
            synchronized(this) {
                this.notifyAll();
            }
        }
    }

    public void pause(JButton button) throws InterruptedException {
        if(this.gamePaused==false) {
            this.gamePaused = true;
            button.setText("Play");
            if(!this.lost) {
                this.interrupt();
            }
        }
        else {
            this.gamePaused = false;
            button.setText("Pause");
            if(!this.lost) {
                synchronized(this) {
                    this.notifyAll();
                }
            }
        }
    }

    public void run() {
        try {
            boolean notReachedAtBottom;
            Thread.sleep(1500);
            while(true) {
                notReachedAtBottom = true;
                this.curPiece = this.nextPiece;
                this.nextPiece = new PieceControl(this.nextBoard);
                this.curPiece.pieceMovement(this.gameBoard,0);
                for(int curPixelR = 0;curPixelR<5;curPixelR++) {
                    for(int curPixelC = 0;curPixelC<5;curPixelC++) {
                        switch (this.nextBoard[curPixelR][curPixelC]) {
                            case 'E':
                                this.nextPixel[curPixelR][curPixelC].setBackground(Color.gray);
                                break;
                            case 'x':
                                this.nextPixel[curPixelR][curPixelC].setBackground(Color.black);
                                break;
                            default:
                                this.nextPixel[curPixelR][curPixelC].setBackground(PieceControl.pieceToColorList.get(this.nextBoard[curPixelR][curPixelC]));
                        }
                    }
                }
                while(notReachedAtBottom) {
                    try {
                        Thread.sleep(200);
                        this.curPiece.pieceMovement(this.gameBoard,1);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        if(e instanceof InterruptedException) {
                            if(this.quit == true) {
                                throw new InterruptedException();
                            }
                            synchronized(this) {
                                this.wait();
                            }
                        }
                        else {
                            try {
                                this.curPiece.settled = true;
                                for(int j:this.curPiece.allRows.keySet()) {
                                    this.scorePos = j+this.curPiece.pLine;
                                    if(this.scorePos>=0&&this.scorePos< GameplayCode.totalRows){
                                        for(int i = 0;i<gameBoard[0].length;i++) {
                                            if(gameBoard[this.scorePos][i] == 'E') {
                                                this.scorePos = -1;
                                                break;
                                            }
                                        }
                                        if (this.scorePos != -1) {
                                            this.personalScore++;
                                            Arrays.fill(gameBoard[this.scorePos], 'x');
                                            Thread.sleep(300);
                                            for(int i = this.scorePos;i>2;i--) {
                                                this.gameBoard[i] = this.gameBoard[i-1].clone();
                                            }
                                            this.scorePos = -1;
                                            scoreb.setText(" Score: " + this.personalScore);
                                            Thread.sleep(100);
                                        }
                                    }
                                }
                                notReachedAtBottom = false;
                                Thread.sleep(500);
                            }
                            catch(InterruptedException ie) {
                                if(this.quit == true) {
                                    throw new Exception();
                                }
                                synchronized(this) {
                                    this.wait();
                                }
                            }
                        }
                    }
                }
            }
        }
        catch(Exception e){
            if(GameplayCode.twoplayer) {
                this.lost = true;
                if(!this.otherLost) {
                    try {
                        synchronized(this) {
                            this.wait();
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                if(this.extramsg == "") {
                    this.extramsg = "Opponent Score: " + this.otherScore+"\n";
                }
            }
            e.printStackTrace();
        }
    }

    public String playGame(JLabel score) throws Exception {
        this.scoreb = score;
        this.start();
        this.join();
        if(GameplayCode.twoplayer == false) {
            return ("Your Score: " + this.personalScore);
        }
        else {
            return (this.extramsg + "Your Score: " + this.personalScore);
        }
    }

}
