package com.example;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;


public class Main extends Base {

    HashMap<String, Boolean> ownedKeys = new HashMap<>();
    int[][] scoredGrid = new int[getGridHeight()][getGridWidth()];

    public String[][] map;
    public String[][] oldMap;
    public Integer currentPositionI = 0;
    public Integer currentPositionJ = 0;
    public Integer maxScoreOfGrid = 10000;
    public House chosenDiamond = new House();
    ArrayList<House> diamonds = new ArrayList<>();

    public Double minimumExpectedValue = 1d;

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
        oldMap = map;
        map = getGrid();
        scoredGrid[currentPositionI][currentPositionJ] = scoredGrid[currentPositionI][currentPositionJ] / 4;
        System.out.println("\t------------------------------------");

        if (getTurnCount() == 1) {
            new SecondThread().start();
            new GetAllDiamondsPosition().start();
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
                removeDiamondFromList(chosenDiamond);
                diamonds.add(chosenDiamond);
                new SecondThread().start();
            } else {
                chosenDiamond.setPassedTurnsToFollow(chosenDiamond.getPassedTurnsToFollow() + 1);
            }
        }
        updatePosition();
        updateKeys();
        updateDiamondsArchive();
        try {
            Thread.sleep(600);
        } catch (Exception ex) {

        }
        return decideAction(currentPositionI, currentPositionJ);
    }

    public static void main(String[] args) throws IOException {
        Main client = new Main("127.0.0.1", 9921);
        client.play();
    }

    public void updateDiamondsArchive() {
        try {
            if (oldMap[currentPositionI][currentPositionJ].charAt(0) == '1' || oldMap[currentPositionI][currentPositionJ].charAt(0) == '2' || oldMap[currentPositionI][currentPositionJ].charAt(0) == '3' || oldMap[currentPositionI][currentPositionJ].charAt(0) == '4') {
                System.out.println("*************GOT THE DIAMOND\n");
                House receivedDiamond = new House();
                receivedDiamond.setI(currentPositionI);
                receivedDiamond.setJ(currentPositionJ);
                removeDiamondFromList(receivedDiamond);
                chosenDiamond = null;
                new SecondThread().start();
            }
        } catch (Exception ex) {
        }
    }

    public void updateKeys() {
        try {
            switch (oldMap[currentPositionI][currentPositionJ].charAt(0)) {
                case 'r':
                    ownedKeys.put("r", true);
                    new SecondThread().start();
                    break;
                case 'g':
                    ownedKeys.put("g", true);
                    new SecondThread().start();
                    break;
                case 'y':
                    ownedKeys.put("y", true);
                    new SecondThread().start();
                    break;
            }
        } catch (Exception e) {
        }
    }

    public Action decideAction(Integer i, Integer j) {
        House bestHouse = new House();
        int tmp = -1;
        bestHouse.setScore(0);

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
            System.out.println(bestHouse.getAction() + " to: " + bestHouse.getI() + " : " + bestHouse.getJ() + " \n Score: " + bestHouse.getScore());
        } catch (Exception ex) {
        }

        try {
            if (map[bestHouse.getI()][bestHouse.getJ()] != "W") {
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
        if (map[i][j].charAt(0) == 'W') {
            return true;
        }
        return false;
    }

    public Boolean detectWire(int i, int j) {
        if (map[i][j].charAt(0) == '*') {
            return true;
        }
        return false;
    }

    public Boolean detectTeleport(int i, int j) {
        if (map[i][j].equals("T")) {
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
                    } else if (detectTeleport(a, b)) {
                        scoredGrid[a][b] = (maxScoreOfGrid) / 10;
                    } else if (map[a][b].charAt(0) == 'R') {
                        if (ownedKeys.get("r")) {
                            scoredGrid[a][b] = (maxScoreOfGrid / distance) - a - b;
                        } else {
                            scoredGrid[a][b] = -(maxScoreOfGrid);
                        }
                    } else if (map[a][b].charAt(0) == 'Y') {
                        if (ownedKeys.get("y")) {
                            scoredGrid[a][b] = (maxScoreOfGrid / distance) - a - b;
                        } else {
                            scoredGrid[a][b] = -(maxScoreOfGrid);
                        }
                    } else if (map[a][b].charAt(0) == 'G') {
                        if (ownedKeys.get("g")) {
                            scoredGrid[a][b] = (maxScoreOfGrid / distance) - a - b;
                        } else {
                            scoredGrid[a][b] = -(maxScoreOfGrid);
                        }
                    } else {
                        if (scoredGrid[a][b] == 0) {
                            scoredGrid[a][b] = (maxScoreOfGrid / distance) - a - b;
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }
        scoredGrid[0][0] = scoredGrid[0][0] / 4;

    }

    public void updatePosition() {
        try {
            for (int i = 0; i < getGridHeight(); i++) {
                for (int j = 0; j < getGridWidth(); j++) {
                    if (map[i][j].substring(1).equals("A")) {
                        currentPositionI = i;
                        currentPositionJ = j;
                        scoredGrid[currentPositionI][currentPositionJ] = scoredGrid[currentPositionI][currentPositionJ] / 4;
                        System.out.println("---- We are at:" + currentPositionI + " : " + currentPositionJ);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public House removeDiamondFromList(House diamond) {
        for (int count = 0; count < diamonds.size(); count++) {
            House house = diamonds.get(count);
            if (house.getI() == diamond.getI() && house.getJ() == diamond.getJ()) {
                diamonds.remove(count);
                return house;
            }
        }
        return null;
    }

    class SecondThread extends Thread {
        public void run() {
            if (getTurnCount() > 1) {
                scoredGrid = new int[getGridHeight()][getGridWidth()];
            }
            chooseDiamond(getGrid());
            new Expectation().start();
        }

        public House chooseDiamond(String[][] map) {
            if (diamonds.isEmpty()) {
                try {
                    for (int i = 0; i < getGridHeight(); i++) {
                        for (int j = 0; j < getGridWidth(); j++) {
                            if (map[i][j].charAt(0) == '1' || map[i][j].charAt(0) == '2' || map[i][j].charAt(0) == '3' || map[i][j].charAt(0) == '4') {
                                chosenDiamond = new House();
                                chosenDiamond.setI(i);
                                chosenDiamond.setJ(j);
                                chosenDiamond.setValue(map[i][j]);
                                Long distance = getDistance(currentPositionI, currentPositionJ, chosenDiamond.getI(), chosenDiamond.getJ()) + 4;
                                System.out.println("++++++++Chose new diamond  at: " + i + " : " + j);

                                scoredGrid = new int[getGridHeight()][getGridWidth()];
                                for (int diameter = 0; diameter <= distance + 5; diameter++) {
                                    setNeighborsScoreOfDiamond(chosenDiamond.getI(), chosenDiamond.getJ(), diameter);
                                }
                                return chosenDiamond;
                            }
                        }
                    }
                    return null;
                } catch (Exception ex) {
                    return null;
                }
            } else {
                chosenDiamond = diamonds.get(0);
                Long distance = getDistance(currentPositionI, currentPositionJ, chosenDiamond.getI(), chosenDiamond.getJ()) + 4;
                System.out.println("++++++++Chose new diamond (from list) at: " + chosenDiamond.getI() + " : " + chosenDiamond.getJ());
                scoredGrid = new int[getGridHeight()][getGridWidth()];
                for (int diameter = 0; diameter <= distance + 5; diameter++) {
                    setNeighborsScoreOfDiamond(chosenDiamond.getI(), chosenDiamond.getJ(), diameter);
                }
                return chosenDiamond;
            }
        }
    }


    class Expectation extends Thread {
        public void run() {
            Double expectedValue = calculateExpectedValueFromPosition(currentPositionI, currentPositionJ);
            if (expectedValue < minimumExpectedValue) {
                minimumExpectedValue = expectedValue;
                removeDiamondFromList(chosenDiamond);
                diamonds.add(chosenDiamond);
                new SecondThread().start();
            }
        }


        private Double calculateExpectedValueFromPosition(Integer hypotheticalPositionI, Integer hypotheticalPositionJ) {
            Double expectedValue = 1d;
            Integer numberOfHouses = 1;
            for (int counter = 0; counter < 30; counter++) {
                numberOfHouses++;
                House bestHypotheticalHouse = forecastHypotheticalNextAction(hypotheticalPositionI, hypotheticalPositionJ);
                try {
                    if (bestHypotheticalHouse.getValue().charAt(0) != 'W') {
                        hypotheticalPositionI = bestHypotheticalHouse.getI();
                        hypotheticalPositionJ = bestHypotheticalHouse.getJ();
                        Character nodeValue = map[hypotheticalPositionI][hypotheticalPositionJ].charAt(0);
                        switch (nodeValue) {
                            case '*':
                                expectedValue = (expectedValue * getProbabilities().get("barbed").get(bestHypotheticalHouse.getAction().name().toUpperCase()).get(bestHypotheticalHouse.getAction().name().toUpperCase()));
                                break;
                            case 'T':
                                expectedValue = (expectedValue * getProbabilities().get("teleport").get(bestHypotheticalHouse.getAction().name().toUpperCase()).get(bestHypotheticalHouse.getAction().name().toUpperCase()));
                                break;
                            default:
                                if (nodeValue == '1' || nodeValue == '2' || nodeValue == '3' || nodeValue == 'r' || nodeValue == 'g' || nodeValue == 'y') {
                                    expectedValue = (expectedValue * getProbabilities().get("slider").get(bestHypotheticalHouse.getAction().name().toUpperCase()).get(bestHypotheticalHouse.getAction().name().toUpperCase()));
                                    if (nodeValue == '1' || nodeValue == '2' || nodeValue == '3') {
                                        continue;
                                    }
                                } else {
                                    expectedValue = (expectedValue * getProbabilities().get("normal").get(bestHypotheticalHouse.getAction().name().toUpperCase()).get(bestHypotheticalHouse.getAction().name().toUpperCase()));
                                }
                                break;
                        }
                    }
                } catch (Exception ex) {
                }
            }
            return expectedValue / numberOfHouses;
        }
    }

    private House forecastHypotheticalNextAction(int i, int j) {
        House bestHypotheticalHouse = new House();
        bestHypotheticalHouse.setScore(0);
        int tmp = -1;

        try {
            bestHypotheticalHouse.setScore(scoredGrid[i + 1][j + 1]);
            bestHypotheticalHouse.setAction(Action.DownRight);
            bestHypotheticalHouse.setI(i + 1);
            bestHypotheticalHouse.setJ(j + 1);
            bestHypotheticalHouse.setValue(map[i + 1][j + 1]);
        } catch (Exception ex) {
        }

        for (int a = i - 1; a <= i + 1; a++) {
            for (int b = j - 1; b <= j + 1; b++) {
                try {
                    tmp++;
                    if (bestHypotheticalHouse.getScore() < scoredGrid[a][b]) {
                        bestHypotheticalHouse.setI(a);
                        bestHypotheticalHouse.setJ(b);
                        bestHypotheticalHouse.setScore(scoredGrid[a][b]);
                        bestHypotheticalHouse.setValue(map[a][b]);
                        switch (tmp) {
                            case 0:
                                bestHypotheticalHouse.setAction(Action.UpLeft);
                                break;
                            case 1:
                                bestHypotheticalHouse.setAction(Action.Up);
                                break;
                            case 2:
                                bestHypotheticalHouse.setAction(Action.UpRight);
                                break;
                            case 3:
                                bestHypotheticalHouse.setAction(Action.Left);
                                break;
                            case 4:
                                bestHypotheticalHouse.setAction(Action.NoOp);
                                break;
                            case 5:
                                bestHypotheticalHouse.setAction(Action.Right);
                                break;
                            case 6:
                                bestHypotheticalHouse.setAction(Action.DownLeft);
                                break;
                            case 7:
                                bestHypotheticalHouse.setAction(Action.Down);
                                break;
                            case 8:
                                bestHypotheticalHouse.setAction(Action.DownRight);
                                break;
                        }

                    }
                } catch (Exception ex) {
                }
            }
        }
        return bestHypotheticalHouse;
    }


    class GetAllDiamondsPosition extends Thread {
        public void run() {
            diamonds = getDiamondsPosition(getGrid());
        }

        private ArrayList<House> getDiamondsPosition(String[][] map) {
            ArrayList<House> diamonds = new ArrayList<>();

            try {
                for (int i = 0; i < getGridHeight(); i++) {
                    for (int j = 0; j < getGridWidth(); j++) {
                        if (map[i][j].charAt(0) == '1' || map[i][j].charAt(0) == '2' || map[i][j].charAt(0) == '3' || map[i][j].charAt(0) == '4') {
                            House diamond = new House();
                            diamond.setI(i);
                            diamond.setJ(j);
                            diamond.setValue(map[i][j]);
                            diamonds.add(diamond);
                        }
                    }
                }
            } catch (Exception ex) {
            }
            return diamonds;
        }

    }
}
