import java.util.Scanner;
import java.util.Random;

public class Main {

    static final int NUMBER_ROW = 3;
    static final int NUMBER_COL = 3;
    static final int NUMBER_TO_WIN = 3;

    static final char DOT_X = 'X';
    static final char DOT_O = 'O';
    static final char DOT_EMPTY = ' ';
    static final char[] DOTS = {DOT_X, DOT_O};

    static final String LINE_DELIMITER = "+---" ;

    static final int CHECK_LEFT = 0;
    static final int CHECK_RIGHT = 1;
    static final int CHECK_UP = 2;
    static final int CHECK_DOWN = 3;
    static final int CHECK_RIGHT_DOWN = 4;
    static final int CHECK_LEFT_DOWN = 5;
    static final int CHECK_LEFT_UP = 6;
    static final int CHECK_RIGHT_UP = 7;

    static final int CHECK_SIDE_COUNT = 8;

    static final int[] DELTA_ROW = {0, 0, -1, 1, 1, 1, -1, -1};
    static final int[] DELTA_COL = {-1, 1, 0, 0, 1, -1, -1, 1};

    public static char[][] gameMap;
    public static Scanner sc = new Scanner(System.in);
    public static Random randDigit = new Random();
    public static int freeCellCount = 0;
    public static int humanTurnOrder;

    public static void main(String[] args) {
        createMap();
        freeCellCount = NUMBER_COL * NUMBER_ROW;
        int turnType = getHumanTurnOrder();
        humanTurnOrder = turnType;
        if (humanTurnOrder == 0){
            printMap();
        }
        do {
            nextTurn(turnType);
            turnType = 1 - turnType;
            printMap();
        } while ((hasWinner(NUMBER_TO_WIN, DOT_EMPTY) == -1) && hasEmptyCell());
        if (!hasEmptyCell()){
            System.out.println("It's a draw");
        }
        else{
            if (turnType == 1){
                System.out.println("You win :(");
            }
            else{
                System.out.println("Computer wins!");
            }
        }
    }

    public static boolean hasEmptyCell(){
        return freeCellCount > 0;
    }

    public static int getHumanTurnOrder(){
        if (randDigit.nextInt(10) < 5){
            return 0; // human's first turn
        }
        else{
            return 1; // computer's first turn
        }
    }

    public static void createMap(){
        gameMap = new char[NUMBER_ROW][NUMBER_COL];
        for (int i = 0; i < NUMBER_ROW; i++) {
            for (int j = 0; j < NUMBER_COL; j++){
                gameMap[i][j] = DOT_EMPTY;
            }
        }
    }

    public static void printDelimeterLine(){
        for (int i = 0; i < NUMBER_COL; i++) {
            System.out.print(LINE_DELIMITER);
        }
        System.out.println("+");
    }

    public static void printCoordinateX(){
        for (int i = 0; i < NUMBER_COL; i++) {
            System.out.printf("  %d ", (i+1));
        }
        System.out.println("");
    }

    public static void printMap(){
        try{
            printCoordinateX();
            for (int i = 0; i < NUMBER_ROW; i++) {
                printDelimeterLine();
                for (int j = 0; j < NUMBER_COL; j++) {
                    System.out.printf("| %s ", gameMap[i][j]);
                }
                System.out.printf("| %d\n", i+1);
            }
            printDelimeterLine();
        } catch (Exception e){
            System.out.println("Error printing map!");
        }
    }

    public static boolean isCellValid(int row, int col){
        return (row >= 0) && (row < NUMBER_ROW) && (col >= 0) && (col < NUMBER_COL) && (gameMap[row][col] == DOT_EMPTY);
    }

    public static void printCellErrorMessage(int row, int col){
        if (row < 0 || col < 0){
            System.out.println("You should set positive cell number.");
        }
        if (row >= NUMBER_ROW || col >= NUMBER_COL) {
            System.out.println("Row or column number is bigger than the game field.");
        }
        if ((row >= 0) && (col >= 0) && (row < NUMBER_ROW) && (col < NUMBER_COL) && gameMap[row][col] != DOT_EMPTY) {
            System.out.printf("The cell %d %d not is empty.\n", row + 1, col + 1);
        }
    }

    public static void turnHuman(int turnOrder){
        int x, y;
        do {
            System.out.println("Please, enter row and column:");
            x = sc.nextInt() - 1;
            y = sc.nextInt() - 1;
            if (!isCellValid(x, y)){
                printCellErrorMessage(x, y);
            }
        } while (!isCellValid(x, y));
        gameMap[x][y] = DOTS[humanTurnOrder];
    }

