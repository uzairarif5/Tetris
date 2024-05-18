package tetris;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.TreeMap;

public class PieceControl {
    public static char[] allPieces = new char[]{'P','J','L','Z','|'};
    public int[][] inside;
    public int[][] abovesides;
    public int[][] leftsides;
    public int[][] belowsides;
    public int[][] rightsides;
    public int[][][] allsides;
    public int[] posForNext;
    public static Hashtable<Character,Color> pieceToColorList = new Hashtable<Character,Color>();
    public TreeMap<Integer,Integer> allRows;
    public int orientation;
    public char thisPiece;
    public char littleLetter;
    public boolean settled;
    public int pLine;
    public int pPos;

    public PieceControl(char[][] nextBoard) {
        Random rand = new Random();
        this.settled = false;
        this.allsides = new int[][][] {abovesides,leftsides,belowsides,rightsides};
        this.thisPiece = PieceControl.allPieces[rand.nextInt(PieceControl.allPieces.length)];
        this.orientation = 1;
        this.pLine = 2;
        this.pPos = 5;
        switch(this.thisPiece) {
            case 'P':
                this.littleLetter = 'p';
                this.posForNext = new int[] {2,2};
                this.inside = new int[][] {{1,0},{0,1},{0,0},{0,-1},{-1,0}};
                break;
            case 'J':
                this.littleLetter = 'j';
                this.posForNext = new int[] {3,2};
                this.inside = new int[][] {{0,-1},{0,0},{-1,0},{-2,0}};
                break;
            case 'L':
                this.littleLetter = 'l';
                this.posForNext = new int[] {3,2};
                this.inside = new int[][] {{0,1},{0,0},{-1,0},{-2,0}};
                break;
            case 'Z':
                this.littleLetter = 'z';
                this.posForNext = new int[] {2,2};
                this.inside = new int[][] {{0,1},{0,0},{-1,0},{-1,-1}};
                break;
            case '|':
                this.littleLetter = 'i';
                this.posForNext = new int[] {2,2};
                this.inside = new int[][] {{1,0},{0,0},{-1,0},{-2,0}};
                break;
        }
        PieceControl.pieceToColorList.put('P', Color.pink);
        PieceControl.pieceToColorList.put('p', Color.magenta);
        PieceControl.pieceToColorList.put('J', new Color(100,40,200));
        PieceControl.pieceToColorList.put('j', new Color(85,5,145));
        PieceControl.pieceToColorList.put('L', Color.green);
        PieceControl.pieceToColorList.put('l', new Color(20,145,0));
        PieceControl.pieceToColorList.put('Z', Color.red);
        PieceControl.pieceToColorList.put('z', new Color(180,18,8));
        PieceControl.pieceToColorList.put('|', Color.yellow);
        PieceControl.pieceToColorList.put('i', Color.orange);
        calculateSides();
        for(int curLine = 0;curLine<5;curLine++) {
            Arrays.fill(nextBoard[curLine], 'E');
        }
        for(int[] coords: this.inside) {
            nextBoard[this.posForNext[0]+coords[0]][this.posForNext[1]+coords[1]] = this.littleLetter;
        }
        nextBoard[this.posForNext[0]][this.posForNext[1]] = this.thisPiece;
    }

    //findTheSides
    public void calculateSides() {
        this.allRows = new TreeMap<Integer,Integer>();
        TreeMap<Integer,Integer> colToRow = new TreeMap<Integer,Integer>();
        ArrayList<int[]> toAdd = new ArrayList<int[]>();
        ArrayList<int[]> toAdd2 = new ArrayList<int[]>();
        Integer loopVar;
        for(loopVar=0;loopVar<this.inside.length;loopVar++) {
            this.allRows.put(this.inside[loopVar][0],this.inside[loopVar][1]);
            colToRow.put(this.inside[loopVar][1],this.inside[loopVar][0]);
        }

        for(Integer col: colToRow.keySet()) {
            int smallestRow = 4;
            int largestRow = -4;
            for(loopVar=0;loopVar<this.inside.length;loopVar++) {
                if(col == this.inside[loopVar][1] && this.inside[loopVar][0]-1<smallestRow) {
                    smallestRow = this.inside[loopVar][0]-1;
                }
                if(col == this.inside[loopVar][1] && this.inside[loopVar][0]+1>largestRow) {
                    largestRow = this.inside[loopVar][0]+1;
                }
            }
            toAdd.add(new int[]{smallestRow, col});
            toAdd2.add(new int[]{largestRow, col});
        }
        this.abovesides = new int[toAdd.size()][2];
        this.belowsides = new int[toAdd2.size()][2];
        for(loopVar=0;loopVar<toAdd.size();loopVar++) {
            this.abovesides[loopVar] = toAdd.get(loopVar);
            this.belowsides[loopVar] = toAdd2.get(loopVar);
        }

        toAdd.clear();
        toAdd2.clear();
        for(Integer row: this.allRows.keySet()) {
            int largestCol = -4;
            int smallestCol = 4;
            for(loopVar=0;loopVar<this.inside.length;loopVar++) {
                if(row == this.inside[loopVar][0] && this.inside[loopVar][1]+1>largestCol) {
                    largestCol = this.inside[loopVar][1]+1;
                }
                if(row == this.inside[loopVar][0] && this.inside[loopVar][1]-1<smallestCol) {
                    smallestCol = this.inside[loopVar][1]-1;
                }
            }
            toAdd.add(new int[]{row, largestCol});
            toAdd2.add(new int[]{row, smallestCol});
        }
        this.rightsides = new int[toAdd.size()][2];
        this.leftsides = new int[toAdd2.size()][2];
        for(loopVar=0;loopVar<toAdd.size();loopVar++) {
            this.rightsides[loopVar] = toAdd.get(loopVar);
            this.leftsides[loopVar] = toAdd2.get(loopVar);
        }
    }

