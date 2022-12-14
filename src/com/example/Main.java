package com.example;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Main extends Base {

    HashMap<String, Boolean> ownedKeys = new HashMap<>();
    int[][] scoredGrid = new int[getGridHeight()][getGridWidth()];

    public String[][] map;
    public Integer currentPositionI = 0;
    public Integer currentPositionJ = 0;
    public Integer maxScoreOfGrid = 10000;
    public House chosenDiamond = new House();

    public Main() {
        super();
    }

    public Main(String serverIp, int serverPort) {
        super(serverIp, serverPort);
    }

    public Main(String serverIp) {
        super(serverIp);
    }

    public Main(int serverPort) {
        super(serverPort);
    }

    @Override
    public Action doTurn() {
        System.out.println("------------------------------------");
        System.out.println("---- We are at:" + currentPositionI + " : " + currentPositionJ);
        scoredGrid[currentPositionI][currentPositionJ] = scoredGrid[currentPositionI][currentPositionJ] / 4;

        if (getTurnCount() == 1) {
            map = getGrid();
            new SecondThread().start();
            ownedKeys.put("g", false);
            ownedKeys.put("r", false);
            ownedKeys.put("y", false);
        }
        for (int a = 0; a < getGridHeight(); a++) {
            for (int b = 0; b < getGridWidth(); b++) {
                System.out.print("\t" + scoredGrid[a][b]);
            }
            System.out.println();
        }
        if (chosenDiamond == null) {
            new SecondThread().start();
        } else {
            if (chosenDiamond.getPassedTurnsToFollow() > 30) {
                new SecondThread().start();
            } else {
                chosenDiamond.setPassedTurnsToFollow(chosenDiamond.getPassedTurnsToFollow() + 1);
            }
        }
        return decideAction(currentPositionI, currentPositionJ);
    }

    public static void main(String[] args) throws IOException {
        Main client = new Main("127.0.0.1", 9921);
        client.play();
    }

    public Action decideAction(Integer i, Integer j) {
        House bestHouse = new House();
        int tmp = -1;
        try {
            bestHouse.setScore(scoredGrid[i + 1][j + 1]);
            bestHouse.setAction(Action.DownRight);
            bestHouse.setI(i + 1);
            bestHouse.setJ(j + 1);
            bestHouse.setValue(map[i + 1][j + 1]);
        } catch (Exception ex) {
        }

        for (int a = i - 1; a <= i + 1; a++) {
            for (int b = j - 1; b <= j + 1; b++) {
                try {
                    tmp++;
                    if (bestHouse.getScore() < scoredGrid[a][b]) {
                        bestHouse.setI(a);
                        bestHouse.setJ(b);
                        bestHouse.setScore(scoredGrid[a][b]);
                        bestHouse.setValue(map[a][b]);
                        switch (tmp) {
                            case 0:
                                bestHouse.setAction(Action.UpLeft);
                                break;
                            case 1:
                                bestHouse.setAction(Action.Up);
                                break;
                            case 2:
                                bestHouse.setAction(Action.UpRight);
                                break;
                            case 3:
                                bestHouse.setAction(Action.Left);
                                break;
                            case 4:
                                bestHouse.setAction(Action.NoOp);
                                new SecondThread().start();
                                break;
                            case 5:
                                bestHouse.setAction(Action.Right);
                                break;
                            case 6:
                                bestHouse.setAction(Action.DownLeft);
                                break;
                            case 7:
                                bestHouse.setAction(Action.Down);
                                break;
                            case 8:
                                bestHouse.setAction(Action.DownRight);
                                break;
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }

        try {
            if (bestHouse.getScore() == 0) {
                if (i + 1 != getGridHeight() && j + 1 != getGridWidth() && !map[i - 1][j - 1].equals("W")) {
                    int a = i + 1;
                    int b = j + 1;
                    bestHouse.setI(a);
                    bestHouse.setJ(b);
                    bestHouse.setScore(scoredGrid[a][b]);
                    bestHouse.setValue(map[a][b]);
                    bestHouse.setAction(Action.DownRight);
                } else {
                    bestHouse.setI(i);
                    bestHouse.setJ(j);
                    bestHouse.setScore(scoredGrid[i][j]);
                    bestHouse.setValue(map[i][j]);
                    bestHouse.setAction(Action.NoOp);
                }
            }
        } catch (Exception ex) {
        }

        try {
            currentPositionI = bestHouse.getI();
            currentPositionJ = bestHouse.getJ();
//            scoredGrid[bestHouse.getI()][bestHouse.getJ()] = scoredGrid[bestHouse.getI()][bestHouse.getJ()] / 4;
            System.out.println(bestHouse.getAction() + " to: " + bestHouse.getI() + " : " + bestHouse.getJ() + " \n Score: " + bestHouse.score);
            if (bestHouse.getScore() == maxScoreOfGrid * 5) {
                System.out.println("*************GOT THE DIAMOND\n");
                map[bestHouse.getI()][bestHouse.getJ()] = "E";
                chosenDiamond = null;
                new SecondThread().start();
            }
            if (bestHouse.getValue().equals("1") || bestHouse.getValue().equals("2") || bestHouse.getValue().equals("3") || bestHouse.getValue().equals("4")) {
                map[bestHouse.getI()][bestHouse.getJ()] = "E";
            }

            if (map[bestHouse.getI()][bestHouse.getJ()] != "W") {
                switch (map[bestHouse.getI()][bestHouse.getJ()]) {
                    case "r":
                        ownedKeys.put("r", true);
                        new SecondThread().start();

                        break;
                    case "g":
                        ownedKeys.put("g", true);
                        new SecondThread().start();

                        break;
                    case "y":
                        ownedKeys.put("y", true);
                        new SecondThread().start();

                        break;
                }
                return bestHouse.getAction();
            } else {
                return Action.NoOp;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public Long getDistance(int ai, int aj, int bi, int bj) {
        return Math.round(Math.sqrt((ai - bi) * (ai - bi) + (aj - bj) * (aj - bj)));
    }

    public Boolean detectWall(int i, int j) {
        if (map[i][j].equals("W")) {
            return true;
        }
        return false;
    }

    public Boolean detectWire(int i, int j) {
        if (map[i][j].equals("*")) {
            return true;
        }
        return false;
    }

    public void setNeighborsScoreOfDiamond(int i, int j, int distance) {
        try {
            scoredGrid[i][j] = maxScoreOfGrid * 5;
        } catch (Exception e) {
        }
        for (int a = i - distance; a <= (i + distance); a++) {
            for (int b = j - distance; b <= (j + distance); b++) {
                try {
                    if (detectWall(a, b)) {
                        scoredGrid[a][b] = -(maxScoreOfGrid);
                    } else if (detectWire(a, b)) {
                        scoredGrid[a][b] = (maxScoreOfGrid) / 100;
                    } else if (map[i][j].equals("R")) {
                        if (ownedKeys.get("r")) {
                            map[i][j] = "E";
                            if (scoredGrid[a][b] == 0) {
                                scoredGrid[a][b] = (maxScoreOfGrid / distance) - a - b;
                            }
                        } else {
                            scoredGrid[a][b] = -(maxScoreOfGrid);
                        }
                    } else if (map[i][j].equals("Y")) {
                        if (ownedKeys.get("y")) {
                            map[i][j] = "E";
                            if (scoredGrid[a][b] == 0) {
                                scoredGrid[a][b] = (maxScoreOfGrid / distance) - a - b;
                            }
                        } else {
                            scoredGrid[a][b] = -(maxScoreOfGrid);
                        }
                    } else if (map[i][j].equals("G")) {
                        if (ownedKeys.get("g")) {
                            map[i][j] = "E";
                            if (scoredGrid[a][b] == 0) {
                                scoredGrid[a][b] = (maxScoreOfGrid / distance) - a - b;
                            }
                        } else {
                            scoredGrid[a][b] = -(maxScoreOfGrid);
                        }
                    } else {
                        if (scoredGrid[a][b] == 0) {
                            scoredGrid[a][b] = (maxScoreOfGrid / distance) - a - b;
                        }
                    }
                } catch (Exception ex) {
                    // System.out.println(ex);
                }
            }
        }
    }

    class SecondThread extends Thread {
        public void run() {
            if (getTurnCount() > 1) {
                scoredGrid = new int[getGridHeight()][getGridWidth()];
            }
            chooseDiamond(map);

//            if (chooseDiamond(map) == null) {
//                chooseDiamondWide(map);
//            }
        }

        public House chooseDiamond(String[][] map) {
            try {
                for (int i = 0; i < getGridHeight(); i++) {
                    for (int j = 0; j < getGridWidth(); j++) {
                        if (map[i][j].equals("1") || map[i][j].equals("2") || map[i][j].equals("3") || map[i][j].equals("4")) {
                            chosenDiamond = new House();
                            chosenDiamond.setI(i);
                            chosenDiamond.setJ(j);
                            System.out.println("\n++++++++New Diamond Wide:" + i + " : " + j);
                            chosenDiamond.setValue(map[i][j]);
                            Long distance = getDistance(currentPositionI, currentPositionJ, chosenDiamond.getI(), chosenDiamond.getJ()) + 4;
                            System.out.println("Distance : " + distance);

                            scoredGrid = new int[getGridHeight()][getGridWidth()];
                            for (int tmp = 0; tmp <= distance + 5; tmp++) {
                                setNeighborsScoreOfDiamond(chosenDiamond.getI(), chosenDiamond.getJ(), tmp);
                            }
                            return chosenDiamond;
                        }
                    }
                }
                return null;
            } catch (Exception ex) {
                return null;
            }
        }

        public House chooseDiamondWide(String[][] map) {
            try {
                for (int i = getGridHeight() - 1; i >= 0; i--) {
                    for (int j = getGridWidth() - 1; j >= 0; j--) {
                        if (map[i][j].equals("1") || map[i][j].equals("2") || map[i][j].equals("3") || map[i][j].equals("4")) {
                            chosenDiamond = new House();
                            chosenDiamond.setI(i);
                            chosenDiamond.setJ(j);
                            System.out.println("\n++++++++New Diamond :" + i + " : " + j);
                            chosenDiamond.setValue(map[i][j]);
                            Long distance = getDistance(currentPositionI, currentPositionJ, chosenDiamond.getI(), chosenDiamond.getJ()) + 4;
                            System.out.println("Distance : " + distance);
                            for (int tmp = 0; tmp <= distance; tmp++) {
                                setNeighborsScoreOfDiamond(chosenDiamond.getI(), chosenDiamond.getJ(), tmp);
                            }
                            return chosenDiamond;
                        }
                    }
                }
                return null;
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