    public static void turnComputer(int turnOrder){
        int x, y;
        int winStrategy;
        boolean needStop = false;
        if ((humanTurnOrder == 1) && (gameMap[0][0] == DOT_EMPTY)){
            gameMap[0][0] = DOTS[1 - humanTurnOrder];
            needStop = true;
        }
        if (!needStop) {
            // check, could computer win
            winStrategy = hasWinner(NUMBER_TO_WIN - 1, DOTS[1 - humanTurnOrder]);
            if (winStrategy != -1) {
                for (int i = 0; i < NUMBER_ROW; i++) {
                    if (needStop) {
                        break;
                    }
                    for (int j = 0; j < NUMBER_COL; j++) {
                        if (gameMap[i][j] == DOT_EMPTY) {
                            gameMap[i][j] = DOTS[1 - humanTurnOrder];
                            if (hasWinner(NUMBER_TO_WIN, DOTS[1 - humanTurnOrder]) == -1) {
                                gameMap[i][j] = DOT_EMPTY;
                            } else {
                                needStop = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (!needStop) {
            // check, could human win
            winStrategy = hasWinner(NUMBER_TO_WIN - 1, DOTS[humanTurnOrder]);
            if (winStrategy != -1) {
                for (int i = 0; i < NUMBER_ROW; i++) {
                    if (needStop) {
                        break;
                    }
                    for (int j = 0; j < NUMBER_COL; j++) {
                        if (gameMap[i][j] == DOT_EMPTY) {
                            gameMap[i][j] = DOTS[humanTurnOrder];
                            if (hasWinner(NUMBER_TO_WIN, DOTS[humanTurnOrder]) == -1) {
                                gameMap[i][j] = DOT_EMPTY;
                            } else {
                                gameMap[i][j] = DOTS[1 - humanTurnOrder];
                                needStop = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (!needStop) {
            // random turn
            do {
                x = randDigit.nextInt(NUMBER_ROW);
                y = randDigit.nextInt(NUMBER_COL);
            } while (!isCellValid(x, y));
            gameMap[x][y] = DOTS[1 - humanTurnOrder];
        }
    }

    public static void nextTurn(int turnType){
        if (turnType == 0){
            turnHuman(0);
        }
        else{
            turnComputer(1);
        }
        freeCellCount--;
    }

    public static boolean canCheckSide(int direction, int position, int maxValue, int checkCellsCount){
        boolean canCheck = true;
        switch (direction){
            case (-1) : canCheck &= (position - checkCellsCount + 1) >= 0; break;
            case (1) : canCheck &= (position + checkCellsCount - 1) < maxValue; break;
        }
        return canCheck;
    }

    public static boolean canCheckDirection(int direction, int posRow, int posCol, int checkCellsCount){
        boolean canCheck = true;
        switch (direction){
            case (CHECK_LEFT) : canCheck &= canCheckSide(DELTA_COL[CHECK_LEFT], posCol, NUMBER_COL, checkCellsCount); break;
            case (CHECK_RIGHT) : canCheck &= canCheckSide(DELTA_COL[CHECK_RIGHT], posCol, NUMBER_COL, checkCellsCount); break;
            case (CHECK_UP) : canCheck &= canCheckSide(DELTA_ROW[CHECK_UP], posRow, NUMBER_ROW, checkCellsCount); break;
            case (CHECK_DOWN) : canCheck &= canCheckSide(DELTA_ROW[CHECK_DOWN], posRow, NUMBER_ROW, checkCellsCount); break;
            case (CHECK_RIGHT_DOWN) : canCheck &= (canCheckSide(DELTA_ROW[CHECK_RIGHT_DOWN], posRow, NUMBER_ROW, checkCellsCount) && canCheckSide(DELTA_COL[CHECK_RIGHT_DOWN], posCol, NUMBER_COL, checkCellsCount)); break;
            case (CHECK_LEFT_DOWN) : canCheck &= (canCheckSide(DELTA_ROW[CHECK_LEFT_DOWN], posRow, NUMBER_ROW, checkCellsCount) && canCheckSide(DELTA_COL[CHECK_LEFT_DOWN], posCol, NUMBER_COL, checkCellsCount)); break;
            case (CHECK_LEFT_UP) : canCheck &= (canCheckSide(DELTA_ROW[CHECK_LEFT_UP], posRow, NUMBER_ROW, checkCellsCount) && canCheckSide(DELTA_COL[CHECK_LEFT_UP], posCol, NUMBER_COL, checkCellsCount)); break;
            case (CHECK_RIGHT_UP) : canCheck &= (canCheckSide(DELTA_ROW[CHECK_RIGHT_UP], posRow, NUMBER_ROW, checkCellsCount) && canCheckSide(DELTA_COL[CHECK_RIGHT_UP], posCol, NUMBER_COL, checkCellsCount)); break;
        }
        return canCheck;
    }

    public static int hasWinner(int checkCellsCount, char checkDot){
        char curCell;
        boolean[] winDirection = new boolean[8];
        int winnerSide = -1;

        // for every cell
        for (int row = 0; row < NUMBER_ROW; row++) {
            if (winnerSide != -1){
                break;
            }
            for (int col = 0; col < NUMBER_COL; col++) {
                curCell = gameMap[row][col];
                if (checkDot != DOT_EMPTY){
                    if (curCell != checkDot){
                        break;
                    }
                }
                if (winnerSide != -1){
                    break;
                }
                if (gameMap[row][col] != DOT_EMPTY){
                    // clear win cells
                    for (int x = 0; x < 8; x++) {
                         winDirection[x] = true;
                    }

                    for (int x = 0; x < checkCellsCount; x++) {
                        for (int direction = 0; direction < CHECK_SIDE_COUNT; direction++) {
                            if (winDirection[direction]) {
                                if (canCheckDirection(direction, row, col, checkCellsCount)) {
                                    winDirection[direction] &= gameMap[row + DELTA_ROW[direction] * x][col + DELTA_COL[direction] * x] == curCell;
                                } else {
                                    winDirection[direction] = false;
                                }
                            }
                        }
                    }
                    for (int x = 0; x < CHECK_SIDE_COUNT; x++){
                        if (winDirection[x]){
                            winnerSide = x;
                        }
                    }
                }
            }
        }
        return winnerSide;
    }
}