    //new moveDown
    public void moveDown(char[][] gameBoard) throws Exception {
        //check if below is clear
        for(int[] i:this.belowsides) {
            try {
                if(gameBoard[this.pLine+i[0]][this.pPos+i[1]] != 'E') {
                    throw new Exception("no space below");
                }
            }
            catch(Exception e) {
                throw new Exception("no space below");
            }
        }

        //drop down
        this.pLine++;
        for (int[] i: this.inside) {
            gameBoard[this.pLine+i[0]][this.pPos+i[1]] = this.littleLetter;
        }
        for(int[] i:this.abovesides) {
            gameBoard[this.pLine+i[0]][this.pPos+i[1]] = 'E';
        }
        gameBoard[this.pLine][this.pPos] = this.thisPiece;
    }

    public void moveLeft(char[][] gameBoard) throws Exception {

        for(int[] i:this.leftsides) {
            try {
                if(gameBoard[this.pLine+i[0]][this.pPos+i[1]] != 'E') {
                    throw new Exception("no space left");
                }
            }
            catch(Exception e) {
                throw new Exception("no space left");
            }
        }

        this.pPos--;
        for(int[] i:this.rightsides) {
            gameBoard[this.pLine+i[0]][this.pPos+i[1]] = 'E';
        }
        for (int[] i: this.inside) {
            gameBoard[this.pLine+i[0]][this.pPos+i[1]] = this.littleLetter;
        }
        gameBoard[this.pLine][this.pPos] = this.thisPiece;
    }

    public void moveRight(char[][] gameBoard) throws Exception {

        for(int[] i:this.rightsides) {
            try {
                if(gameBoard[this.pLine+i[0]][this.pPos+i[1]] != 'E') {
                    throw new Exception("no space right");
                }
            }
            catch(Exception e) {
                throw new Exception("no space right");
            }
        }

        this.pPos++;
        for(int[] i:this.leftsides) {
            gameBoard[this.pLine+i[0]][this.pPos+i[1]] = 'E';
        }
        for (int[] i: this.inside) {
            gameBoard[this.pLine+i[0]][this.pPos+i[1]] = this.littleLetter;
        }
        gameBoard[this.pLine][this.pPos] = this.thisPiece;
    }

    public void rotate(char[][] gameBoard,int num) throws Exception {
        int[][] tempArray = new int[this.inside.length][2];
        if (num ==4) {
            for(int i=0;i<this.inside.length;i++) {
                int tempNum = this.inside[i][0];
                tempArray[i][0] = this.inside[i][1]*-1;
                tempArray[i][1] = tempNum;
            }
        }
        else {
            for(int i=0;i<this.inside.length;i++) {
                int tempNum = this.inside[i][0];
                tempArray[i][0] = this.inside[i][1];
                tempArray[i][1] = tempNum*-1;
            }
        }
        try {
            boolean replace;
            boolean replace2 = true;
            for (int[] i: tempArray) {
                replace = true;
                for (int[] j: this.inside) {
                    if(this.pLine+j[0] == this.pLine+i[0] && this.pPos+j[1] == this.pPos+i[1]) {
                        replace = false;
                    }
                }
                if(replace == true) {
                    if(gameBoard[this.pLine+i[0]][this.pPos+i[1]] != 'E') {
                        replace2 = false;
                        break;
                    }
                }
            }
            if(replace2==true) {
                if(this.orientation==5) {
                    this.orientation = 1;
                }
                else if(this.orientation==0) {
                    this.orientation = 4;
                }
                for (int[] i: this.inside) {
                    gameBoard[this.pLine+i[0]][this.pPos+i[1]] = 'E';
                }
                this.inside = tempArray.clone();
                for (int[] i: this.inside) {
                    gameBoard[this.pLine+i[0]][this.pPos+i[1]] = this.littleLetter;
                }
                gameBoard[this.pLine][this.pPos] = this.thisPiece;
                calculateSides();
            }
        }
        catch(Exception e) {
            throw new Exception("cannot rotate");
        }
    }

    //new overall Control
    public void pieceMovement(char[][] gameBoard,int movement) throws Exception {
        switch(movement) {
            case 0:
                this.pLine = 2;
                this.pPos = 5;
                for(int[] i:this.belowsides) {
                    if(gameBoard[this.pLine+i[0]][this.pPos+i[1]] != 'E') {
                        throw new Exception("you lose!");
                    }
                }
                break;
            case 1:
                this.moveDown(gameBoard);
                break;
            case 2:
                this.moveLeft(gameBoard);
                break;
            case 3:
                this.moveRight(gameBoard);
                break;
            case 4:
                this.orientation--;
                rotate(gameBoard,4);
                break;
            case 5:
                this.orientation++;
                rotate(gameBoard,5);
                break;
        }
    }

}
